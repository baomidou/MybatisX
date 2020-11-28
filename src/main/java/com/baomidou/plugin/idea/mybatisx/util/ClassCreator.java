package com.baomidou.plugin.idea.mybatisx.util;

import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;

import java.io.OutputStream;
import java.util.Set;

public class ClassCreator {
    public void createFromAllowedFields(Set<String> allowFields, PsiClass entityClass, String dtoName) {
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
            .append(" ").append(dtoName).append("{").append("\n");
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
            PsiFile file = directory.createFile(dtoName + ".java");
            VirtualFile virtualFile = file.getVirtualFile();

            try (OutputStream outputStream = virtualFile.getOutputStream(null);) {
                outputStream.write(stringBuilder.toString().getBytes());
            } catch (Exception e) {

            }
        });
    }
}
