package com.baomidou.plugin.idea.mybatisx.reference;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.junit.Test;

public class TestParamReferenceTest extends LightJavaCodeInsightFixtureTestCase {

    @Test
    public void testCompletion() {
        myFixture.configureByText("User.java",
            "package com.baomidou.mybatis3.domain;\n" +
                "public class User {\n" +
                "    private String name;\n" +
                "    private int age;\n" +
                "    private String address;\n" +
                "    public String getName() { return name; }\n" +
                "    public void setName(String name) { this.name = name; }\n" +
                "}\n");

        myFixture.configureByText("UserMapper.java",
            "package com.baomidou.mybatis3.mapper;\n" +
                "import com.baomidou.mybatis3.domain.User;\n" +
                "import org.apache.ibatis.annotations.Param;\n" +
                "import java.util.List;\n" +
                "public interface UserMapper {\n" +
                "    List<User> findUser(@Param(\"user\") User user, @Param(\"role\") String role);\n" +
                "}\n");

        myFixture.configureByText("UserMapper.xml",
            "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >\n" +
                "<mapper namespace=\"com.baomidou.mybatis3.mapper.UserMapper\">\n" +
                "    <select id=\"findUser\">\n" +
                "        <if test=\"u<caret>\">\n" +
                "        </if>\n" +
                "    </select>\n" +
                "</mapper>");

        myFixture.completeBasic();
        java.util.List<String> lookupElementStrings = myFixture.getLookupElementStrings();
        assertNotNull(lookupElementStrings);
        assertContainsElements(lookupElementStrings, "user");
    }

    @Test
    public void testDotCompletion() {
        myFixture.configureByText("User.java",
            "package com.baomidou.mybatis3.domain;\n" +
                "public class User {\n" +
                "    private String name;\n" +
                "}\n");

        myFixture.configureByText("UserMapper.java",
            "package com.baomidou.mybatis3.mapper;\n" +
                "import com.baomidou.mybatis3.domain.User;\n" +
                "import org.apache.ibatis.annotations.Param;\n" +
                "import java.util.List;\n" +
                "public interface UserMapper {\n" +
                "    List<User> findUser(@Param(\"user\") User user);\n" +
                "}\n");

        myFixture.configureByText("UserMapper.xml",
            "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >\n" +
                "<mapper namespace=\"com.baomidou.mybatis3.mapper.UserMapper\">\n" +
                "    <select id=\"findUser\">\n" +
                "        <if test=\"user.<caret>\">\n" +
                "        </if>\n" +
                "    </select>\n" +
                "</mapper>");

        myFixture.completeBasic();
        java.util.List<String> lookupElementStrings = myFixture.getLookupElementStrings();
        assertNotNull(lookupElementStrings);
        assertContainsElements(lookupElementStrings, "user.name");
    }

    @Test
    public void testReferenceResolution() {
        myFixture.configureByText("User.java",
            "package com.baomidou.mybatis3.domain;\n" +
                "public class User {\n" +
                "    private String name;\n" +
                "}\n");

        myFixture.configureByText("UserMapper.java",
            "package com.baomidou.mybatis3.mapper;\n" +
                "import com.baomidou.mybatis3.domain.User;\n" +
                "import org.apache.ibatis.annotations.Param;\n" +
                "import java.util.List;\n" +
                "public interface UserMapper {\n" +
                "    List<User> findUser(@Param(\"user\") User user);\n" +
                "}\n");

        myFixture.configureByText("UserMapper.xml",
            "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >\n" +
                "<mapper namespace=\"com.baomidou.mybatis3.mapper.UserMapper\">\n" +
                "    <select id=\"findUser\">\n" +
                "        <if test=\"us<caret>er != null\">\n" + // Check user
                "        </if>\n" +
                "    </select>\n" +
                "</mapper>");

        com.intellij.psi.PsiElement element = myFixture.getElementAtCaret();
        assertTrue(element instanceof com.intellij.psi.PsiParameter);
        assertEquals("user", ((com.intellij.psi.PsiParameter) element).getName());
    }

    @Test
    public void testMethodCompletionWithParentheses() {
        myFixture.configureByText("User.java",
            "package com.baomidou.mybatis3.domain;\n" +
                "public class User {\n" +
                "    private String name;\n" +
                "    public String getName() { return name; }\n" +
                "}\n");

        myFixture.configureByText("UserMapper.java",
            "package com.baomidou.mybatis3.mapper;\n" +
                "import com.baomidou.mybatis3.domain.User;\n" +
                "import org.apache.ibatis.annotations.Param;\n" +
                "import java.util.List;\n" +
                "public interface UserMapper {\n" +
                "    List<User> findUser(@Param(\"user\") User user);\n" +
                "}\n");

        myFixture.configureByText("UserMapper.xml",
            "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >\n" +
                "<mapper namespace=\"com.baomidou.mybatis3.mapper.UserMapper\">\n" +
                "    <select id=\"findUser\">\n" +
                "        <if test=\"user.getNa<caret>\">\n" +
                "        </if>\n" +
                "    </select>\n" +
                "</mapper>");

        myFixture.completeBasic();
        java.util.List<String> lookupElementStrings = myFixture.getLookupElementStrings();
        assertNotNull(lookupElementStrings);
        // We expect it to FAIL initially if we check for "getName()"
        // Current behavior is likely "getName" without parentheses
        assertContainsElements(lookupElementStrings, "getName()");
    }
}
