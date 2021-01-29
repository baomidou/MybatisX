package com.baomidou.plugin.idea.mybatisx.action;

import com.baomidou.plugin.idea.mybatisx.model.Config;
import com.baomidou.plugin.idea.mybatisx.ui.CodeGenerateUI;
import com.google.common.base.Joiner;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ClassGenerateDialogWrapper extends DialogWrapper {


    private CodeGenerateUI codeGenerateUI = new CodeGenerateUI();

    protected ClassGenerateDialogWrapper(@Nullable Project project) {
        super(project);
        setTitle("Generate Options");
        setSize(600, 400);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return codeGenerateUI.getRootPanel();
    }

    public CodeGenerateUI getCodeGenerateUI() {
        return codeGenerateUI;
    }

    public void generateCode(PsiElement[] psiElements) {
        try {
            List<String> result = new ArrayList<>();
            if (psiElements.length == 1) {
                Config generator_config = new Config();
//                    generator_config.setName(tableNameField.getText());
//                    generator_config.setTableName(tableNameField.getText());
//                    generator_config.setProjectFolder(projectFolderBtn.getText());
//
//                    generator_config.setModelPackage(modelPackageField.getText());
//                    generator_config.setDaoPackage(daoPackageField.getText());
//                    generator_config.setXmlPackage(xmlPackageField.getText());
//                    generator_config.setDaoName(daoNameField.getText());
//                    generator_config.setModelName(modelNameField.getText());
//                    generator_config.setPrimaryKey(keyField.getText());
//
//                    generator_config.setOffsetLimit(offsetLimitBox.getSelectedObjects() != null);
//                    generator_config.setComment(commentBox.getSelectedObjects() != null);
//                    generator_config.setNeedToStringHashcodeEquals(needToStringHashcodeEqualsBox.getSelectedObjects() != null);
//                    generator_config.setNeedMapperAnnotation(needMapperAnnotationBox.getSelectedObjects() != null);
//                    generator_config.setUseSchemaPrefix(useSchemaPrefixBox.getSelectedObjects() != null);
//                    generator_config.setNeedForUpdate(needForUpdateBox.getSelectedObjects() != null);
//                    generator_config.setAnnotationDAO(annotationDAOBox.getSelectedObjects() != null);
//                    generator_config.setUseDAOExtendStyle(useDAOExtendStyleBox.getSelectedObjects() != null);
//                    generator_config.setJsr310Support(jsr310SupportBox.getSelectedObjects() != null);
//                    generator_config.setAnnotation(annotationBox.getSelectedObjects() != null);
//                    generator_config.setUseActualColumnNames(useActualColumnNamesBox.getSelectedObjects() != null);
//                    generator_config.setUseTableNameAlias(useTableNameAliasBox.getSelectedObjects() != null);
//                    generator_config.setUseExample(useExampleBox.getSelectedObjects() != null);
//                    generator_config.setUseLombokPlugin(useLombokBox.getSelectedObjects() != null);
//                    generator_config.setSimpleMode(useSimpleModeBox.getSelectedObjects() != null);
//                    generator_config.setModelMvnPath(modelMvnField.getText());
//                    generator_config.setDaoMvnPath(daoMvnField.getText());
//                    generator_config.setXmlMvnPath(xmlMvnField.getText());


//                    result = new MybatisGenerator(generator_config).execute(psiElements[0], project, true);
            } else {

//                    for (PsiElement psiElement : psiElements) {
//                        TableInfo tableInfo = new TableInfo((DbTable) psiElement);
//                        String tableName = tableInfo.getTableName();
//                        String modelName = StringUtils.dbStringToCamelStyle(tableName);
//                        String primaryKey = "";
//                        if (tableInfo.getPrimaryKeys() != null && tableInfo.getPrimaryKeys().size() != 0) {
//                            primaryKey = tableInfo.getPrimaryKeys().get(0);
//                        }
//                        Config generator_config = new Config();
//                        generator_config.setName(tableName);
//                        generator_config.setTableName(tableName);
//                        generator_config.setProjectFolder(projectFolderBtn.getText());
//
//                        generator_config.setModelPackage(modelPackageField.getText());
//                        generator_config.setDaoPackage(daoPackageField.getText());
//                        generator_config.setXmlPackage(xmlPackageField.getText());
//                        generator_config.setDaoName(modelName + daoPostfixField.getText());
//                        generator_config.setModelName(modelName);
//                        generator_config.setPrimaryKey(primaryKey);
//                        generator_config.setOffsetLimit(offsetLimitBox.getSelectedObjects() != null);
//
//                        generator_config.setComment(commentBox.getSelectedObjects() != null);
//                        generator_config.setNeedToStringHashcodeEquals(needToStringHashcodeEqualsBox.getSelectedObjects() != null);
//                        generator_config.setNeedMapperAnnotation(needMapperAnnotationBox.getSelectedObjects() != null);
//                        generator_config.setUseSchemaPrefix(useSchemaPrefixBox.getSelectedObjects() != null);
//                        generator_config.setNeedForUpdate(needForUpdateBox.getSelectedObjects() != null);
//                        generator_config.setAnnotationDAO(annotationDAOBox.getSelectedObjects() != null);
//                        generator_config.setUseDAOExtendStyle(useDAOExtendStyleBox.getSelectedObjects() != null);
//                        generator_config.setJsr310Support(jsr310SupportBox.getSelectedObjects() != null);
//                        generator_config.setAnnotation(annotationBox.getSelectedObjects() != null);
//                        generator_config.setUseActualColumnNames(useActualColumnNamesBox.getSelectedObjects() != null);
//                        generator_config.setUseTableNameAlias(useTableNameAliasBox.getSelectedObjects() != null);
//                        generator_config.setUseExample(useExampleBox.getSelectedObjects() != null);
//                        generator_config.setUseLombokPlugin(useLombokBox.getSelectedObjects() != null);
//                        generator_config.setSimpleMode(useSimpleModeBox.getSelectedObjects() != null);
//                        generator_config.setModelMvnPath(modelMvnField.getText());
//                        generator_config.setDaoMvnPath(daoMvnField.getText());
//                        generator_config.setXmlMvnPath(xmlMvnField.getText());
//                        result = new MybatisGenerator(generator_config).execute(psiElement, project, true);
//                    }

            }
            if (!result.isEmpty()) {
                Messages.showMessageDialog(Joiner.on("\n").join(result), "warnning", Messages.getWarningIcon());
            }

        } catch (Exception e1) {
            Messages.showMessageDialog(e1.getMessage(), "error", Messages.getErrorIcon());
        }
    }
}
