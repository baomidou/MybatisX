package com.baomidou.plugin.idea.mybatisx.jpa.file;

import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.testFramework.IdeaTestUtil;
import com.intellij.testFramework.builders.JavaModuleFixtureBuilder;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;

import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

public class CreateFileTest extends JavaCodeInsightFixtureTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/resources/module";
    }


    public void testCreateFile() {
        myFixture.configureByFiles("domain/EntityClass.java", "domain/EntityParentClass.java");

        Project project = getProject();
        Set<String> allowFields = new HashSet<>();
        allowFields.add("name");
        allowFields.add("amount");

        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        GlobalSearchScope globalSearchScope = GlobalSearchScope.allScope(project);
        PsiClass entityClass = javaPsiFacade.findClass("domain.EntityClass", globalSearchScope);
        String entityClassIdAgeDTO = "EntityClassIdAgeDTO";
        PsiClass dtoClass = javaPsiFacade.getElementFactory().createClass(entityClassIdAgeDTO);


        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("package").append(" ");
        stringBuilder.append(((PsiJavaFile) entityClass.getParent()).getPackageName());
        stringBuilder.append(";");
        stringBuilder.append("\n");

        for (PsiField field : entityClass.getAllFields()) {
            if (allowFields.contains(field.getName())) {
                String importType = field.getType().getCanonicalText();
                stringBuilder.append("import").append(" ").append(importType).append(";").append("\n");
            }
        }

        stringBuilder.append("public").append(" ").append("class")
            .append(" ").append(entityClassIdAgeDTO).append("{").append("\n");
        for (PsiField field : entityClass.getAllFields()) {
            if (allowFields.contains(field.getName())) {
                stringBuilder.append("private").append(" ");
                stringBuilder.append(field.getType().getPresentableText()).append(" ");
                stringBuilder.append(field.getName()).append(";").append("\n");
            }
        }
        for (PsiMethod psiMethod : entityClass.getAllMethods()) {
            if (psiMethod.getName().startsWith("set") &&
                allowFields.contains(StringUtils.lowerCaseFirstChar(psiMethod.getName().substring(3)))) {
                stringBuilder.append(psiMethod.getText()).append("\n");
            }
        }
        for (PsiMethod psiMethod : entityClass.getAllMethods()) {
            if (psiMethod.getName().startsWith("get") &&
                allowFields.contains(StringUtils.lowerCaseFirstChar(psiMethod.getName().substring(3)))) {
                stringBuilder.append(psiMethod.getText()).append("\n");
            }
        }
        for (PsiMethod psiMethod : entityClass.getAllMethods()) {
            if (psiMethod.getName().startsWith("is") &&
                allowFields.contains(StringUtils.lowerCaseFirstChar(psiMethod.getName().substring(2)))) {
                stringBuilder.append(psiMethod).append("\n");
            }
        }

        stringBuilder.append("}");

        assert entityClass != null;
        WriteAction.run(() -> {
            PsiDirectory directory = entityClass.getContainingFile().getParent();
            PsiFile file = directory.createFile(entityClassIdAgeDTO + ".java");
            VirtualFile virtualFile = file.getVirtualFile();

            try (OutputStream outputStream = virtualFile.getOutputStream(null);) {
                outputStream.write(stringBuilder.toString().getBytes());
            } catch (Exception e) {

            }
        });

//        JavaPsiFacade psiFileFactory = JavaPsiFacade.getInstance(project);
//        PsiElementFactory elementFactory = psiFileFactory.getElementFactory();
//        PsiClass resultMapClass = elementFactory.createClassFromText("public class Hello{}", entityClass);

    }

    @Override
    protected void tuneFixture(JavaModuleFixtureBuilder moduleBuilder) throws Exception {
        super.tuneFixture(moduleBuilder);

        String javaHome = System.getenv("JAVA_HOME");
        if (javaHome == null) {
            javaHome = IdeaTestUtil.getMockJdk18Path().getPath();
        }

        moduleBuilder.addJdk(javaHome);

        moduleBuilder.addLibrary("mp3-lib", "src/test/testData/lib");

    }
}
