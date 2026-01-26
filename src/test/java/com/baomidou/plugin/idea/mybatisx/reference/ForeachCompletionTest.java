package com.baomidou.plugin.idea.mybatisx.reference;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.junit.Test;

import java.util.List;

public class ForeachCompletionTest extends LightJavaCodeInsightFixtureTestCase {

    @Test
    public void testForeachCollectionCompletion() {
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
                "    List<User> findUser(@Param(\"users\") List<User> users);\n" +
                "}\n");

        // Test completion in 'collection' attribute
        myFixture.configureByText("UserMapper.xml",
            "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >\n" +
                "<mapper namespace=\"com.baomidou.mybatis3.mapper.UserMapper\">\n" +
                "    <select id=\"findUser\">\n" +
                "        <foreach collection=\"us<caret>\" item=\"user\">\n" +
                "        </foreach>\n" +
                "    </select>\n" +
                "</mapper>");

        myFixture.completeBasic();
        List<String> lookupElementStrings = myFixture.getLookupElementStrings();
        assertNotNull(lookupElementStrings);
        assertContainsElements(lookupElementStrings, "users");
    }

    @Test
    public void testBindValueCompletion() {
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
                "    List<User> findUser(@Param(\"query\") User query);\n" +
                "}\n");

        // Test completion in 'bind' 'value' attribute
        myFixture.configureByText("UserMapper.xml",
            "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >\n" +
                "<mapper namespace=\"com.baomidou.mybatis3.mapper.UserMapper\">\n" +
                "    <select id=\"findUser\">\n" +
                "        <bind name=\"pattern\" value=\"qu<caret>\" />\n" +
                "    </select>\n" +
                "</mapper>");

        myFixture.completeBasic();
        List<String> lookupElementStrings = myFixture.getLookupElementStrings();
        assertNotNull(lookupElementStrings);
        assertContainsElements(lookupElementStrings, "query");
    }
}
