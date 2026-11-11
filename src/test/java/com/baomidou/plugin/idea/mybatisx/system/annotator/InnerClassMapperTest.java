package com.baomidou.plugin.idea.mybatisx.system.annotator;

import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.model.MapperNamespace;
import com.baomidou.plugin.idea.mybatisx.model.MapperStatementId;
import com.baomidou.plugin.idea.mybatisx.service.JavaService;
import com.baomidou.plugin.idea.mybatisx.util.JavaUtils;
import com.baomidou.plugin.idea.mybatisx.util.MapperUtils;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import com.intellij.util.xml.DomElement;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InnerClassMapperTest extends LightJavaCodeInsightFixtureTestCase {

    @Test
    public void testInnerClassMapperWithDollarNamespace() {
        myFixture.configureByText("MapperDao.java",
            "package com.baomidou.mybatis3.mapper;\n" +
                "import org.apache.ibatis.annotations.Param;\n" +
                "public class MapperDao {\n" +
                "    public interface InnerMapper {\n" +
                "        void test(@Param(\"id\") String id);\n" +
                "    }\n" +
                "}\n");

        myFixture.configureByText("InnerMapper.xml",
            "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >\n" +
                "<mapper namespace=\"com.baomidou.mybatis3.mapper.MapperDao$InnerMapper\">\n" +
                "    <select id=\"test\">\n" +
                "        select 1\n" +
                "    </select>\n" +
                "</mapper>");

        Optional<PsiClass> clazz = JavaUtils.findClazz(
            getProject(), "com.baomidou.mybatis3.mapper.MapperDao$InnerMapper");
        assertTrue(clazz.isPresent());

        Optional<PsiClass[]> classes = JavaUtils.findClasses(
            getProject(), "com.baomidou.mybatis3.mapper.MapperDao$InnerMapper");
        assertTrue(classes.isPresent() && classes.get().length > 0);

        PsiMethod[] testMethods = clazz.get().findMethodsByName("test", false);
        assertEquals(1, testMethods.length);

        Optional<DomElement> statement = JavaService.getInstance(
            getProject()).findStatement(testMethods[0]);
        assertTrue("Statement should be found for inner interface method", statement.isPresent());

        List<Mapper> mappers = new ArrayList<>();
        JavaService.getInstance(getProject()).processClass(clazz.get(), mappers::add);
        assertEquals(1, mappers.size());

        assertEquals(1, MapperUtils.findMappers(getProject(), clazz.get()).size());
        assertEquals(1, MapperUtils.findMappers(getProject(), "com.baomidou.mybatis3.mapper.MapperDao$InnerMapper").size());
        assertEquals(1, MapperUtils.findMappers(getProject(), "com.baomidou.mybatis3.mapper.MapperDao.InnerMapper").size());
        assertEquals(1, MapperUtils.findMappers(getProject(), MapperNamespace.fromPsiClass(clazz.get())).size());

        Optional<PsiMethod[]> methods = JavaUtils.findMethods(
            getProject(), "com.baomidou.mybatis3.mapper.MapperDao$InnerMapper", "test");
        assertTrue(methods.isPresent() && methods.get().length > 0);

        assertTrue(MapperUtils.isNamespaceMatches("com.baomidou.mybatis3.mapper.MapperDao$InnerMapper", clazz.get()));
        assertTrue(MapperUtils.isNamespaceMatches("com.baomidou.mybatis3.mapper.MapperDao.InnerMapper", clazz.get()));

        // Value Object Model tests
        MapperNamespace nsFromRawDollar = MapperNamespace.of("com.baomidou.mybatis3.mapper.MapperDao$InnerMapper");
        MapperNamespace nsFromRawDot = MapperNamespace.of("com.baomidou.mybatis3.mapper.MapperDao.InnerMapper");
        MapperNamespace nsFromPsi = MapperNamespace.fromPsiClass(clazz.get());
        assertEquals("com.baomidou.mybatis3.mapper.MapperDao.InnerMapper", nsFromPsi.getCanonicalName());
        assertEquals("com.baomidou.mybatis3.mapper.MapperDao$InnerMapper", nsFromPsi.getJvmName());
        assertEquals(nsFromRawDollar, nsFromRawDot);
        assertEquals(nsFromRawDollar, nsFromPsi);

        MapperStatementId stmtFromMethod = MapperStatementId.ofMethod(testMethods[0]);
        MapperStatementId stmtFromSignatureDollar = MapperStatementId.ofSignature("com.baomidou.mybatis3.mapper.MapperDao$InnerMapper.test");
        MapperStatementId stmtFromSignatureDot = MapperStatementId.ofSignature("com.baomidou.mybatis3.mapper.MapperDao.InnerMapper.test");
        assertEquals("com.baomidou.mybatis3.mapper.MapperDao.InnerMapper.test", stmtFromMethod.getCanonicalId());
        assertEquals(stmtFromMethod, stmtFromSignatureDollar);
        assertEquals(stmtFromMethod, stmtFromSignatureDot);

        com.intellij.psi.xml.XmlFile xmlFile = (com.intellij.psi.xml.XmlFile) myFixture.getFile();
        com.intellij.psi.xml.XmlTag rootTag = xmlFile.getRootTag();
        assertNotNull(rootTag);
        com.intellij.psi.xml.XmlAttribute nsAttr = rootTag.getAttribute("namespace");
        assertNotNull(nsAttr);
        com.intellij.psi.xml.XmlAttributeValue val = nsAttr.getValueElement();
        assertNotNull(val);
        com.intellij.psi.PsiReference[] references = val.getReferences();
        boolean resolvedToInner = false;
        for (com.intellij.psi.PsiReference ref : references) {
            com.intellij.psi.PsiElement elem = ref.resolve();
            if (clazz.get().equals(elem)) {
                resolvedToInner = true;
                break;
            }
        }
        assertTrue("XML namespace reference should resolve to InnerMapper class", resolvedToInner);
    }
}
