package com.baomidou.plugin.idea.mybatisx.ui;

import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JpaAdvanceDialog extends DialogWrapper {

    SmartJpaAdvanceUI smartJpaAdvanceUI = new SmartJpaAdvanceUI();

    public JpaAdvanceDialog(@Nullable Project project) {
        super(project);
        super.init();
        setTitle("Generate Options");


    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return smartJpaAdvanceUI.getRootPanel();
    }


    public Set<String> getSelectedFields() {
        Set<String> strings = new HashSet<>();
        Component[] components = smartJpaAdvanceUI.getConditionPanel().getComponents();
        for (Component component : components) {
            if (component instanceof JCheckBox) {
                JCheckBox checkBox = (JCheckBox) component;
                if (checkBox.isSelected()) {
                    strings.add(checkBox.getText());
                }
            }
        }
        return strings;
    }

    public String getAllFieldsStr(){
        return smartJpaAdvanceUI.getAllFieldsText();
    }
    private static final Logger logger = Logger.getInstance(JpaAdvanceDialog.class);

    public void initFields(List<String> conditionFields, List<TxField> allFields, String entityClass) {
        JPanel conditionPanel = smartJpaAdvanceUI.getConditionPanel();
        // 默认 5 列
        int columns =  5;
        int biggerColumn = (int)Math.sqrt(conditionFields.size());
        // 如果 列的总数大于25个, 则取 开根号之后的整形值
        columns = Math.max(biggerColumn,columns);
        int rows = conditionFields.size() / columns + (conditionFields.size() % columns == 0 ? 0 : 1);
        GridLayoutManager mgr = new GridLayoutManager(rows, columns);
        conditionPanel.setLayout(mgr);
        conditionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (int i = 0; i < conditionFields.size(); i++) {
            String conditionField = conditionFields.get(i);
            GridConstraints constraints = new GridConstraints();
            constraints.setRow(i / columns);
            constraints.setColumn(i % columns);
            constraints.setFill(GridConstraints.FILL_BOTH);
            JCheckBox checkBox = new JCheckBox(conditionField, true);
            conditionPanel.add(checkBox, constraints);
        }

        smartJpaAdvanceUI.initResultFields(allFields);
        smartJpaAdvanceUI.setResultType(entityClass);
    }

    public String getResultMap() {
        return smartJpaAdvanceUI.getResultMap();
    }


    public String getResultTypeClass() {
        return smartJpaAdvanceUI.getResultTypeClass();
    }
    public boolean isResultType(){
        return smartJpaAdvanceUI.isResultType();
    }
}
