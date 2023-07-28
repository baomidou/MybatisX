package com.baomidou.plugin.idea.mybatisx.util;

import com.intellij.codeInsight.actions.FileInEditorProcessor;
import com.intellij.codeInsight.actions.LastRunReformatCodeOptionsProvider;
import com.intellij.codeInsight.actions.ReformatCodeRunOptions;
import com.intellij.codeInsight.actions.TextRangeType;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiInvalidElementAccessException;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * 创建类, 可能有更便捷的方式创建实体类. 有待优化
 */
public class ClassCreator {
    public void createFromAllowedFields(Set<String> allowFields, PsiClass entityClass, String dtoName) {
        PsiDirectory directory = entityClass.getContainingFile().getParent();
        if (directory == null) {

            return;
        }
        String stringBuilder = defineClass(allowFields, entityClass, dtoName);

        WriteAction.run(() -> {
            try {
                PsiFile file = directory.createFile(dtoName + ".java");

                VirtualFile virtualFile = file.getVirtualFile();
                virtualFile.setBinaryContent(stringBuilder.getBytes());

                // 格式化代码
                LastRunReformatCodeOptionsProvider provider = new LastRunReformatCodeOptionsProvider(PropertiesComponent.getInstance());
                provider.saveCodeCleanupState(true);
                provider.saveOptimizeImportsState(true);
                provider.saveRearrangeCodeState(true);

                ReformatCodeRunOptions currentRunOptions = provider.getLastRunOptions(file);

                currentRunOptions.setProcessingScope(TextRangeType.WHOLE_FILE);
                new FileInEditorProcessor(file, null, currentRunOptions).processCode();

            } catch (PsiInvalidElementAccessException | IOException | IncorrectOperationException ignored) {
            }

        });
    }

    @NotNull
    public String defineClass(Set<String> allowFields, PsiClass entityClass, String dtoName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("package").append(" ");
        stringBuilder.append(((PsiJavaFile) entityClass.getParent()).getPackageName());
        stringBuilder.append(";");
        stringBuilder.append("\n");

        // 添加字段的 import
        for (PsiField field : entityClass.getAllFields()) {
            if (allowFields.contains(field.getName())) {
                String importType = field.getType().getCanonicalText();
                stringBuilder.append("import").append(" ").append(importType).append(";").append("\n");
            }
        }

        stringBuilder.append("public").append(" ").append("class")
            .append(" ").append(dtoName).append("{").append("\n");
        // 添加字段
        Set<String> addedFields = new HashSet<>();
        for (PsiField field : entityClass.getAllFields()) {
            if (allowFields.contains(field.getName())
                && addedFields.add(field.getName())) {
                final PsiDocComment docComment = field.getDocComment();
                if (docComment != null) {
                    stringBuilder.append(docComment.getText()).append("\n");
                }
                stringBuilder.append("private").append(" ");
                stringBuilder.append(field.getType().getPresentableText()).append(" ");
                stringBuilder.append(field.getName()).append(";").append("\n");
            }
        }
        // 添加setter 方法
        for (PsiMethod psiMethod : entityClass.getAllMethods()) {
            if (psiMethod.getName().startsWith("set") &&
                allowFields.contains(StringUtils.lowerCaseFirstChar(psiMethod.getName().substring(3)))) {
                stringBuilder.append(psiMethod.getText()).append("\n");
            }
        }
        // 添加getter方法
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
        return stringBuilder.toString();
    }
}
