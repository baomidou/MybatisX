package com.baomidou.plugin.idea.mybatisx.reference;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.junit.Test;

import java.util.List;

public class CollectionCompletionTest extends LightJavaCodeInsightFixtureTestCase {

    @Test
    public void testCollectionCompletion() {
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

        myFixture.configureByText("UserMapper.xml",
            "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >\n" +
                "<mapper namespace=\"com.baomidou.mybatis3.mapper.UserMapper\">\n" +
                "    <select id=\"findUser\">\n" +
                "        <if test=\"users.<caret>\">\n" +
                "        </if>\n" +
                "    </select>\n" +
                "</mapper>");

        myFixture.completeBasic();
        List<String> lookupElementStrings = myFixture.getLookupElementStrings();
        assertNotNull(lookupElementStrings);
        
        // Print all variants to seeing what we get
        System.out.println("Variants: " + lookupElementStrings);

        // We expect "size" (property) and "size()" (method) to be present currently
        boolean hasSizeProperty = lookupElementStrings.contains("users.size");
        boolean hasSizeMethod = lookupElementStrings.contains("users.size()");
        
        // Note: lookup strings usually contain the full text that will be inserted.
        // If the contributor adds "size", it matches "users.size"? 
        // Wait, the contributor code adds: variants.add(LookupElementBuilder.create(prefix + "size")...
        // prefix is "users." (if scanned correctly).
        // Let's verify what prefix is used. 
        // TestParamReferenceContributor:359: String prefix = value.substring(0, lastDot);
        // If value is "users.", then prefix is "users".
        // So added element is "users.size".
        
        assertTrue("Should contain size property", hasSizeProperty);
        assertTrue("Should contain size() method", hasSizeMethod);
    }
}
