package com.baomidou.plugin.idea.mybatisx.generate.ui;

import com.baomidou.plugin.idea.mybatisx.generate.dto.DomainInfo;
import com.baomidou.plugin.idea.mybatisx.generate.dto.GenerateConfig;
import com.baomidou.plugin.idea.mybatisx.generate.dto.TableUIInfo;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.intellij.database.psi.DbTable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.ChooseModulesDialog;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

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
        return domainInfo;

    }

    private String calculateClassName(String tableName, String ignorePrefixs, String ignoreSuffixs) {
        String fName = tableName;
        final String EMPTY = "";
        if (!StringUtils.isEmpty(ignorePrefixs)) {
            String[] splitPrefixs = ignorePrefixs.split(",");
            for (String ignorePrefix : splitPrefixs) {
                fName = fName.replaceAll("^" + ignorePrefix, EMPTY);
            }
        }
        if (!StringUtils.isEmpty(ignoreSuffixs)) {
            String[] splitSuffixs = ignoreSuffixs.split(",");
            for (String ignoreSuffix : splitSuffixs) {
                fName = fName.replaceFirst(ignoreSuffix+"$", EMPTY);
            }
        }
        return StringUtils.dbStringToCamelStyle(fName);
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public void fillData(Project project, List<DbTable> dbTables, GenerateConfig generateConfig) {
        this.dbTables = dbTables;
        String ignorePrefix = generateConfig.getIgnoreTablePrefix();
        String ignoreSuffix = generateConfig.getIgnoreTableSuffix();

        refreshTableNames(dbTables, ignorePrefix, ignoreSuffix);

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
                refreshTableNames(dbTables, ignoreTablePrefixTextField.getText(), ignoreTableSuffixTextField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                refreshTableNames(dbTables, ignoreTablePrefixTextField.getText(), ignoreTableSuffixTextField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                refreshTableNames(dbTables, ignoreTablePrefixTextField.getText(), ignoreTableSuffixTextField.getText());
            }
        };
        ignoreTablePrefixTextField.getDocument().addDocumentListener(listener);

        ignoreTableSuffixTextField.getDocument().addDocumentListener(listener);
    }

    private void refreshTableNames(List<DbTable> dbTables, String ignorePrefix, String ignoreSuffix) {
        for (int currentRowIndex = model.getRowCount() - 1; currentRowIndex >= 0; currentRowIndex--) {
            model.removeRow(currentRowIndex);
        }
        for (DbTable dbTable : dbTables) {
            String tableName = dbTable.getName();
            String className = calculateClassName(tableName, ignorePrefix, ignoreSuffix);
            model.addRow(new TableUIInfo(tableName, className));
        }
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
        int ideaIndex = moduleDirPath.indexOf(".idea");
        if (ideaIndex > -1) {
            moduleDirPath = moduleDirPath.substring(0, ideaIndex);
        }
        moduleChooseTextField.setText(moduleDirPath);
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
        // 保存对象, 用于传递和对象生成
        generateConfig.setTableUIInfoList(model.getItems());
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
