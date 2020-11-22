package com.baomidou.plugin.idea.mybatisx.ui;

import com.baomidou.plugin.idea.mybatisx.model.Config;
import com.baomidou.plugin.idea.mybatisx.setting.PersistentConfig;
import com.baomidou.plugin.idea.mybatisx.util.JTextFieldHintListener;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.psi.PsiPackage;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Mybatis generator setting ui.
 */
public class MybatisGeneratorSettingUI  {
    /**
     * The Content panel.
     */
    public JPanel contentPanel = new JBPanel<>(new GridLayout(1, 1));


    private JBTextField modelPackageField = new JBTextField(12);
    private JBTextField daoPackageField = new JBTextField(12);
    private JBTextField xmlPackageField = new JBTextField(12);
    private JTextField daoPostfixField = new JTextField(10);

    private TextFieldWithBrowseButton projectFolderBtn = new TextFieldWithBrowseButton();
    private JTextField modelMvnField = new JBTextField(15);
    private JTextField daoMvnField = new JBTextField(15);
    private JTextField xmlMvnField = new JBTextField(15);
    private JButton setProjectBtn = new JButton("Set-Project-Path");

    private JCheckBox offsetLimitBox = new JCheckBox("Page(分页)");
    private JCheckBox commentBox = new JCheckBox("comment(实体注释)");
    private JCheckBox overrideXMLBox = new JCheckBox("Overwrite-Xml");
    private JCheckBox overrideJavaBox = new JCheckBox("Overwrite-Java");
    private JCheckBox needToStringHashcodeEqualsBox = new JCheckBox("toString/hashCode/equals");
    private JCheckBox useSchemaPrefixBox = new JCheckBox("Use-Schema(使用Schema前缀)");
    private JCheckBox needForUpdateBox = new JCheckBox("Add-ForUpdate(select增加ForUpdate)");
    private JCheckBox annotationDAOBox = new JCheckBox("Repository-Annotation(Repository注解)");
    private JCheckBox useDAOExtendStyleBox = new JCheckBox("Parent-Interface(公共父接口)");
    private JCheckBox jsr310SupportBox = new JCheckBox("JSR310: Date and Time API");
    private JCheckBox annotationBox = new JCheckBox("JPA-Annotation(JPA注解)");
    private JCheckBox useActualColumnNamesBox = new JCheckBox("Actual-Column(实际的列名)");
    private JCheckBox useTableNameAliasBox = new JCheckBox("Use-Alias(启用别名查询)");
    private JCheckBox useExampleBox = new JCheckBox("Use-Example");
    private JCheckBox useLombokBox = new JCheckBox("Use-Lombox");
    private JCheckBox useSimpleModeBox = new JCheckBox("Use-SimpleMode");

    private PersistentConfig config;

    /**
     * Instantiates a new Mybatis generator setting ui.
     */
    public MybatisGeneratorSettingUI() {
    }


    /**
     * Create ui.
     *
     * @param project the project
     */
    public void createUI(Project project) {
        String projectFolder = project.getBasePath();
        contentPanel.setPreferredSize(new Dimension(0, 0));

        /**
         * project folder
         */
        JPanel projectFolderPanel = new JPanel();
        projectFolderPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel projectLabel = new JLabel("project folder:");
        projectFolderPanel.add(projectLabel);
        projectFolderBtn.setTextFieldPreferredWidth(45);
        projectFolderBtn.setText(projectFolder);
        projectFolderBtn.addBrowseFolderListener(new TextBrowseFolderListener(
                FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                super.actionPerformed(e);
                projectFolderBtn.setText(projectFolderBtn.getText().replaceAll("\\\\", "/"));
            }
        });
        projectFolderPanel.add(projectFolderBtn);
        projectFolderPanel.add(setProjectBtn);


        /**
         * mode panel
         */
        JPanel modelPanel = new JPanel();
        modelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        modelPanel.setBorder(BorderFactory.createTitledBorder("model setting"));
        JBLabel labelLeft4 = new JBLabel("package:");
        modelPanel.add(labelLeft4);
        modelPanel.add(modelPackageField);
        JButton packageBtn1 = new JButton("...");
        packageBtn1.addActionListener(actionEvent -> {
            final PackageChooserDialog chooser = new PackageChooserDialog("chooser model package", project);
            chooser.selectPackage(modelPackageField.getText());
            chooser.show();
            final PsiPackage psiPackage = chooser.getSelectedPackage();
            String packageName = psiPackage == null ? null : psiPackage.getQualifiedName();
            modelPackageField.setText(packageName);
        });
        modelPanel.add(packageBtn1);
        modelPanel.add(new JLabel("path:"));
        modelMvnField.setText("src/main/java");
        modelPanel.add(modelMvnField);

        /**
         * dao panel
         */
        JPanel daoPanel = new JPanel();
        daoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        daoPanel.setBorder(BorderFactory.createTitledBorder("dao setting"));

        daoPanel.add(new JLabel("dao postfix:"));
        daoPostfixField.setText("Dao");
        daoPanel.add(daoPostfixField);

        JLabel labelLeft5 = new JLabel("package:");
        daoPanel.add(labelLeft5);
        daoPanel.add(daoPackageField);

        JButton packageBtn2 = new JButton("...");
        packageBtn2.addActionListener(actionEvent -> {
            final PackageChooserDialog chooser = new PackageChooserDialog("choose dao package", project);
            chooser.selectPackage(daoPackageField.getText());
            chooser.show();
            final PsiPackage psiPackage = chooser.getSelectedPackage();
            String packageName = psiPackage == null ? null : psiPackage.getQualifiedName();
            daoPackageField.setText(packageName);
        });
        daoPanel.add(packageBtn2);
        daoPanel.add(new JLabel("path:"));
        daoMvnField.setText("src/main/java");
        daoPanel.add(daoMvnField);


        /**
         * xml mapper panel
         */
        JPanel xmlMapperPanel = new JPanel();
        xmlMapperPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        xmlMapperPanel.setBorder(BorderFactory.createTitledBorder("xml mapper setting"));
        JLabel labelLeft6 = new JLabel("package:");
        xmlMapperPanel.add(labelLeft6);
        xmlMapperPanel.add(xmlPackageField);
        xmlMapperPanel.add(new JLabel("path:"));
        xmlMvnField.setText("src/main/resources");
        xmlMapperPanel.add(xmlMvnField);


        /**
         * options panel
         */
        JBPanel optionsPanel = new JBPanel(new GridLayout(5, 5, 5, 5));
        optionsPanel.setBorder(BorderFactory.createTitledBorder("options panel"));

        commentBox.setSelected(true);
        overrideXMLBox.setSelected(true);
        overrideJavaBox.setSelected(true);
        useSchemaPrefixBox.setSelected(true);
//        默认不使用 lombok
//        useLombokBox.setSelected(true);


        optionsPanel.add(commentBox);
        optionsPanel.add(overrideXMLBox);
        optionsPanel.add(overrideJavaBox);
        optionsPanel.add(useLombokBox);
        optionsPanel.add(useSimpleModeBox);
        optionsPanel.add(useSchemaPrefixBox);
        optionsPanel.add(needForUpdateBox);
        optionsPanel.add(annotationDAOBox);
        optionsPanel.add(jsr310SupportBox);
        optionsPanel.add(annotationBox);
        optionsPanel.add(useActualColumnNamesBox);
        optionsPanel.add(useTableNameAliasBox);
        optionsPanel.add(needToStringHashcodeEqualsBox);
        optionsPanel.add(useExampleBox);
        optionsPanel.add(useDAOExtendStyleBox);
        optionsPanel.add(offsetLimitBox);
        /**
         * 设置面板内容
         */
        JPanel mainPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mainPanel.add(projectFolderPanel);
        mainPanel.add(modelPanel);
        mainPanel.add(daoPanel);
        mainPanel.add(xmlMapperPanel);
        mainPanel.add(optionsPanel);
        contentPanel.add(mainPanel);

        config = PersistentConfig.getInstance(project);
        Map<String, Config> initConfig = config.getInitConfig();
        if (initConfig != null) {
            Config config = initConfig.get("initConfig");
            daoPostfixField.setText(config.getDaoPostfix());
            modelPackageField.setText(config.getModelPackage());
            daoPackageField.setText(config.getDaoPackage());
            xmlPackageField.setText(config.getXmlPackage());

            projectFolderBtn.setText(config.getProjectFolder());
            offsetLimitBox.setSelected(config.isOffsetLimit());
            commentBox.setSelected(config.isComment());
            needToStringHashcodeEqualsBox.setSelected(config.isNeedToStringHashcodeEquals());
            useSchemaPrefixBox.setSelected(config.isUseSchemaPrefix());
            needForUpdateBox.setSelected(config.isNeedForUpdate());
            annotationDAOBox.setSelected(config.isAnnotationDAO());
            useDAOExtendStyleBox.setSelected(config.isUseDAOExtendStyle());
            jsr310SupportBox.setSelected(config.isJsr310Support());
            annotationBox.setSelected(config.isAnnotation());
            useActualColumnNamesBox.setSelected(config.isUseActualColumnNames());
            useTableNameAliasBox.setSelected(config.isUseTableNameAlias());
            useExampleBox.setSelected(config.isUseExample());
            useLombokBox.setSelected(config.isUseLombokPlugin());
            useSimpleModeBox.setSelected(config.getSimpleMode());
        } else {
            modelPackageField.addFocusListener(new JTextFieldHintListener(modelPackageField, "generator"));
            daoPackageField.addFocusListener(new JTextFieldHintListener(daoPackageField, "generator"));
            xmlPackageField.addFocusListener(new JTextFieldHintListener(xmlPackageField, "generator"));
        }
    }

    /**
     * Is modified boolean.
     *
     * @return the boolean
     */
    public boolean isModified() {
        boolean modified = true;
//        modified |= !this.id.getText().equals(config.getId());
//        modified |= !this.entity.getText().equals(config.getEntity());
//        modified |= !this.project_directory.getText().equals(config.getProject_directory());
//        modified |= !this.dao_name.getText().equals(config.getDao_name());
//
//        modified |= !this.entity_package.getText().equals(config.getEntity_package());
//        modified |= !this.entity_directory.getText().equals(config.getEntity_directory());
//        modified |= !this.mapper_package.getText().equals(config.getMapper_package());
//        modified |= !this.mapper_directory.getText().equals(config.getMapper_directory());
//        modified |= !this.xml_package.getText().equals(config.getXml_package());
//        modified |= !this.xml_directory.getText().equals(config.getXml_directory());
//        modified |= !this.password.getPassword().equals(config.getPassword());
//        modified |= !this.username.getText().equals(config.getUsername());
        return modified;
    }

    /**
     * Apply.
     */
    public void apply() {
        HashMap<String, Config> initConfig = new HashMap<>();
        Config config = new Config();
        config.setName("initConfig");
        config.setDaoPostfix(daoPostfixField.getText());
        config.setModelPackage(modelPackageField.getText());
        config.setDaoPackage(daoPackageField.getText());
        config.setXmlPackage(xmlPackageField.getText());
        config.setProjectFolder(projectFolderBtn.getText());

        config.setOffsetLimit(offsetLimitBox.getSelectedObjects() != null);
        config.setComment(commentBox.getSelectedObjects() != null);
        config.setNeedToStringHashcodeEquals(needToStringHashcodeEqualsBox.getSelectedObjects() != null);
        config.setUseSchemaPrefix(useSchemaPrefixBox.getSelectedObjects() != null);
        config.setNeedForUpdate(needForUpdateBox.getSelectedObjects() != null);
        config.setAnnotationDAO(annotationDAOBox.getSelectedObjects() != null);
        config.setUseDAOExtendStyle(useDAOExtendStyleBox.getSelectedObjects() != null);
        config.setJsr310Support(jsr310SupportBox.getSelectedObjects() != null);
        config.setAnnotation(annotationBox.getSelectedObjects() != null);
        config.setUseActualColumnNames(useActualColumnNamesBox.getSelectedObjects() != null);
        config.setUseTableNameAlias(useTableNameAliasBox.getSelectedObjects() != null);
        config.setUseExample(useExampleBox.getSelectedObjects() != null);
        config.setUseLombokPlugin(useLombokBox.getSelectedObjects() != null);
        config.setSimpleMode(useSimpleModeBox.getSelectedObjects() != null);
        initConfig.put(config.getName(), config);
        this.config.setInitConfig(initConfig);


    }

    /**
     * Reset.
     */
    public void reset() {

    }

    /**
     * Gets content pane.
     *
     * @return the content pane
     */
    public JPanel getContentPane() {
        return contentPanel;
    }


}
