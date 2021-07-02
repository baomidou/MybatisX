package com.baomidou.plugin.idea.mybatisx.generate.ui;

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
                .setPreferredSize(new Dimension(760, 200))
                .disableAddAction()
                .disableRemoveAction()
                .disableUpDownActions()
                .createPanel(),
            gridConstraints);


    }

    private String calculateClassName(String tableName, String ignorePrefix, String ignoreSuffix) {
        String fName = tableName;
        if (!StringUtils.isEmpty(ignorePrefix)) {
            if (fName.startsWith(ignorePrefix)) {
                fName = fName.substring(ignorePrefix.length());
            }
        }
        if (!StringUtils.isEmpty(ignoreSuffix)) {
            if (fName.endsWith(ignoreSuffix)) {
                fName = fName.substring(0, fName.length() - ignoreSuffix.length());
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

        for (DbTable dbTable : dbTables) {
            String tableName = dbTable.getName();
            String className = calculateClassName(tableName, ignorePrefix, ignoreSuffix);
            model.addRow(new TableUIInfo(tableName, className));
        }

        ignoreTablePrefixTextField.setText(generateConfig.getIgnoreTablePrefix());
        ignoreTableSuffixTextField.setText(generateConfig.getIgnoreTableSuffix());
        fieldPrefixTextField.setText(generateConfig.getIgnoreFieldPrefix());
        fieldSuffixTextField.setText(generateConfig.getIgnoreFieldSuffix());
        superClassTextField.setText(generateConfig.getSuperClass());
        encodingTextField.setText(generateConfig.getEncoding());
        basePackageTextField.setText(generateConfig.getBasePackage());
        basePathTextField.setText(generateConfig.getBasePath());
        relativePackageTextField.setText(generateConfig.getRelativePackage());
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
        });
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
