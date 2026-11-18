package com.baomidou.plugin.idea.mybatisx.injection;

import com.baomidou.plugin.idea.mybatisx.util.DomUtils;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.Language;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiReference;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Tests language injection and XML validation rules inside MyBatis Mapper annotations
 * ({@code @Select}, {@code @Update}, {@code @Insert}, {@code @Delete}), including multiline text blocks.
 */
public class MapperAnnotationScriptInjectionTest extends LightJavaCodeInsightFixtureTestCase {

    @Override
    protected @NotNull LightProjectDescriptor getProjectDescriptor() {
        return JAVA_17;
    }

    private void addMybatisAnnotations() {
        myFixture.addClass(
            "package org.apache.ibatis.annotations;\n" +
                "public @interface Update { String[] value(); }\n"
        );
        myFixture.addClass(
            "package org.apache.ibatis.annotations;\n" +
                "public @interface Select { String[] value(); }\n"
        );
        myFixture.addClass(
            "package org.apache.ibatis.annotations;\n" +
                "public @interface Param { String value(); }\n"
        );
    }

    @Test
    public void testMultilineScriptTextBlockInjectsXmlAndValidates() {
        addMybatisAnnotations();
        myFixture.addClass(
            "package demo;\n" +
                "public class TagItem {\n" +
                "    private String epc;\n" +
                "    private boolean active;\n" +
                "    public String getEpc() { return epc; }\n" +
                "    public boolean isActive() { return active; }\n" +
                "}\n"
        );

        myFixture.configureByText("TagMapper.java",
            "package demo;\n" +
                "import org.apache.ibatis.annotations.Param;\n" +
                "import org.apache.ibatis.annotations.Update;\n" +
                "import java.util.List;\n" +
                "public interface TagMapper {\n" +
                "    @Update(\"\"\"\n" +
                "        <script>\n" +
                "        UPDATE ftw_stocktaking_result_tag\n" +
                "        SET opp_tid = CASE epc\n" +
                "        <foreach collection=\"items\" item=\"item\">\n" +
                "            <if test=\"item.active\">\n" +
                "                WHEN #{item.epc} THEN 1\n" +
                "            </if>\n" +
                "        </foreach>\n" +
                "        END\n" +
                "        WHERE task_no = #{taskNo}\n" +
                "        </script>\n" +
                "        \"\"\")\n" +
                "    int updateBatch(@Param(\"taskNo\") String taskNo, @Param(\"items\") List<TagItem> items);\n" +
                "}\n"
        );

        int offset = myFixture.getFile().getText().indexOf("collection=\"items\"") + "collection=\"".length();
        PsiElement injectedElement = InjectedLanguageManager.getInstance(getProject())
            .findInjectedElementAt(myFixture.getFile(), offset);
        assertNotNull("Expected XML language injection inside multiline <script>", injectedElement);
        assertEquals(XMLLanguage.INSTANCE, injectedElement.getContainingFile().getLanguage());
        assertTrue("Expected injected XML to be recognized as MyBatis mapper file",
            DomUtils.isMybatisFile(injectedElement.getContainingFile()));

        // Verify collection="items" resolves to @Param("items") List<TagItem> items
        PsiElement attrValue = injectedElement.getParent();
        PsiReference[] refs = attrValue.getReferences();
        assertTrue("Expected reference on collection=\"items\"", refs.length > 0);
        PsiElement resolved = refs[0].resolve();
        assertTrue("Expected collection=\"items\" to resolve to PsiParameter, got: " + resolved,
            resolved instanceof PsiParameter);
        assertEquals("items", ((PsiParameter) resolved).getName());

        // Verify no ERROR highlights (no SQL error on <script>/<foreach>, no XML validation error)
        List<HighlightInfo> errors = myFixture.doHighlighting().stream()
            .filter(info -> info.getSeverity() == HighlightSeverity.ERROR)
            .collect(Collectors.toList());
        assertTrue("Expected no errors in valid multiline <script>, but found: " + errors, errors.isEmpty());
    }

    @Test
    public void testMultilineScriptReportsXmlValidationErrors() {
        addMybatisAnnotations();
        myFixture.configureByText("BadMapper.java",
            "package demo;\n" +
                "import org.apache.ibatis.annotations.Param;\n" +
                "import org.apache.ibatis.annotations.Update;\n" +
                "public interface BadMapper {\n" +
                "    @Update(\"\"\"\n" +
                "        <script>\n" +
                "        UPDATE t SET a = 1\n" +
                "        <foreach collection=\"taskNo\" item=\"x\">\n" +
                "            <if test=\"taskNo\">AND 1=1</if>\n" +
                "        </foreach>\n" +
                "        </script>\n" +
                "        \"\"\")\n" +
                "    int bad(@Param(\"taskNo\") String taskNo);\n" +
                "}\n"
        );

        List<String> errorDescriptions = myFixture.doHighlighting().stream()
            .filter(info -> info.getSeverity() == HighlightSeverity.ERROR)
            .map(HighlightInfo::getDescription)
            .collect(Collectors.toList());

        assertTrue("Expected collection type error, got: " + errorDescriptions,
            errorDescriptions.contains("Expression must be Collection or Array (found: String)"));
        assertTrue("Expected boolean type error, got: " + errorDescriptions,
            errorDescriptions.contains("Expression must verify boolean (found: String)"));
    }

    @Test
    public void testArrayScriptInjectsXml() {
        addMybatisAnnotations();
        myFixture.configureByText("ArrayMapper.java",
            "package demo;\n" +
                "import org.apache.ibatis.annotations.Param;\n" +
                "import org.apache.ibatis.annotations.Select;\n" +
                "public interface ArrayMapper {\n" +
                "    @Select({\"<script>\", \"SELECT * FROM t\", \"<if test='ok'>WHERE 1=1</if>\", \"</script>\"})\n" +
                "    int count(@Param(\"ok\") boolean ok);\n" +
                "}\n"
        );

        int offset = myFixture.getFile().getText().indexOf("test='ok'");
        PsiElement injectedElement = InjectedLanguageManager.getInstance(getProject())
            .findInjectedElementAt(myFixture.getFile(), offset);
        assertNotNull("Expected XML injection in array @Select", injectedElement);
        assertEquals(XMLLanguage.INSTANCE, injectedElement.getContainingFile().getLanguage());
    }

    @Test
    public void testPlainSqlWithoutScriptStillInjectsSql() {
        Language sqlLang = Language.findLanguageByID("SQL");
        if (sqlLang == null) {
            return;
        }
        addMybatisAnnotations();
        PsiFile file = myFixture.configureByText("PlainSqlMapper.java",
            "package demo;\n" +
                "import org.apache.ibatis.annotations.Select;\n" +
                "public interface PlainSqlMapper {\n" +
                "    @Select(\"SELECT 1\")\n" +
                "    int selectOne();\n" +
                "}\n"
        );

        int offset = file.getText().indexOf("SELECT 1");
        PsiElement injectedElement = InjectedLanguageManager.getInstance(getProject())
            .findInjectedElementAt(file, offset);
        assertNotNull("Expected SQL injection for plain @Select", injectedElement);
        assertTrue("Expected SQL dialect language", injectedElement.getContainingFile().getLanguage().isKindOf(sqlLang));
    }

    @Test
    public void testJdbcTypeCompletionInAnnotation() {
        Language sqlLang = Language.findLanguageByID("SQL");
        if (sqlLang == null) {
            return;
        }
        addMybatisAnnotations();
        myFixture.configureByText("JdbcTypeMapper.java",
            "package demo;\n" +
                "import org.apache.ibatis.annotations.Param;\n" +
                "import org.apache.ibatis.annotations.Update;\n" +
                "public interface JdbcTypeMapper {\n" +
                "    @Update(\"\"\"\n" +
                "        UPDATE t SET status = #{succStatus,jdbcType=VA<caret>}\n" +
                "        WHERE id = #{id}\n" +
                "        \"\"\")\n" +
                "    int update(@Param(\"succStatus\") String succStatus, @Param(\"id\") Long id);\n" +
                "}\n"
        );

        myFixture.completeBasic();
        List<String> lookupStrings = myFixture.getLookupElementStrings();
        assertNotNull("Expected jdbcType completion variants", lookupStrings);
        assertTrue("Expected VARCHAR in jdbcType completion, got: " + lookupStrings,
            lookupStrings.contains("VARCHAR"));
    }

    @Test
    public void testHashMarkNavigationInPlainSqlAnnotation() {
        Language sqlLang = Language.findLanguageByID("SQL");
        if (sqlLang == null) {
            return;
        }
        addMybatisAnnotations();
        myFixture.configureByText("HashMarkNavMapper.java",
            "package demo;\n" +
                "import org.apache.ibatis.annotations.Param;\n" +
                "import org.apache.ibatis.annotations.Update;\n" +
                "public interface HashMarkNavMapper {\n" +
                "    @Update(\"\"\"\n" +
                "        UPDATE t SET updater = #{updater}\n" +
                "        WHERE task_no = #{taskNo}\n" +
                "        \"\"\")\n" +
                "    int update(@Param(\"taskNo\") String taskNo, @Param(\"updater\") String updater);\n" +
                "}\n"
        );

        int offset = myFixture.getFile().getText().indexOf("#{taskNo}") + 2;
        PsiElement injected = InjectedLanguageManager.getInstance(getProject())
            .findInjectedElementAt(myFixture.getFile(), offset);
        assertNotNull("Expected SQL element at #{taskNo}", injected);
        PsiElement[] targets = new com.baomidou.plugin.idea.mybatisx.reference.HashMarkGotoDeclarationHandler()
            .getGotoDeclarationTargets(injected, injected.getTextOffset(), myFixture.getEditor());
        assertNotNull("Expected #{taskNo} goto targets", targets);
        assertTrue("Expected #{taskNo} to resolve to mapper method", targets.length > 0);
    }

    @Test
    public void testParameterFindUsagesIncludesHashMarkInAnnotation() {
        addMybatisAnnotations();
        PsiFile file = myFixture.configureByText("SummaryMapper.java",
            "package demo;\n" +
                "import org.apache.ibatis.annotations.Param;\n" +
                "import org.apache.ibatis.annotations.Select;\n" +
                "public interface SummaryMapper {\n" +
                "    @Select(\"\"\"\n" +
                "        <script>\n" +
                "        SELECT * FROM ftw_store_order_tags\n" +
                "        WHERE deleted = 0\n" +
                "        <if test=\"boxNo != null and boxNo != ''\">\n" +
                "            AND box_no = #{boxNo}\n" +
                "        </if>\n" +
                "        </script>\n" +
                "        \"\"\")\n" +
                "    int count(@Param(\"orderNo\") String orderNo, @Param(\"boxNo\") String boxNo);\n" +
                "}\n"
        );

        com.intellij.psi.PsiClass mapperClass = myFixture.findClass("demo.SummaryMapper");
        PsiParameter boxNoParam = mapperClass.getMethods()[0].getParameterList().getParameters()[1];
        java.util.Collection<PsiReference> usages =
            com.intellij.psi.search.searches.ReferencesSearch.search(boxNoParam).findAll();

        boolean hasTestRef = false;
        boolean hasHashMarkRef = false;
        for (PsiReference usage : usages) {
            String hostText = usage.getElement().getText();
            if (hostText.contains("boxNo != null")) {
                hasTestRef = true;
            }
            if (hostText.contains("#{boxNo}") && usage.getRangeInElement().substring(hostText).equals("boxNo")) {
                hasHashMarkRef = true;
            }
        }
        assertTrue("Expected usage in <if test>, usages=" + usages, hasTestRef);
        assertTrue("Expected usage in #{boxNo}, usages=" + usages, hasHashMarkRef);
    }

    @Test
    public void testEmptyHashMarkCompletionInScript() {
        addMybatisAnnotations();
        myFixture.configureByText("CompletionScriptMapper.java",
            "package demo;\n" +
                "import org.apache.ibatis.annotations.Param;\n" +
                "import org.apache.ibatis.annotations.Select;\n" +
                "public interface CompletionScriptMapper {\n" +
                "    @Select(\"\"\"\n" +
                "        <script>\n" +
                "        SELECT * FROM ftw_store_order_tags\n" +
                "        <if test=\"boxNo != null\">\n" +
                "            AND box_no = #{<caret>}\n" +
                "        </if>\n" +
                "        </script>\n" +
                "        \"\"\")\n" +
                "    int count(@Param(\"orderNo\") String orderNo, @Param(\"boxNo\") String boxNo);\n" +
                "}\n"
        );

        myFixture.completeBasic();
        List<String> lookupStrings = myFixture.getLookupElementStrings();
        assertNotNull("Expected parameter completion inside empty #{}", lookupStrings);
        assertTrue("Expected boxNo in completion, got: " + lookupStrings, lookupStrings.contains("boxNo"));
        assertTrue("Expected orderNo in completion, got: " + lookupStrings, lookupStrings.contains("orderNo"));
    }
}
