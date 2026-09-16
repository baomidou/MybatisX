package com.baomidou.plugin.idea.mybatisx.util;

import com.intellij.codeInsight.actions.FileInEditorProcessor;
import com.intellij.codeInsight.actions.LastRunReformatCodeOptionsProvider;
import com.intellij.codeInsight.actions.ReformatCodeRunOptions;
import com.intellij.codeInsight.actions.TextRangeType;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiInvalidElementAccessException;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiWildcardType;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.util.IncorrectOperationException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 创建类, 可能有更便捷的方式创建实体类. 有待优化
 */
@Slf4j
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

                Editor editor = FileEditorManager.getInstance(file.getProject()).openTextEditor(
                    new OpenFileDescriptor(file.getProject(), virtualFile), false
                );

                if (editor == null) {
                    log.error("Failed to get Editor");
                    return;
                }

                new FileInEditorProcessor(file, editor, currentRunOptions).processCode();

            } catch (PsiInvalidElementAccessException | IOException | IncorrectOperationException ignored) {
            }

        });
    }

    @NotNull
    public String defineClass(Set<String> allowFields, PsiClass entityClass, String dtoName) {
        StringBuilder stringBuilder = new StringBuilder();

        // 包名: 内部类的 parent 不是 PsiJavaFile, 不能直接强转, 统一从所在文件获取
        String packageName = getPackageNameOf(entityClass);
        if (!packageName.isEmpty()) {
            stringBuilder.append("package").append(" ").append(packageName).append(";").append("\n");
        }

        // 添加字段的 import
        appendImports(stringBuilder, entityClass, allowFields, packageName);

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
                stringBuilder.append(psiMethod.getText()).append("\n");
            }
        }

        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    /**
     * 添加 DTO 字段所引用类型的 import.
     * 跳过基本类型、数组、默认包、java.lang 以及同包类型, 并递归收集泛型参数,
     * 避免生成 {@code import int;} 之类的非法 import.
     */
    private void appendImports(@NotNull StringBuilder stringBuilder,
                               @NotNull PsiClass entityClass,
                               @NotNull Set<String> allowFields,
                               @NotNull String packageName) {
        Set<String> importedTypes = new LinkedHashSet<>();
        for (PsiField field : entityClass.getAllFields()) {
            if (allowFields.contains(field.getName())) {
                collectImportTypes(field.getType(), packageName, importedTypes);
            }
        }
        for (String importedType : importedTypes) {
            stringBuilder.append("import").append(" ").append(importedType).append(";").append("\n");
        }
    }

    /**
     * 递归收集类型中需要 import 的类: 数组取元素类型, 泛型取全部类型参数及通配符边界, 基本类型无需 import.
     */
    private void collectImportTypes(@Nullable PsiType type,
                                    @NotNull String packageName,
                                    @NotNull Set<String> importedTypes) {
        if (type instanceof PsiArrayType arrayType) {
            collectImportTypes(arrayType.getComponentType(), packageName, importedTypes);
        } else if (type instanceof PsiClassType classType) {
            PsiClass psiClass = classType.resolve();
            if (psiClass != null) {
                addImportForClass(psiClass, packageName, importedTypes);
            }
            for (PsiType parameter : classType.getParameters()) {
                collectImportTypes(parameter, packageName, importedTypes);
            }
        } else if (type instanceof PsiWildcardType wildcardType) {
            collectImportTypes(wildcardType.getExtendsBound(), packageName, importedTypes);
            collectImportTypes(wildcardType.getSuperBound(), packageName, importedTypes);
        }
    }

    /**
     * 记录某个类所需的 import. 内部类连同其外层类一并导入, 以保证
     * {@code Outer.Inner} 与 {@code Inner} 两种写法都能被解析(多余的 import 会在格式化时被优化掉).
     */
    private void addImportForClass(@NotNull PsiClass psiClass,
                                   @NotNull String packageName,
                                   @NotNull Set<String> importedTypes) {
        PsiClass current = psiClass;
        while (current != null) {
            String qualifiedName = current.getQualifiedName();
            String classPackageName = getPackageNameOf(current);
            // 匿名类/局部类/类型变量没有限定名; 默认包、java.lang 及同包类型均无需 import
            if (StringUtils.isEmpty(qualifiedName)
                || classPackageName.isEmpty()
                || "java.lang".equals(classPackageName)
                || classPackageName.equals(packageName)) {
                return;
            }
            importedTypes.add(qualifiedName);
            current = current.getContainingClass();
        }
    }

    /**
     * 获取类所在文件的包名, 无法直接获取时按限定名回退, 兼容内部类与库文件.
     */
    @NotNull
    private static String getPackageNameOf(@NotNull PsiClass psiClass) {
        PsiFile containingFile = psiClass.getContainingFile();
        if (containingFile instanceof PsiJavaFile javaFile) {
            return javaFile.getPackageName();
        }
        PsiClass containingClass = psiClass.getContainingClass();
        if (containingClass != null) {
            return getPackageNameOf(containingClass);
        }
        String qualifiedName = psiClass.getQualifiedName();
        if (qualifiedName == null) {
            return "";
        }
        int lastDot = qualifiedName.lastIndexOf('.');
        return lastDot < 0 ? "" : qualifiedName.substring(0, lastDot);
    }
}
