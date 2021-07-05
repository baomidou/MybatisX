package com.baomidou.plugin.idea.mybatisx.generate.ui;

import com.baomidou.plugin.idea.mybatisx.generate.dto.DomainInfo;
import com.baomidou.plugin.idea.mybatisx.generate.dto.GenerateConfig;
import com.baomidou.plugin.idea.mybatisx.generate.dto.ModuleUIInfo;
import com.baomidou.plugin.idea.mybatisx.generate.dto.TemplateAnnotationType;
import com.baomidou.plugin.idea.mybatisx.generate.dto.TemplateSettingDTO;
import com.baomidou.plugin.idea.mybatisx.generate.util.DomainPlaceHolder;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.intellij.openapi.actionSystem.ActionToolbarPosition;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.ChooseModulesDialog;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.TableView;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.util.PlatformIcons;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CodeGenerateUI {
    private JPanel rootPanel;
    private JCheckBox commentCheckBox;
    private JCheckBox lombokCheckBox;
    private JCheckBox actualColumnCheckBox;
    private JCheckBox JSR310DateAPICheckBox;
    private JCheckBox useActualColumnAnnotationInjectCheckBox;

    private JCheckBox toStringHashCodeEqualsCheckBox;
    private JPanel containingPanel;
    private JRadioButton JPARadioButton;
    private JRadioButton mybatisPlus3RadioButton;
    private JRadioButton mybatisPlus2RadioButton;
    private JRadioButton noneRadioButton;
    private JPanel templateExtraPanel;
    private JPanel templateExtraRadiosPanel;

    private ButtonGroup templateButtonGroup = new ButtonGroup();

    ListTableModel<ModuleUIInfo> model = new ListTableModel<>(
        new MybaitsxModuleInfo("config name", 120, false),
        new MybaitsxModuleInfo("module path", 420, true, true),
        new MybaitsxModuleInfo("base path", 140, true),
        new MybaitsxModuleInfo("package name", 160, true)
    );
    private Project project;
    private DomainInfo domainInfo;
    private String selectedTemplateName;

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public void fillData(Project project,
                         GenerateConfig generateConfig,
                         String defaultsTemplatesName,
                         Map<String, List<TemplateSettingDTO>> templateSettingMap) {
        this.project = project;

        // 选择注解还是XML
        selectAnnotation(generateConfig.getAnnotationType());
        // 是否需要字段注释
        if (generateConfig.isNeedsComment()) {
            commentCheckBox.setSelected(true);
        }
        // 需要生成 toString/hashcode/equals
        if (generateConfig.isNeedToStringHashcodeEquals()) {
            toStringHashCodeEqualsCheckBox.setSelected(true);
        }
        // 使用lombok 注解生成实体类
        if (generateConfig.isUseLombokPlugin()) {
            lombokCheckBox.setSelected(true);
        }
        // 使用数据库的字段作为列名
        if (generateConfig.isUseActualColumns()) {
            actualColumnCheckBox.setSelected(true);
        }
        // 使用LocalDate
        if (generateConfig.isJsr310Support()) {
            JSR310DateAPICheckBox.setSelected(true);
        }
        // 使用实际的列名注解
        if (generateConfig.isUseActualColumnAnnotationInject()) {
            useActualColumnAnnotationInjectCheckBox.setSelected(true);
        }

        // 初始化表格, 用于展示要生成的文件模块
        initTemplates(generateConfig, defaultsTemplatesName, templateSettingMap);

        selectedTemplateName = generateConfig.getTemplatesName();
    }

    private void selectDefaultTemplateRadio(String templatesName) {
        // 选择默认的模板, 或者记忆的模板
        if (StringUtils.isEmpty(templatesName)) {
            final Enumeration<AbstractButton> elements = templateButtonGroup.getElements();
            if (elements.hasMoreElements()) {
                final AbstractButton abstractButton = elements.nextElement();
                abstractButton.setSelected(true);
            }
        } else {
            final Enumeration<AbstractButton> elements = templateButtonGroup.getElements();
            while (elements.hasMoreElements()) {
                final AbstractButton abstractButton = elements.nextElement();
                final String text = abstractButton.getText();
                if (templatesName.equals(text)) {
                    abstractButton.setSelected(true);
                    break;
                }
            }
        }
    }

    private boolean refresh = false;

    private void initTemplates(GenerateConfig generateConfig, String defaultsTemplatesName, Map<String, List<TemplateSettingDTO>> templateSettingMap) {

        TableView<ModuleUIInfo> tableView = new TableView<>(model);

        GridConstraints gridConstraints = new GridConstraints();
        gridConstraints.setFill(GridConstraints.FILL_HORIZONTAL);

        templateExtraPanel.add(ToolbarDecorator.createDecorator(tableView)
            .setToolbarPosition(ActionToolbarPosition.LEFT)
            .addExtraAction(new AnActionButton("Refresh Template", PlatformIcons.SYNCHRONIZE_ICON) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    AbstractButton selectedTemplateName = findSelectedTemplateName();
                    if (selectedTemplateName == null) {
                        return;
                    }
                    List<TemplateSettingDTO> templateSettingDTOS = templateSettingMap.get(selectedTemplateName.getText());
                    if (templateSettingDTOS == null) {
                        return;
                    }
                    initSelectedModuleTable(templateSettingDTOS, domainInfo.getModulePath());
                    refresh = true;
                }

            })
            .disableAddAction()
            .disableUpDownActions()
            .setPreferredSize(new Dimension(840, 150))
            .createPanel(), gridConstraints);

        // N 行 3 列 的排版模式
        GridLayout templateRadioLayout = new GridLayout(0, 3, 0, 0);
        templateExtraRadiosPanel.setLayout(templateRadioLayout);
        // 添加动态模板组
        for (String templateName : templateSettingMap.keySet()) {
            JRadioButton comp = new JRadioButton();
            comp.setText(templateName);
            templateButtonGroup.add(comp);
            templateExtraRadiosPanel.add(comp);
        }

        final ItemListener itemListener = new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                final JRadioButton jRadioButton = (JRadioButton) e.getItem();
                // 只接受选择事件
                if (e.getStateChange() != ItemEvent.SELECTED) {
                    return;
                }
                String templatesName = jRadioButton.getText();
                List<TemplateSettingDTO> list = buildTemplatesSettings(templatesName);
                List<ModuleUIInfo> moduleUIInfoList = buildModuleUIInfos(templatesName, list);
                initMemoryModuleTable(moduleUIInfoList);
            }

            private List<ModuleUIInfo> buildModuleUIInfos(String templatesName, List<TemplateSettingDTO> list) {
                List<ModuleUIInfo> moduleUIInfoList = null;
                // 1. 优先选择默认的
                if (!refresh && templatesName.equals(generateConfig.getTemplatesName()) ) {
                    moduleUIInfoList = generateConfig.getModuleUIInfoList();
                }
                // 2. 其次根据选择的模板名称来决定使用哪个模板
                if (moduleUIInfoList == null) {
                    moduleUIInfoList = buildByTemplates(list, domainInfo.getModulePath());
                }
                return moduleUIInfoList;
            }

            private List<TemplateSettingDTO> buildTemplatesSettings(String templatesName) {
                List<TemplateSettingDTO> list = null;
                // 选择选定的模板
                if (!StringUtils.isEmpty(templatesName)) {
                    list = templateSettingMap.get(templatesName);
                }
                // 选择默认模板
                if (list == null && !StringUtils.isEmpty(defaultsTemplatesName)) {
                    list = templateSettingMap.get(defaultsTemplatesName);
                }
                // 默认模板没有设置, 或者默认模板改了新的名字, 找到values的第一条记录
                if (list == null) {
                    list = templateSettingMap.values().iterator().next();
                }
                return list;
            }

            private List<ModuleUIInfo> buildByTemplates(List<TemplateSettingDTO> list, String modulePath) {
                List<ModuleUIInfo> moduleUIInfoList = new ArrayList<>(list.size());
                // 添加列的内容
                for (TemplateSettingDTO templateSettingDTO : list) {
                    ModuleUIInfo item = new ModuleUIInfo();
                    item.setConfigName(templateSettingDTO.getConfigName());
                    // 默认使用实体模块的模块路径
                    item.setModulePath(modulePath);
                    item.setBasePath(templateSettingDTO.getBasePath());
                    item.setFileName(templateSettingDTO.getFileName());
                    item.setFileNameWithSuffix(templateSettingDTO.getFileName() + templateSettingDTO.getSuffix());
                    item.setPackageName(templateSettingDTO.getPackageName());
                    item.setEncoding(templateSettingDTO.getEncoding());
                    moduleUIInfoList.add(item);
                }
                return moduleUIInfoList;
            }
        };

        final Enumeration<AbstractButton> radios = templateButtonGroup.getElements();
        while (radios.hasMoreElements()) {
            final JRadioButton radioButton = (JRadioButton) radios.nextElement();
            radioButton.addItemListener(itemListener);
        }
    }

    private AbstractButton findSelectedTemplateName() {
        AbstractButton selectedButton = null;
        Enumeration<AbstractButton> elements = templateButtonGroup.getElements();
        while (elements.hasMoreElements()) {
            AbstractButton abstractButton = elements.nextElement();
            if (abstractButton.isSelected()) {
                selectedButton = abstractButton;
            }
        }
        return selectedButton;
    }

    private void initMemoryModuleTable(List<ModuleUIInfo> list) {
        // 扩展面板的列表内容
        // 移除所有行, 重新刷新
        for (int rowCount = model.getRowCount(); rowCount > 0; rowCount--) {
            model.removeRow(rowCount - 1);
        }

        // 添加列的内容
        for (ModuleUIInfo item : list) {
            model.addRow(item);
        }

    }

    /**
     * 初始化默认的模块选择表格
     *
     * @param list
     * @param modulePath
     */
    private void initSelectedModuleTable(List<TemplateSettingDTO> list, String modulePath) {
        // 扩展面板的列表内容
        // 移除所有行, 重新刷新
        for (int rowCount = model.getRowCount(); rowCount > 0; rowCount--) {
            model.removeRow(rowCount - 1);
        }

        // 添加列的内容
        for (TemplateSettingDTO templateSettingDTO : list) {
            ModuleUIInfo item = new ModuleUIInfo();
            item.setConfigName(templateSettingDTO.getConfigName());
            // 默认使用实体模块的模块路径
            item.setModulePath(modulePath);
            item.setBasePath(templateSettingDTO.getBasePath());
            item.setFileName(templateSettingDTO.getFileName());
            item.setFileNameWithSuffix(templateSettingDTO.getFileName() + templateSettingDTO.getSuffix());
            item.setPackageName(templateSettingDTO.getPackageName());
            item.setEncoding(templateSettingDTO.getEncoding());
            model.addRow(item);
        }

    }

    public void fillDomainInfo(DomainInfo domainInfo) {
        this.domainInfo = domainInfo;
        selectDefaultTemplateRadio(selectedTemplateName);
    }

    private class MybaitsxModuleInfo extends ColumnInfo<ModuleUIInfo, String> {

        public MybaitsxModuleInfo(String name, int width, boolean editable) {
            super(name);
            this.width = width;
            this.editable = editable;
        }

        public MybaitsxModuleInfo(String name, int width, boolean editable, boolean moduleEditor) {
            super(name);
            this.width = width;
            this.editable = editable;
            this.moduleEditor = moduleEditor;
        }

        private int width;
        private boolean editable;
        private boolean moduleEditor;

        @Override
        public boolean isCellEditable(ModuleUIInfo moduleUIInfo) {
            return editable;
        }

        @Override
        public int getWidth(JTable table) {
            return width;
        }

        @Nullable
        @Override
        public TableCellRenderer getRenderer(ModuleUIInfo moduleUIInfo) {
            return new DefaultTableCellRenderer();
        }

        private void chooseModule(JTextField textField,ModuleUIInfo moduleUIInfo) {
            Module[] modules = ModuleManager.getInstance(project).getModules();
            ChooseModulesDialog dialog = new ChooseModulesDialog(project, Arrays.asList(modules), "Choose Module", "Choose Single Module");
            dialog.setSingleSelectionMode();
            dialog.show();

            List<Module> chosenElements = dialog.getChosenElements();
            if (chosenElements.size() > 0) {
                Module module = chosenElements.get(0);
                String modulePath = findModulePath(module);
                textField.setText(modulePath);
                setValue(moduleUIInfo, modulePath);
            }
        }

        @Nullable
        @Override
        public TableCellEditor getEditor(ModuleUIInfo moduleUIInfo) {
            JTextField textField = new JTextField();
            if (moduleEditor) {
                // 模块选择
                textField.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        chooseModule(textField,moduleUIInfo);
                    }
                });
            }

            DefaultCellEditor defaultCellEditor = new DefaultCellEditor(textField);
            defaultCellEditor.addCellEditorListener(new CellEditorListener() {
                @Override
                public void editingStopped(ChangeEvent e) {
                    String s = defaultCellEditor.getCellEditorValue().toString();
                    if (getName().equals("module path")) {
                        moduleUIInfo.setModulePath(s);
                    } else if (getName().equals("base path")) {
                        moduleUIInfo.setBasePath(s);
                    } else if (getName().equals("package name")) {
                        moduleUIInfo.setPackageName(s);
                    }
                }

                @Override
                public void editingCanceled(ChangeEvent e) {

                }
            });
            return defaultCellEditor;
        }

        private String findModulePath(Module module) {
            String moduleDirPath = ModuleUtil.getModuleDirPath(module);
            int ideaIndex = moduleDirPath.indexOf(".idea");
            if (ideaIndex > -1) {
                moduleDirPath = moduleDirPath.substring(0, ideaIndex);
            }
            return moduleDirPath;
        }

        @Nullable
        @Override
        public String valueOf(ModuleUIInfo item) {
            String value = null;
            if (getName().equals("config name")) {
                value = item.getConfigName();
            } else if (getName().equals("module path")) {
                value = DomainPlaceHolder.replace(item.getModulePath(), domainInfo);
            } else if (getName().equals("base path")) {
                value = DomainPlaceHolder.replace(item.getBasePath(), domainInfo);
            } else if (getName().equals("package name")) {
                value = DomainPlaceHolder.replace(item.getPackageName(), domainInfo);
            }
            return value;
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

    public void refreshGenerateConfig(GenerateConfig generateConfig) {

        List<ModuleUIInfo> moduleUIInfoList = IntStream.range(0, model.getRowCount())
            .mapToObj(index -> model.getRowValue(index))
            .collect(Collectors.toList());
        generateConfig.setModuleUIInfoList(moduleUIInfoList);
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
        generateConfig.setUseActualColumnAnnotationInject(useActualColumnAnnotationInjectCheckBox.isSelected());
        generateConfig.setJsr310Support(JSR310DateAPICheckBox.isSelected());


        String annotationTypeName = findAnnotationType();
        generateConfig.setAnnotationType(annotationTypeName);

        String templatesName = null;
        final Enumeration<AbstractButton> elements = templateButtonGroup.getElements();
        while (elements.hasMoreElements()) {
            final AbstractButton abstractButton = elements.nextElement();
            if (abstractButton.isSelected()) {
                templatesName = abstractButton.getText();
                break;
            }
        }
        generateConfig.setTemplatesName(templatesName);

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
