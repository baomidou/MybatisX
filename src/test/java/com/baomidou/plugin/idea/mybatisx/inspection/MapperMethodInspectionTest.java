package com.baomidou.plugin.idea.mybatisx.inspection;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.junit.Test;

public class MapperMethodInspectionTest extends LightJavaCodeInsightFixtureTestCase {

    @Test
    public void testPrivateStaticAndDefaultMethodsDoNotRequireStatement() {
        myFixture.enableInspections(new MapperMethodInspection());

        myFixture.configureByText("UserMapper.java",
            "package com.baomidou.mybatis3.mapper;\n" +
                "public interface UserMapper {\n" +
                "    void <error descr=\"Statement with id=\\\"selectMissing\\\" not defined in mapper xml\">selectMissing</error>();\n" +
                "\n" +
                "    private static String convertWarehouse(String input) {\n" +
                "        if (input == null) return null;\n" +
                "        return input;\n" +
                "    }\n" +
                "\n" +
                "    default String defaultMethod() {\n" +
                "        return \"default\";\n" +
                "    }\n" +
                "\n" +
                "    static String staticMethod() {\n" +
                "        return \"static\";\n" +
                "    }\n" +
                "}\n");

        myFixture.configureByText("UserMapper.xml",
            "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >\n" +
                "<mapper namespace=\"com.baomidou.mybatis3.mapper.UserMapper\">\n" +
                "</mapper>");

        myFixture.checkHighlighting(true, false, true);
    }
}
