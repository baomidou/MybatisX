package com.baomidou.plugin.idea.mybatisx.generate.ui;

import com.baomidou.plugin.idea.mybatisx.generate.classname.CamelStrategy;
import com.baomidou.plugin.idea.mybatisx.generate.classname.ClassNameStrategy;
import com.baomidou.plugin.idea.mybatisx.generate.classname.SameAsTableNameStrategy;
import com.baomidou.plugin.idea.mybatisx.generate.dto.DomainInfo;
import com.baomidou.plugin.idea.mybatisx.generate.dto.GenerateConfig;
import com.baomidou.plugin.idea.mybatisx.generate.dto.TableUIInfo;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.intellij.database.psi.DbTable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ui.configuration.ChooseModulesDialog;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.TableView;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TablePreviewUI {
    private JPanel rootPanel;
    private JPanel listPanel;
    private JTextField ignoreTablePrefixTextField;
    private JTextField ignoreTableSuffixTextField;
    private JTextField fieldPrefixTextField;
    private JLabel lblFieldSuffix;
    private JTextField fieldSuffixTextField;
    private JTextField superClassTextField;
    private JTextField encodingTextField;
    private JTextField basePackageTextField;
    private JTextField relativePackageTextField;
    private JTextField basePathTextField;
    private JTextField moduleChooseTextField;
    private JPanel middlePanel;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JTextField extraClassSuffixTextField;
    private JLabel keepTableName;
    private JRadioButton camelRadioButton;
    private JRadioButton sameAsTablenameRadioButton;
    private JPanel classNameStrategyPanel;
    private PsiElement[] tableElements;
    private List<DbTable> dbTables;


    private String moduleName;

    ListTableModel<TableUIInfo> model = new ListTableModel<>(
        new MybaitsxTableColumnInfo("tableName", false),
        new MybaitsxTableColumnInfo("className", true)
    );

    public TablePreviewUI() {
        TableView<TableUIInfo> tableView = new TableView<>(model);
        GridConstraints gridConstraints = new GridConstraints();
        gridConstraints.setFill(GridConstraints.FILL_HORIZONTAL);

        listPanel.add(ToolbarDecorator.createDecorator(tableView)
                .setPreferredSize(new Dimension(860, 200))
                .disableAddAction()
                .disableRemoveAction()
                .disableUpDownActions()
                .createPanel(),
            gridConstraints);


    }


    public DomainInfo buildDomainInfo() {
        DomainInfo domainInfo = new DomainInfo();
        domainInfo.setModulePath(moduleChooseTextField.getText());
        domainInfo.setBasePath(basePathTextField.getText());
        domainInfo.setBasePackage(basePackageTextField.getText());
        domainInfo.setRelativePackage(relativePackageTextField.getText());
        domainInfo.setEncoding(encodingTextField.getText());
        // 放一个自己名字的引用
        domainInfo.setFileName("${domain.fileName}");
        return domainInfo;

    }


    public JPanel getRootPanel() {
        return rootPanel;
    }

    public void fillData(Project project, List<DbTable> dbTables, GenerateConfig generateConfig) {
        this.dbTables = dbTables;
        String ignorePrefix = generateConfig.getIgnoreTablePrefix();
        String ignoreSuffix = generateConfig.getIgnoreTableSuffix();

        String classNameStrategy = generateConfig.getClassNameStrategy();
        selectClassNameStrategyByName(findStrategyByName(classNameStrategy));
        refreshTableNames(classNameStrategy, dbTables, ignorePrefix, ignoreSuffix);

        ignoreTablePrefixTextField.setText(generateConfig.getIgnoreTablePrefix());
        ignoreTableSuffixTextField.setText(generateConfig.getIgnoreTableSuffix());
        fieldPrefixTextField.setText(generateConfig.getIgnoreFieldPrefix());
        fieldSuffixTextField.setText(generateConfig.getIgnoreFieldSuffix());
        superClassTextField.setText(generateConfig.getSuperClass());
        encodingTextField.setText(generateConfig.getEncoding());
        basePackageTextField.setText(generateConfig.getBasePackage());
        basePathTextField.setText(generateConfig.getBasePath());
        relativePackageTextField.setText(generateConfig.getRelativePackage());
        extraClassSuffixTextField.setText(generateConfig.getExtraClassSuffix());
        moduleName = generateConfig.getModuleName();

        if (!StringUtils.isEmpty(moduleName)) {
            Module[] modules = ModuleManager.getInstance(project).getModules();
            for (Module module : modules) {
                if (module.getName().equals(moduleName)) {
                    chooseModulePath(module);
                }
            }
        }

        moduleChooseTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                chooseModule(project);
            }
        });

        DocumentListener listener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                refreshTableNames(classNameStrategy, dbTables, ignoreTablePrefixTextField.getText(), ignoreTableSuffixTextField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                refreshTableNames(classNameStrategy, dbTables, ignoreTablePrefixTextField.getText(), ignoreTableSuffixTextField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                refreshTableNames(classNameStrategy, dbTables, ignoreTablePrefixTextField.getText(), ignoreTableSuffixTextField.getText());
            }
        };

        final ItemListener classNameChangeListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                final Object source = e.getItem();
                if (!(source instanceof JRadioButton)) {
                    return;
                }
                JRadioButton radioButton = (JRadioButton) source;
                if (!radioButton.isSelected()) {
                    return;
                }
                refreshTableNames(findClassNameStrategy(), dbTables, ignorePrefix, ignoreSuffix);
            }
        };
        camelRadioButton.addItemListener(classNameChangeListener);
        sameAsTablenameRadioButton.addItemListener(classNameChangeListener);

        ignoreTablePrefixTextField.getDocument().addDocumentListener(listener);

        ignoreTableSuffixTextField.getDocument().addDocumentListener(listener);
    }


    private void refreshTableNames(String classNameStrategyName, List<DbTable> dbTables, String ignorePrefix, String ignoreSuffix) {
        for (int currentRowIndex = model.getRowCount() - 1; currentRowIndex >= 0; currentRowIndex--) {
            model.removeRow(currentRowIndex);
        }
        ClassNameStrategy classNameStrategy = findStrategyByName(classNameStrategyName);
        for (DbTable dbTable : dbTables) {
            String tableName = dbTable.getName();
            String className = classNameStrategy.calculateClassName(tableName, ignorePrefix, ignoreSuffix);
            model.addRow(new TableUIInfo(tableName, className));
        }
    }

    List<ClassNameStrategy> classNameStrategies = new ArrayList<ClassNameStrategy>(){
        {
            add(new CamelStrategy());
            add(new SameAsTableNameStrategy());
        }
    };
    private ClassNameStrategy findStrategyByName(String classNameStrategyName) {
        ClassNameStrategy classNameStrategy = null;
        for (ClassNameStrategy nameStrategy : classNameStrategies) {
            if (nameStrategy.getText().equals(classNameStrategyName)) {
                classNameStrategy = nameStrategy;
                break;
            }
        }
        // 策略为空, 或者不是SAME的, 统统都是驼峰命名
        if (classNameStrategy == null) {
            classNameStrategy = new CamelStrategy();
        }
        return classNameStrategy;
    }

    private void chooseModule(Project project) {
        Module[] modules = ModuleManager.getInstance(project).getModules();
        ChooseModulesDialog dialog = new ChooseModulesDialog(project, Arrays.asList(modules), "Choose Module", "Choose Single Module");
        dialog.setSingleSelectionMode();
        dialog.show();

        List<Module> chosenElements = dialog.getChosenElements();
        if (chosenElements.size() > 0) {
            Module module = chosenElements.get(0);
            chooseModulePath(module);
            moduleName = module.getName();
        }
    }

    private void chooseModulePath(Module module) {

        String moduleDirPath = ModuleUtil.getModuleDirPath(module);
        int childModuleIndex = indexFromChildModule(moduleDirPath);
        if (hasChildModule(childModuleIndex)) {
            Optional<String> pathFromModule = getPathFromModule(module);
            if(pathFromModule.isPresent()){
                moduleDirPath = pathFromModule.get();
            }else{
                moduleDirPath = moduleDirPath.substring(0, childModuleIndex);
            }
        }

        moduleChooseTextField.setText(moduleDirPath);
    }

    private boolean hasChildModule(int childModuleIndex) {
        return childModuleIndex > -1;
    }

    private int indexFromChildModule(String moduleDirPath) {
        return moduleDirPath.indexOf(".idea");
    }

    private Optional<String> getPathFromModule(Module module) {
        // 兼容gradle获取子模块
        VirtualFile[] contentRoots = ModuleRootManager.getInstance(module).getContentRoots();
        if(contentRoots.length == 1){
            return  Optional.ofNullable(contentRoots[0].getPath());
        }
        return Optional.empty();
    }

    public void refreshGenerateConfig(GenerateConfig generateConfig) {
        generateConfig.setIgnoreTablePrefix(ignoreTablePrefixTextField.getText());
        generateConfig.setIgnoreTableSuffix(ignoreTableSuffixTextField.getText());
        generateConfig.setIgnoreFieldPrefix(fieldPrefixTextField.getText());
        generateConfig.setIgnoreFieldSuffix(fieldSuffixTextField.getText());
        generateConfig.setSuperClass(superClassTextField.getText());
        generateConfig.setEncoding(encodingTextField.getText());
        generateConfig.setBasePackage(basePackageTextField.getText());
        generateConfig.setBasePath(basePathTextField.getText());
        generateConfig.setRelativePackage(relativePackageTextField.getText());
        generateConfig.setModulePath(moduleChooseTextField.getText());
        generateConfig.setModuleName(moduleName);
        generateConfig.setExtraClassSuffix(extraClassSuffixTextField.getText());
        generateConfig.setClassNameStrategy(findClassNameStrategy());
        // 保存对象, 用于传递和对象生成
        generateConfig.setTableUIInfoList(model.getItems());
    }

    private void selectClassNameStrategyByName(ClassNameStrategy classNameStrategy) {
        for (Component component : classNameStrategyPanel.getComponents()) {
            if (component instanceof JRadioButton) {
                JRadioButton radioButton = (JRadioButton) component;
                if (radioButton.getText().equals(classNameStrategy.getText())) {
                    radioButton.setSelected(true);
                    break;
                }
            }
        }
    }

    private String findClassNameStrategy() {
        String name = null;
        for (Component component : classNameStrategyPanel.getComponents()) {
            if (component instanceof JRadioButton) {
                JRadioButton radioButton = (JRadioButton) component;
                if (radioButton.isSelected()) {
                    name = radioButton.getText();
                    break;
                }
            }
        }
        return name;
    }

    private static class MybaitsxTableColumnInfo extends ColumnInfo<TableUIInfo, String> {

        private boolean editable;

        public MybaitsxTableColumnInfo(String name, boolean editable) {
            super(name);
            this.editable = editable;
        }

        @Override
        public boolean isCellEditable(TableUIInfo tableUIInfo) {
            return editable;
        }

        @Nullable
        @Override
        public TableCellEditor getEditor(TableUIInfo tableUIInfo) {
            DefaultCellEditor defaultCellEditor = new DefaultCellEditor(new JTextField(getName()));
            defaultCellEditor.addCellEditorListener(new CellEditorListener() {
                @Override
                public void editingStopped(ChangeEvent e) {
                    Object cellEditorValue = defaultCellEditor.getCellEditorValue();
                    tableUIInfo.setClassName(cellEditorValue.toString());
                }

                @Override
                public void editingCanceled(ChangeEvent e) {

                }
            });
            return defaultCellEditor;
        }

        @Nullable
        @Override
        public String valueOf(TableUIInfo item) {
            String value = null;
            if (getName().equals("tableName")) {
                value = item.getTableName();
            } else if (getName().equals("className")) {
                value = item.getClassName();
            }
            return value;
        }
    }
}
