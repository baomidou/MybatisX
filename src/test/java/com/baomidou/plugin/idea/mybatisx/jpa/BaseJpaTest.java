package com.baomidou.plugin.idea.mybatisx.jpa;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.IdeaTestUtil;
import com.intellij.testFramework.builders.JavaModuleFixtureBuilder;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @author ls9527
 * <p>
 * JavaCompletionTestCase
 * <p>
 * JavaCodeInsightFixtureTestCase
 *
 * 03 JavaCodeInsightFixtureTestCase
 * 04 LightJavaCodeInsightFixtureTestCase
 */
public abstract class BaseJpaTest extends JavaCodeInsightFixtureTestCase {

    public void setUp() throws Exception {
        super.setUp();
        // 添加 src/test/resources 目录
        VfsRootAccess.allowRootAccess(
            getTestRootDisposable(),
            "D:/IdeaProjects/MybatisX/src/test/resources"
        );
        // 或者更通用地使用相对路径：
        // VfsRootAccess.allowRootAccess(getTestRootDisposable(),
        //     Paths.get("src/test/resources").toAbsolutePath().toString());
    }

    public static final String MYBATIS_X_GENERATE_MYBATIS_SQL = "[MybatisX] Generate Mybatis Sql";

    @Override
    protected String getTestDataPath() {
        return "src/test/resources/module";
    }

    protected void launchAction(String tipName) throws IOException {

        String tip = getTestDataPath() + "/template/TipTemplateMapper.java";
        String tipContent = FileUtils.readFileToString(new File(tip), "UTF-8");
        tipContent = tipContent.replace("#tip#", tipName);

        myFixture.configureByFiles("template/Blog.java","template/TipMapper.xml");

        PsiFile mapperJava = myFixture.addFileToProject("template/TipMapper.java", tipContent);
        myFixture.configureFromTempProjectFile("template/TipMapper.java");

        myFixture.openFileInEditor(mapperJava.getVirtualFile());

        List<IntentionAction> intentionActions = myFixture.filterAvailableIntentions(MYBATIS_X_GENERATE_MYBATIS_SQL);
        Assert.assertTrue(intentionActions.size() > 0);

        Optional<IntentionAction> intentionActionOptional = intentionActions.stream().filter(action -> action.getText().equalsIgnoreCase(MYBATIS_X_GENERATE_MYBATIS_SQL)).findAny();
        Assert.assertTrue(intentionActionOptional.isPresent());

        IntentionAction intentionAction = intentionActionOptional.get();
        myFixture.launchAction(intentionAction);

    }

    @Override
    protected void tuneFixture(JavaModuleFixtureBuilder moduleBuilder) throws Exception {
        super.tuneFixture(moduleBuilder);

        String javaHome = System.getenv("JAVA_HOME");
        if(javaHome == null){
            javaHome = IdeaTestUtil.getMockJdk18Path().getPath();
        }

        moduleBuilder.addJdk(javaHome);

        moduleBuilder.addLibrary("mp3-lib","src/test/testData/lib");

    }
}
