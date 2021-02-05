package com.baomidou.plugin.idea.mybatisx.generate.ui;

import com.baomidou.plugin.idea.mybatisx.generate.dto.GenerateConfig;
import com.baomidou.plugin.idea.mybatisx.generate.dto.TemplateAnnotationType;
import com.baomidou.plugin.idea.mybatisx.generate.dto.TemplateSettingDTO;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.intellij.database.model.DasTable;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
    private JComboBox templatesComboBox;

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public JPanel getContainingPanel() {
        return containingPanel;
    }

    public void fillData(Project project,
                         PsiElement[] tableElements,
                         GenerateConfig generateConfig,
                         String defaultsTemplatesName,
                         Map<String, List<TemplateSettingDTO>> templateSettingMap) {

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
        // 添加动态模板组
        templateSettingMap.keySet().forEach(templatesComboBox::addItem);

        final ItemListener itemListener = e -> {

            final Object templatesName = e.getItem();
            // 只接受选择事件
            if (e.getStateChange() != ItemEvent.SELECTED) {
                return;
            }
            List<TemplateSettingDTO> list = null;
            // 选择选定的模板
            if (!StringUtils.isEmpty(templatesName)) {
                list = templateSettingMap.get(templatesName.toString());
            }
            // 选择默认模板
            if (!StringUtils.isEmpty(defaultsTemplatesName)) {
                list = templateSettingMap.get(defaultsTemplatesName);
            }
            // 默认模板没有设置, 或者默认模板改了新的名字, 找到values的第一条记录
            if (list == null) {
                list = templateSettingMap.values().iterator().next();
            }

            templateExtraPanel.removeAll();
            for (TemplateSettingDTO templateSettingDTO : list) {
                // 添加的模板默认都是要选中的
                JCheckBox jCheckBox = new JCheckBox(templateSettingDTO.getConfigName());
                final List<String> extraTemplateNames = generateConfig.getExtraTemplateNames();
                if (extraTemplateNames != null && extraTemplateNames.contains(jCheckBox.getText())) {
                    jCheckBox.setSelected(true);
                }
                templateExtraPanel.add(jCheckBox);
            }
            templateExtraPanel.updateUI();
        };
        templatesComboBox.addItemListener(itemListener);

        GridLayout mgr = new GridLayout(0, 2, 0, 0);
        templateExtraPanel.setLayout(mgr);


        final String templatesName = generateConfig.getTemplatesName();
        if (templatesName != null) {
            templatesComboBox.setSelectedItem(templatesName);
        } else {
            templatesComboBox.setSelectedItem(0);
        }
        itemListener.itemStateChanged(new ItemEvent(templatesComboBox, ItemEvent.ITEM_STATE_CHANGED, templatesName, ItemEvent.SELECTED));

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

        final Object selectedTemplatesName = templatesComboBox.getSelectedItem();
        if (selectedTemplatesName != null) {
            generateConfig.setTemplatesName(selectedTemplatesName.toString());
        }
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


}
