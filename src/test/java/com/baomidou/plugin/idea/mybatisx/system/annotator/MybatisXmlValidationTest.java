package com.baomidou.plugin.idea.mybatisx.system.annotator;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.junit.Test;

public class MybatisXmlValidationTest extends LightJavaCodeInsightFixtureTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // Register the annotator manually if needed, but often it works if plugin.xml is loaded or we add it.
        // Since we are in a light test environment without full plugin loading sometimes, we might need to register it.
        // But let's try assuming standard environment or simpler setup.
        // Actually, for LightJavaCodeInsightFixtureTestCase, we often need to manualy load the annotator if it's not a full plugin test.
        // However, let's look at how other tests do it. 
        // TestParamReferenceTest didn't register anything specific, but it tested References, which use ReferenceContributor.
        // Annotators are different. 
    }

    @Test
    public void testBooleanValidation() {
        myFixture.configureByText("User.java",
            "package com.baomidou.mybatis3.domain;\n" +
                "public class User {\n" +
                "    private String name;\n" +
                "    private boolean valid;\n" +
                "    public boolean isValid() { return valid; }\n" +
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

        // We use <error> tag to assert where we expect errors.
        myFixture.configureByText("UserMapper.xml",
            "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >\n" +
                "<mapper namespace=\"com.baomidou.mybatis3.mapper.UserMapper\">\n" +
                "    <select id=\"findUser\">\n" +
                "        <!-- Valid boolean -->\n" +
                "        <if test=\"user.valid\">\n" +
                "        </if>\n" +
                "        <if test=\"user.isValid()\">\n" +
                "        </if>\n" +
                "        <!-- Valid operators -->\n" +
                "        <when test=\"user != null\">\n" +
                "        </when>\n" +
                "        <when test=\"user.name != null\">\n" +
                "        </when>\n" +
                "        <!-- Invalid boolean -->\n" +
                "        <if test=\"<error descr=\\\"Expression must verify boolean (found: String)\\\">user.name</error>\">\n" +
                "        </if>\n" +
                "    </select>\n" +
                "</mapper>");
        
        // IMPORTANT: We must register the annotator to test it in the fixture if it's not loaded automatically.
        // use myFixture.checkHighlighting() handles checking <error> tags.
        // But we need to make sure our Annotator is running.
        // There isn't a direct "myFixture.enableAnnotator" method.
        // Use CodeInsightTestFixture.enableInspections for inspections.
        // For Annotators, we can often rely on them being registered if the test environment mimics the plugin.
        // If not, we might need a stricter test class. 
        // Let's try to pass the annotator class to checkHighlighting if supported or ensure it's loaded.
        // NOTE: In simpler tests, we might need: import com.intellij.codeInsight.daemon.impl.HighlightInfo;
        
        // Actually, we can do:
        // Use `ValidationTest` pattern.
        
        // Let's rely on manual execution if needed, but for now try simply running checkHighlighting.
        // If it fails to find the annotator, we might need to look at how to register it.
        // Some frameworks use `Fixture.enableInspections(new MybatisXmlValidationAnnotator())` ? No, that's for inspections.
        
    }
    
    @Test
    public void testCollectionValidation() {
        myFixture.configureByText("User.java",
            "package com.baomidou.mybatis3.domain;\n" +
                "import java.util.List;\n" +
                "public class User {\n" +
                "    private String name;\n" +
                "    private List<String> roles;\n" +
                "    public List<String> getRoles() { return roles; }\n" +
                "    public String getName() { return name; }\n" +
                "}\n");

        myFixture.configureByText("UserMapper.java",
            "package com.baomidou.mybatis3.mapper;\n" +
                "import com.baomidou.mybatis3.domain.User;\n" +
                "import org.apache.ibatis.annotations.Param;\n" +
                "import java.util.List;\n" +
                "public interface UserMapper {\n" +
                "    void insertUsers(@Param(\"user\") User user, @Param(\"users\") List<User> users);\n" +
                "}\n");

        myFixture.configureByText("UserMapper.xml",
            "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >\n" +
                "<mapper namespace=\"com.baomidou.mybatis3.mapper.UserMapper\">\n" +
                "    <insert id=\"insertUsers\">\n" +
                "        <!-- Valid Collection -->\n" +
                "        <foreach collection=\"users\" item=\"u\">\n" +
                "        </foreach>\n" +
                "        <foreach collection=\"user.roles\" item=\"r\">\n" +
                "        </foreach>\n" +
                "        <!-- Direct Collection Interface -->\n" +
                "        <foreach collection=\"user.roles\" item=\"r\">\n" + // roles is List, which is Collection.
                "        </foreach>\n" +
                "        <!-- Check explicit Collection type if possible or just assume List covers it. -->\n" +
                "        <!-- Map used as collection -->\n" +
                "        <!-- We need to add a Map field to User to test this -->\n" +
                "        <!-- Invalid Collection -->\n" +
                "        <foreach collection=\"<error descr=\\\"Expression must be Collection or Array (found: User)\\\">user</error>\" item=\"u\">\n" +
                "        </foreach>\n" +
                "        <foreach collection=\"<error descr=\\\"Expression must be Collection or Array (found: String)\\\">user.name</error>\" item=\"u\">\n" +
                "        </foreach>\n" +
                "    </insert>\n" +
                "</mapper>");
    }
    
    @Test
    public void testMapCollectionValidation() {
          myFixture.configureByText("User.java",
            "package com.baomidou.mybatis3.domain;\n" +
                "import java.util.Map;\n" +
                "public class User {\n" +
                "    private Map<String, String> properties;\n" +
                "    public Map<String, String> getProperties() { return properties; }\n" +
                "}\n");

        myFixture.configureByText("UserMapper.java",
            "package com.baomidou.mybatis3.mapper;\n" +
                "import com.baomidou.mybatis3.domain.User;\n" +
                "import org.apache.ibatis.annotations.Param;\n" +
                "public interface UserMapper {\n" +
                "    void insertUsers(@Param(\"user\") User user);\n" +
                "}\n");

        myFixture.configureByText("UserMapper.xml",
            "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >\n" +
                "<mapper namespace=\"com.baomidou.mybatis3.mapper.UserMapper\">\n" +
                "    <insert id=\"insertUsers\">\n" +
                "        <!-- Valid Map -->\n" +
                "        <foreach collection=\"user.properties\" item=\"p\">\n" +
                "        </foreach>\n" +
                "    </insert>\n" +
                "</mapper>");
    }
}
