package com.baomidou.plugin.idea.mybatisx.ui;


import com.baomidou.plugin.idea.mybatisx.generate.MybatisGenerator;
import com.baomidou.plugin.idea.mybatisx.model.Config;
import com.baomidou.plugin.idea.mybatisx.model.TableInfo;
import com.baomidou.plugin.idea.mybatisx.setting.PersistentConfig;
import com.baomidou.plugin.idea.mybatisx.util.JTextFieldHintListener;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.google.common.base.Joiner;
import com.intellij.database.psi.DbTable;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPackage;
import com.intellij.ui.ScreenUtil;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 插件主界面
 * Created by kangtian on 2018/8/1.
 */
public class MybatisGeneratorMainUI extends JFrame {


    private AnActionEvent anActionEvent;
    private Project project;
    private PersistentConfig persistentConfig;
    private PsiElement[] psiElements;
    private Map<String, Config> initConfigMap;
    private Config config;


    private JPanel contentPane = new JBPanel<>();
    private JButton buttonOK = new JButton("ok");
    private JButton buttonCancel = new JButton("cancel");


    private JTextField tableNameField = new JTextField(10);
    private JBTextField modelPackageField = new JBTextField(12);
    private JBTextField daoPackageField = new JBTextField(12);
    private JTextField daoPostfixField = new JTextField(10);
    private JBTextField xmlPackageField = new JBTextField(12);
    private JTextField daoNameField = new JTextField(10);
    private JTextField modelNameField = new JTextField(10);
    private JTextField keyField = new JTextField(10);

    private TextFieldWithBrowseButton projectFolderBtn = new TextFieldWithBrowseButton();
    private JTextField modelMvnField = new JBTextField(15);
    private JTextField daoMvnField = new JBTextField(15);
    private JTextField xmlMvnField = new JBTextField(15);

    private JCheckBox offsetLimitBox = new JCheckBox("Page(分页)");
    private JCheckBox commentBox = new JCheckBox("comment(实体注释)");
    private JCheckBox needToStringHashcodeEqualsBox = new JCheckBox("toString/hashCode/equals");
    private JCheckBox needMapperAnnotationBox = new JCheckBox("Mapper Annotation");
    private JCheckBox useSchemaPrefixBox = new JCheckBox("Use-Schema(使用Schema前缀)");
    private JCheckBox needForUpdateBox = new JCheckBox("Add-ForUpdate(select增加ForUpdate)");
    private JCheckBox annotationDAOBox = new JCheckBox("Repository-Annotation");
    private JCheckBox useDAOExtendStyleBox = new JCheckBox("Parent-Interface(公共父接口)");
    private JCheckBox jsr310SupportBox = new JCheckBox("JSR310: Date and Time API");
    private JCheckBox annotationBox = new JCheckBox("JPA-Annotation(JPA注解)");
    private JCheckBox useActualColumnNamesBox = new JCheckBox("Actual-Column(实际的列名)");
    private JCheckBox useTableNameAliasBox = new JCheckBox("Use-Alias(启用别名查询)");
    private JCheckBox useExampleBox = new JCheckBox("Use-Example");
    private JCheckBox useLombokBox = new JCheckBox("Use-Lombox");
    private JCheckBox useSimpleModeBox = new JCheckBox("Use-SimpleMode");

    /**
     * Instantiates a new Mybatis generator main ui.
     *
     * @param anActionEvent the an action event
     * @throws HeadlessException the headless exception
     */
    public MybatisGeneratorMainUI(AnActionEvent anActionEvent) throws HeadlessException {
        this.anActionEvent = anActionEvent;
        this.project = anActionEvent.getData(PlatformDataKeys.PROJECT);
        this.persistentConfig = PersistentConfig.getInstance(project);
        this.psiElements = anActionEvent.getData(LangDataKeys.PSI_ELEMENT_ARRAY);

        initConfigMap = persistentConfig.getInitConfig();


        setTitle("mybatis generate tool");
        int width = 800;
        int height = 600;
        //设置大小
        setPreferredSize(new Dimension(width, height));
        Rectangle mainScreenBounds = ScreenUtil.getMainScreenBounds();
        int x = (mainScreenBounds.width - width) / 2;
        int y = (mainScreenBounds.height - height) / 2;
        if (x < 0) {
            x = 200;
        }
        if (y < 0) {
            y = 200;
        }
        setLocation(x, y);
        pack();
        setVisible(true);

        getRootPane().setDefaultButton(buttonOK);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        PsiElement psiElement = psiElements[0];
        TableInfo tableInfo = new TableInfo((DbTable) psiElement);
        String tableName = tableInfo.getTableName();
        String modelName = StringUtils.dbStringToCamelStyle(tableName);
        String primaryKey = "";
        if (tableInfo.getPrimaryKeys().size() > 0) {
            primaryKey = tableInfo.getPrimaryKeys().get(0);
        }
        String projectFolder = project.getBasePath();

        boolean multiTable;
        if (psiElements.length > 1) {//多表时，只使用默认配置
            multiTable = true;
            if (initConfigMap != null) {
                config = initConfigMap.get("initConfig");
            }
        } else {
            multiTable = false;
            if (initConfigMap != null) {//单表时，优先使用已经存在的配置
                config = initConfigMap.get("initConfig");
            }
        }


        /**
         * project panel
         */
        JPanel projectFolderPanel = new JPanel();
        projectFolderPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel projectLabel = new JLabel("project folder:");
        projectFolderPanel.add(projectLabel);
        projectFolderBtn.setTextFieldPreferredWidth(70);
        if (config != null && !StringUtils.isEmpty(config.getProjectFolder())) {
            projectFolderBtn.setText(config.getProjectFolder());
        } else {
            projectFolderBtn.setText(projectFolder);
        }
        projectFolderBtn.addBrowseFolderListener(new TextBrowseFolderListener(
            FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                super.actionPerformed(e);
                projectFolderBtn.setText(projectFolderBtn.getText().replaceAll("\\\\", "/"));
            }
        });
        projectFolderPanel.add(projectFolderBtn);


        /**
         * table setting
         */
        JPanel tableNameFieldPanel = new JPanel();
        tableNameFieldPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel tablejLabel = new JLabel("table  name:");
        tablejLabel.setSize(new Dimension(20, 30));
        tableNameFieldPanel.add(tablejLabel);
        if (psiElements.length > 1) {
            tableNameField.addFocusListener(new JTextFieldHintListener(tableNameField, "eg:db_table"));
        } else {
            tableNameField.setText(tableName);
        }
        tableNameFieldPanel.add(tableNameField);

        JPanel keyFieldPanel = new JPanel();
        keyFieldPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        keyFieldPanel.add(new JLabel("primary key:"));
        if (psiElements.length > 1) {
            keyField.addFocusListener(new JTextFieldHintListener(keyField, "eg:primary key"));
        } else {
            keyField.setText(primaryKey);
        }
        keyFieldPanel.add(keyField);

        JPanel tablePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tablePanel.setBorder(BorderFactory.createTitledBorder("table setting"));
        tablePanel.add(tableNameFieldPanel);
        tablePanel.add(keyFieldPanel);


        /**
         * model setting
         */
        JPanel modelPanel = new JPanel();
        modelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        modelPanel.setBorder(BorderFactory.createTitledBorder("model setting"));
        if (!multiTable) {
            JPanel modelNameFieldPanel = new JPanel();
            modelNameFieldPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            modelNameFieldPanel.add(new JLabel("file:"));
            modelNameField.setText(modelName);
            modelNameFieldPanel.add(modelNameField);
            modelPanel.add(modelNameFieldPanel);
        }
        JBLabel labelLeft4 = new JBLabel("package:");
        modelPanel.add(labelLeft4);
        if (config != null && !StringUtils.isEmpty(config.getModelPackage())) {
            modelPackageField.setText(config.getModelPackage());
        } else {
            modelPackageField.setText("generate");
        }
        modelPanel.add(modelPackageField);
        JButton modelPackageFieldBtn = new JButton("...");
        modelPackageFieldBtn.addActionListener(actionEvent -> {
            final PackageChooserDialog chooser = new PackageChooserDialog("chooser model package", project);
            chooser.selectPackage(modelPackageField.getText());
            chooser.show();
            final PsiPackage psiPackage = chooser.getSelectedPackage();
            String packageName = psiPackage == null ? null : psiPackage.getQualifiedName();
            modelPackageField.setText(packageName);
            MybatisGeneratorMainUI.this.toFront();
        });
        modelPanel.add(modelPackageFieldBtn);
        modelPanel.add(new JLabel("path:"));
        modelMvnField.setText("src/main/java");
        modelPanel.add(modelMvnField);


        /**
         * dao setting
         */
        JPanel daoPanel = new JPanel();
        daoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        daoPanel.setBorder(BorderFactory.createTitledBorder("dao setting"));


        if (multiTable) { //多表

            if (config != null && !StringUtils.isEmpty(config.getDaoPostfix())) {
                daoPostfixField.setText(config.getDaoPostfix());
            } else {
                daoPostfixField.setText("Dao");
            }
            daoPanel.add(new JLabel("dao postfix:"));
            daoPanel.add(daoPostfixField);
        } else {//单表
            if (config != null && !StringUtils.isEmpty(config.getDaoPostfix())) {
                daoNameField.setText(modelName + config.getDaoPostfix());
            } else {
                daoNameField.setText(modelName + "Dao");
            }
            daoPanel.add(new JLabel("name:"));
            daoPanel.add(daoNameField);
        }


        JLabel labelLeft5 = new JLabel("package:");
        daoPanel.add(labelLeft5);
        if (config != null && !StringUtils.isEmpty(config.getDaoPackage())) {
            daoPackageField.setText(config.getDaoPackage());
        } else {
            daoPackageField.setText("generate");
        }
        daoPanel.add(daoPackageField);
        JButton packageBtn2 = new JButton("...");
        packageBtn2.addActionListener(actionEvent -> {
            final PackageChooserDialog chooser = new PackageChooserDialog("choose dao package", project);
            chooser.selectPackage(daoPackageField.getText());
            chooser.show();
            final PsiPackage psiPackage = chooser.getSelectedPackage();
            String packageName = psiPackage == null ? null : psiPackage.getQualifiedName();
            daoPackageField.setText(packageName);
            MybatisGeneratorMainUI.this.toFront();
        });
        daoPanel.add(packageBtn2);
        daoPanel.add(new JLabel("path:"));
        daoMvnField.setText("src/main/java");
        daoPanel.add(daoMvnField);


        /**
         * xml mapper setting
         */
        JPanel xmlMapperPanel = new JPanel();
        xmlMapperPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        xmlMapperPanel.setBorder(BorderFactory.createTitledBorder("xml mapper setting"));
        JLabel labelLeft6 = new JLabel("package:");
        xmlMapperPanel.add(labelLeft6);
        if (config != null && !StringUtils.isEmpty(config.getXmlPackage())) {
            xmlPackageField.setText(config.getXmlPackage());
        } else {
            xmlPackageField.setText("generator");
        }
        xmlMapperPanel.add(xmlPackageField);
        xmlMapperPanel.add(new JLabel("path:"));
        xmlMvnField.setText("src/main/resources");
        xmlMapperPanel.add(xmlMvnField);

        /**
         * options
         */
        JBPanel optionsPanel = new JBPanel(new GridLayout(6, 4, 5, 5));
        optionsPanel.setBorder(BorderFactory.createTitledBorder("options"));
        if (config == null) {
            commentBox.setSelected(true);
            useLombokBox.setSelected(true);
        } else {
            if (config.isOffsetLimit()) {
                offsetLimitBox.setSelected(true);
            }
            if (config.isComment()) {
                commentBox.setSelected(true);
            }

            if (config.isNeedToStringHashcodeEquals()) {
                needToStringHashcodeEqualsBox.setSelected(true);
            }
            if (config.isNeedMapperAnnotation()) {
                needMapperAnnotationBox.setSelected(true);
            }
            if (config.isUseSchemaPrefix()) {
                useSchemaPrefixBox.setSelected(true);
            }
            if (config.isNeedForUpdate()) {
                needForUpdateBox.setSelected(true);
            }
            if (config.isAnnotationDAO()) {
                annotationDAOBox.setSelected(true);
            }
            if (config.isUseDAOExtendStyle()) {
                useDAOExtendStyleBox.setSelected(true);
            }
            if (config.isJsr310Support()) {
                jsr310SupportBox.setSelected(true);
            }
            if (config.isAnnotation()) {
                annotationBox.setSelected(true);
            }
            if (config.isUseActualColumnNames()) {
                useActualColumnNamesBox.setSelected(true);
            }
            if (config.isUseTableNameAlias()) {
                useTableNameAliasBox.setSelected(true);
            }
            if (config.isUseExample()) {
                useExampleBox.setSelected(true);
            }
            if (config.isUseLombokPlugin()) {
                useLombokBox.setSelected(true);
            }
        }

        optionsPanel.add(commentBox);
        optionsPanel.add(useLombokBox);
        optionsPanel.add(useSimpleModeBox);
        optionsPanel.add(annotationBox);
        optionsPanel.add(needMapperAnnotationBox);
        optionsPanel.add(useSchemaPrefixBox);
        optionsPanel.add(annotationDAOBox);
        optionsPanel.add(useDAOExtendStyleBox);
        optionsPanel.add(jsr310SupportBox);
        optionsPanel.add(useActualColumnNamesBox);
        optionsPanel.add(useTableNameAliasBox);
        optionsPanel.add(needToStringHashcodeEqualsBox);
        optionsPanel.add(useExampleBox);
        optionsPanel.add(needForUpdateBox);
        optionsPanel.add(offsetLimitBox);

        JPanel mainPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mainPanel.setBorder(new EmptyBorder(10, 30, 5, 40));
        if (!multiTable) {
            mainPanel.add(tablePanel);
        }
        mainPanel.add(projectFolderPanel);
        mainPanel.add(modelPanel);
        mainPanel.add(daoPanel);
        mainPanel.add(xmlMapperPanel);
        mainPanel.add(optionsPanel);


        JPanel paneBottom = new JPanel();//确认和取消按钮
        paneBottom.setLayout(new FlowLayout(2));
        paneBottom.add(buttonOK);
        paneBottom.add(buttonCancel);


        /**
         * historyConfig panel
         */

        this.getContentPane().add(Box.createVerticalStrut(10)); //采用x布局时，添加固定宽度组件隔开
        final DefaultListModel<String> defaultListModel = new DefaultListModel<>();


        final JBList configJBList = new JBList(defaultListModel);
        configJBList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        configJBList.setSelectedIndex(0);
        configJBList.setVisibleRowCount(25);
        JBScrollPane ScrollPane = new JBScrollPane(configJBList);


        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
        btnPanel.add(new JLabel("      "));//用来占位置


        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout());
        contentPane.add(mainPanel, BorderLayout.CENTER);
        contentPane.add(paneBottom, BorderLayout.SOUTH);
        setContentPane(contentPane);

        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        try {
            dispose();
            List<String> result = new ArrayList<>();
            if (psiElements.length == 1) {
                Config generator_config = new Config();
                generator_config.setName(tableNameField.getText());
                generator_config.setTableName(tableNameField.getText());
                generator_config.setProjectFolder(projectFolderBtn.getText());

                generator_config.setModelPackage(modelPackageField.getText());
                generator_config.setDaoPackage(daoPackageField.getText());
                generator_config.setXmlPackage(xmlPackageField.getText());
                generator_config.setDaoName(daoNameField.getText());
                generator_config.setModelName(modelNameField.getText());
                generator_config.setPrimaryKey(keyField.getText());

                generator_config.setOffsetLimit(offsetLimitBox.getSelectedObjects() != null);
                generator_config.setComment(commentBox.getSelectedObjects() != null);
                generator_config.setNeedToStringHashcodeEquals(needToStringHashcodeEqualsBox.getSelectedObjects() != null);
                generator_config.setNeedMapperAnnotation(needMapperAnnotationBox.getSelectedObjects() != null);
                generator_config.setUseSchemaPrefix(useSchemaPrefixBox.getSelectedObjects() != null);
                generator_config.setNeedForUpdate(needForUpdateBox.getSelectedObjects() != null);
                generator_config.setAnnotationDAO(annotationDAOBox.getSelectedObjects() != null);
                generator_config.setUseDAOExtendStyle(useDAOExtendStyleBox.getSelectedObjects() != null);
                generator_config.setJsr310Support(jsr310SupportBox.getSelectedObjects() != null);
                generator_config.setAnnotation(annotationBox.getSelectedObjects() != null);
                generator_config.setUseActualColumnNames(useActualColumnNamesBox.getSelectedObjects() != null);
                generator_config.setUseTableNameAlias(useTableNameAliasBox.getSelectedObjects() != null);
                generator_config.setUseExample(useExampleBox.getSelectedObjects() != null);
                generator_config.setUseLombokPlugin(useLombokBox.getSelectedObjects() != null);
                generator_config.setSimpleMode(useSimpleModeBox.getSelectedObjects() != null);
                generator_config.setModelMvnPath(modelMvnField.getText());
                generator_config.setDaoMvnPath(daoMvnField.getText());
                generator_config.setXmlMvnPath(xmlMvnField.getText());


                result = new MybatisGenerator(generator_config).execute(psiElements[0], project, true);
            } else {

                for (PsiElement psiElement : psiElements) {
                    TableInfo tableInfo = new TableInfo((DbTable) psiElement);
                    String tableName = tableInfo.getTableName();
                    String modelName = StringUtils.dbStringToCamelStyle(tableName);
                    String primaryKey = "";
                    if (tableInfo.getPrimaryKeys() != null && tableInfo.getPrimaryKeys().size() != 0) {
                        primaryKey = tableInfo.getPrimaryKeys().get(0);
                    }
                    Config generator_config = new Config();
                    generator_config.setName(tableName);
                    generator_config.setTableName(tableName);
                    generator_config.setProjectFolder(projectFolderBtn.getText());

                    generator_config.setModelPackage(modelPackageField.getText());
                    generator_config.setDaoPackage(daoPackageField.getText());
                    generator_config.setXmlPackage(xmlPackageField.getText());
                    generator_config.setDaoName(modelName + daoPostfixField.getText());
                    generator_config.setModelName(modelName);
                    generator_config.setPrimaryKey(primaryKey);
                    generator_config.setOffsetLimit(offsetLimitBox.getSelectedObjects() != null);

                    generator_config.setComment(commentBox.getSelectedObjects() != null);
                    generator_config.setNeedToStringHashcodeEquals(needToStringHashcodeEqualsBox.getSelectedObjects() != null);
                    generator_config.setNeedMapperAnnotation(needMapperAnnotationBox.getSelectedObjects() != null);
                    generator_config.setUseSchemaPrefix(useSchemaPrefixBox.getSelectedObjects() != null);
                    generator_config.setNeedForUpdate(needForUpdateBox.getSelectedObjects() != null);
                    generator_config.setAnnotationDAO(annotationDAOBox.getSelectedObjects() != null);
                    generator_config.setUseDAOExtendStyle(useDAOExtendStyleBox.getSelectedObjects() != null);
                    generator_config.setJsr310Support(jsr310SupportBox.getSelectedObjects() != null);
                    generator_config.setAnnotation(annotationBox.getSelectedObjects() != null);
                    generator_config.setUseActualColumnNames(useActualColumnNamesBox.getSelectedObjects() != null);
                    generator_config.setUseTableNameAlias(useTableNameAliasBox.getSelectedObjects() != null);
                    generator_config.setUseExample(useExampleBox.getSelectedObjects() != null);
                    generator_config.setUseLombokPlugin(useLombokBox.getSelectedObjects() != null);
                    generator_config.setSimpleMode(useSimpleModeBox.getSelectedObjects() != null);
                    generator_config.setModelMvnPath(modelMvnField.getText());
                    generator_config.setDaoMvnPath(daoMvnField.getText());
                    generator_config.setXmlMvnPath(xmlMvnField.getText());
                    result = new MybatisGenerator(generator_config).execute(psiElement, project, true);
                }

            }
            if (!result.isEmpty()) {
                Messages.showMessageDialog(Joiner.on("\n").join(result), "warnning", Messages.getWarningIcon());
            }

        } catch (Exception e1) {
            Messages.showMessageDialog(e1.getMessage(), "error", Messages.getErrorIcon());
        } finally {
            dispose();
        }
    }

    private void onCancel() {
        dispose();
    }
}
