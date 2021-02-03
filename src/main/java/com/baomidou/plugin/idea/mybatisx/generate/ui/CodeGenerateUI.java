package com.baomidou.plugin.idea.mybatisx.generate.ui;

import com.baomidou.plugin.idea.mybatisx.generate.dto.GenerateConfig;
import com.baomidou.plugin.idea.mybatisx.generate.dto.TemplateAnnotationType;
import com.baomidou.plugin.idea.mybatisx.generate.dto.TemplateContext;
import com.baomidou.plugin.idea.mybatisx.generate.dto.TemplateSettingDTO;
import com.baomidou.plugin.idea.mybatisx.generate.setting.TemplatesSettings;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.intellij.database.model.DasTable;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CodeGenerateUI {
    private JPanel rootPanel;
    private JTextField tableNameTextField;
    private JTextField projectFolderTextField;
    private JCheckBox commentCheckBox;
    private JCheckBox lombokCheckBox;
    private JCheckBox actualColumnCheckBox;
    private JCheckBox JSR310DateAPICheckBox;
    private JCheckBox toStringHashCodeEqualsCheckBox;
    private JTextField fileTextField;
    private JTextField basePathTextField;
    private JTextField relativePackageTextField;
    private JPanel containingPanel;
    private JRadioButton JPARadioButton;
    private JRadioButton mybatisPlus3RadioButton;
    private JRadioButton mybatisPlus2RadioButton;
    private JRadioButton noneRadioButton;
    private JPanel templateExtraPanel;
    private JComboBox moduleComboBox;
    private JTextField basePackageTextField;
    private JTextField encodingTextField;

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public JPanel getContainingPanel() {
        return containingPanel;
    }

    public void fillData(Project project, PsiElement[] tableElements, TemplateContext templateContext) {
        GenerateConfig generateConfig = templateContext.getGenerateConfig();
        Map<String, List<TemplateSettingDTO>> templateSettingMap = templateContext.getTemplateSettingMap();
        if (generateConfig == null) {
            generateConfig = new DefaultGenerateConfig(templateContext);
        }
        PsiElement tableElement = tableElements[0];
        DasTable table = (DasTable) tableElement;
        String tableName = table.getName();
        tableNameTextField.setText(tableName);
        // 项目路径
        projectFolderTextField.setText(generateConfig.getTargetProject());
        // model 的名字, 例如: UserRule(用户角色表的实体名称)
        fileTextField.setText(StringUtils.dbStringToCamelStyle(tableName));
        Collection<Module> modulesOfType = ModuleUtil.getModulesOfType(project, JavaModuleType.getModuleType());
        // 实体类的模块名称
        for (Module module : modulesOfType) {
            moduleComboBox.addItem(module.getName());
        }
        String moduleName = generateConfig.getModuleName();
        if (moduleName != null) {
            moduleComboBox.setSelectedItem(moduleName);
        } else if (moduleComboBox.getItemCount() > 0) {
            moduleComboBox.setSelectedIndex(0);
        }

        selectAnnotation(generateConfig.getAnnotationType());
        // 模块下的源码路径
        basePathTextField.setText(generateConfig.getBasePath());
        // 实体类的包名
        relativePackageTextField.setText(generateConfig.getRelativePackage());
        basePackageTextField.setText(generateConfig.getBasePackage());
        encodingTextField.setText(generateConfig.getEncoding());
        GridLayout mgr = new GridLayout(0, 2, 0, 0);
        templateExtraPanel.setLayout(mgr);
        List<TemplateSettingDTO> list = templateSettingMap.get(TemplatesSettings.DEFAULT_TEMPLATE_NAME);
        for (TemplateSettingDTO templateSettingDTO : list) {
            // 添加的模板默认都是要选中的
            JCheckBox jCheckBox = new JCheckBox(templateSettingDTO.getConfigName());
            if (generateConfig.getExtraTemplateNames().contains(jCheckBox.getText())) {
                jCheckBox.setSelected(true);
            }
            templateExtraPanel.add(jCheckBox);
        }


        if (generateConfig.isNeedsComment()) {
            commentCheckBox.setSelected(true);
        }
        if (generateConfig.isNeedToStringHashcodeEquals()) {
            toStringHashCodeEqualsCheckBox.setSelected(true);
        }
        if (generateConfig.isUseLombokPlugin()) {
            lombokCheckBox.setSelected(true);
        }
        if (generateConfig.isUseActualColumns()) {
            actualColumnCheckBox.setSelected(true);
        }
        if (generateConfig.isJsr310Support()) {
            JSR310DateAPICheckBox.setSelected(true);
        }

    }

    /**
     * 选择注解类型
     *
     * @param annotationType
     */
    private void selectAnnotation(String annotationType) {
        if (annotationType == null) {
            noneRadioButton.setSelected(true);
            return;
        }
        TemplateAnnotationType templateAnnotationType = TemplateAnnotationType.valueOf(annotationType);
        if (templateAnnotationType == TemplateAnnotationType.JPA) {
            JPARadioButton.setSelected(true);
        } else if (templateAnnotationType == TemplateAnnotationType.MYBATIS_PLUS3) {
            mybatisPlus3RadioButton.setSelected(true);
        } else if (templateAnnotationType == TemplateAnnotationType.MYBATIS_PLUS2) {
            mybatisPlus2RadioButton.setSelected(true);
        } else {
            noneRadioButton.setSelected(true);
        }
    }

    public GenerateConfig getGenerateConfig(Project project) {
        GenerateConfig generateConfig = new GenerateConfig();
        generateConfig.setDomainObjectName(fileTextField.getText());
        generateConfig.setRelativePackage(relativePackageTextField.getText());
        generateConfig.setBasePackage(basePackageTextField.getText());
        generateConfig.setBasePath(basePathTextField.getText());
        generateConfig.setEncoding(encodingTextField.getText());
        String moduleName = null;
        Object selectedItem = moduleComboBox.getSelectedItem();
        if (selectedItem == null) {
            if (moduleComboBox.getItemCount() > 0) {
                moduleName = moduleComboBox.getItemAt(0).toString();
            }
        }
        if (moduleName == null && selectedItem != null) {
            moduleName = selectedItem.toString();
        }

        final String findModule = moduleName;
        Collection<Module> modulesOfType = ModuleUtil.getModulesOfType(project, JavaModuleType.getModuleType());
        Optional<Module> any = modulesOfType.stream().filter(module -> module.getName().equalsIgnoreCase(findModule)).findAny();
        any.ifPresent(module -> {
            String moduleDirPath = ModuleUtil.getModuleDirPath(any.get());
            int ideaIndex = moduleDirPath.indexOf(".idea");
            if (ideaIndex > -1) {
                moduleDirPath = moduleDirPath.substring(0, ideaIndex);
            }
            generateConfig.setModulePath(moduleDirPath);
        });

        generateConfig.setTableName(tableNameTextField.getText());
        generateConfig.setTargetProject(projectFolderTextField.getText());

        List<String> templateNames = Arrays.stream(templateExtraPanel.getComponents())
            .filter(x -> (x instanceof JCheckBox) && ((JCheckBox) x).isSelected())
            .map(x -> ((JCheckBox) x).getText())
            .collect(Collectors.toList());
        generateConfig.setExtraTemplateNames(templateNames);


        // 从1.4.7起改为基于模板生成, 所以不再需要那么多针对mapper的插件了
//        generateConfig.setOffsetLimit(pageCheckBox.isSelected());
//        generateConfig.setNeedForUpdate(toStringHashCodeEqualsCheckBox.isSelected());
//        generateConfig.setNeedMapperAnnotation(mapperAnnotationCheckBox.isSelected());
//        generateConfig.setRepositoryAnnotation(repositoryAnnotationCheckBox.isSelected());
//        generateConfig.setNeedMapperAnnotation(mapperAnnotationCheckBox.isSelected());
        generateConfig.setNeedsComment(commentCheckBox.isSelected());
        generateConfig.setNeedToStringHashcodeEquals(toStringHashCodeEqualsCheckBox.isSelected());
        generateConfig.setUseLombokPlugin(lombokCheckBox.isSelected());
        generateConfig.setUseActualColumns(actualColumnCheckBox.isSelected());
        generateConfig.setJsr310Support(JSR310DateAPICheckBox.isSelected());


        String annotationTypeName = findAnnotationType();
        generateConfig.setAnnotationType(annotationTypeName);
        return generateConfig;
    }

    @Nullable
    private String findAnnotationType() {
        String annotationTypeName = null;
        if (noneRadioButton.isSelected()) {
            annotationTypeName = TemplateAnnotationType.NONE.name();
        }
        if (annotationTypeName == null && JPARadioButton.isSelected()) {
            annotationTypeName = TemplateAnnotationType.JPA.name();
        }
        if (annotationTypeName == null && mybatisPlus3RadioButton.isSelected()) {
            annotationTypeName = TemplateAnnotationType.MYBATIS_PLUS3.name();
        }
        if (annotationTypeName == null && mybatisPlus2RadioButton.isSelected()) {
            annotationTypeName = TemplateAnnotationType.MYBATIS_PLUS2.name();
        }
        return annotationTypeName;
    }

    private class DefaultGenerateConfig extends GenerateConfig {
        private TemplateContext templateContext;

        public DefaultGenerateConfig(TemplateContext templateContext) {

            this.templateContext = templateContext;
        }

        @Override
        public String getTargetProject() {
            return templateContext.getProjectPath();
        }

        @Override
        public String getModuleName() {
            return templateContext.getModuleName();
        }

        @Override
        public String getAnnotationType() {
            return templateContext.getAnnotationType();
        }

        @Override
        public List<String> getExtraTemplateNames() {
            Map<String, List<TemplateSettingDTO>> templateSettingMap = templateContext.getTemplateSettingMap();
            List<TemplateSettingDTO> list = templateSettingMap.get(TemplatesSettings.DEFAULT_TEMPLATE_NAME);
            return list.stream().map(TemplateSettingDTO::getConfigName).collect(Collectors.toList());
        }

        @Override
        public boolean isNeedsComment() {
            return true;
        }

        @Override
        public boolean isNeedToStringHashcodeEquals() {
            return true;
        }

        @Override
        public String getBasePackage() {
            return "generator";
        }

        @Override
        public String getRelativePackage() {
            return "domain";
        }

        @Override
        public String getBasePath() {
            return "src/main/java";
        }


        @Override
        public String getEncoding() {
            return "UTF-8";
        }
    }

}
