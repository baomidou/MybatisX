package com.baomidou.plugin.idea.mybatisx.jpa;

import com.intellij.codeInsight.completion.CompletionAutoPopupTestCase;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.projectRoots.SimpleJavaSdkType;
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootModificationUtil;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.impl.ModuleLibraryTable;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.ui.configuration.ModuleJdkConfigurable;
import com.intellij.openapi.roots.ui.configuration.ModulesConfigurator;
import com.intellij.openapi.roots.ui.configuration.ProjectJdksConfigurable;
import com.intellij.openapi.roots.ui.configuration.ProjectStructureConfigurable;
import com.intellij.openapi.roots.ui.configuration.projectRoot.LibrariesContainer;
import com.intellij.openapi.roots.ui.configuration.projectRoot.LibrariesContainerFactory;
import com.intellij.openapi.ui.MasterDetailsComponent;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.file.impl.FileManager;
import com.intellij.testFramework.IdeaTestUtil;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.builders.JavaModuleFixtureBuilder;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import com.intellij.util.lang.JavaVersion;
import org.apache.commons.io.FileUtils;
import org.apache.ibatis.reflection.Jdk;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
