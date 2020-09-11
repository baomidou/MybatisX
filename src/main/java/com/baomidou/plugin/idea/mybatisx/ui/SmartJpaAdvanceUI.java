package com.baomidou.plugin.idea.mybatisx.ui;

import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class SmartJpaAdvanceUI {
    public JPanel conditionPanel;
    private JPanel rootPanel;
    private JCheckBox checkAllConditionField;
    private JComboBox methodCombo;
    private JTextField baseResultMapTextField;
    private JTextField resultMapClassTextField;
    private JRadioButton includeRadioButton;
    private JRadioButton allColumnRadioButton;
    private JEditorPane selectedFieldsText;
    private JRadioButton asFieldRadioButton;
    private JTextField baseColumnListTextField;
    private List<TxField> allFields;

    private boolean resultType = false;

    public SmartJpaAdvanceUI() {
        checkAllConditionField.addChangeListener(e -> {
            // 将条件区的选择状态变更
            JCheckBox mainCheck = (JCheckBox) e.getSource();
            Component[] components = conditionPanel.getComponents();
            for (Component component : components) {
                if (component instanceof JCheckBox) {
                    JCheckBox itemCheckBox = (JCheckBox) component;
                    itemCheckBox.setSelected(mainCheck.isSelected());
                }
            }
        });


        includeRadioButton.addActionListener(e -> {
            String includeSql = "<include refid=\"" + baseColumnListTextField.getText() + "\"/>";
            selectedFieldsText.setText(includeSql);
            resultType = false;
        });
        allColumnRadioButton.addActionListener(e -> {
            String collect = allFields.stream().map(TxField::getColumnName).collect(Collectors.joining(" ,"));
            selectedFieldsText.setText(collect);
            resultType = false;
        });
        asFieldRadioButton.addActionListener(e -> {
            String collect = allFields.stream().map(x -> {
                // 没有驼峰命名, 就不需要 as
                if (x.getColumnName().equals(x.getFieldName())) {
                    return x.getColumnName();
                } else {
                    // column as field
                    return x.getColumnName() + " as " + x.getFieldName();
                }
            }).collect(Collectors.joining(" ,"));
            selectedFieldsText.setText(collect);
            resultType = true;
        });


    }

    public GeneratorEnum getGenerator() {
        if (methodCombo.getSelectedIndex() == 1) {
            return GeneratorEnum.MYBATIS_ANNOTATION;
        }
        return GeneratorEnum.MYBATIS_XML;
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public JPanel getConditionPanel() {
        return conditionPanel;
    }

    public JCheckBox getCheckAllConditionField() {
        return checkAllConditionField;
    }

    public String getAllFieldsText() {
        return selectedFieldsText.getText();
    }

    public void initResultFields(List<TxField> allFields) {
        this.allFields = allFields;
    }

    public String getResultMap() {
        return baseResultMapTextField.getText();
    }

    public String getResultTypeClass() {
        return resultMapClassTextField.getText();
    }

    public boolean isResultType() {
        return resultType;
    }

    public void setResultType(String entityClass) {
        resultMapClassTextField.setText(entityClass);
    }

    public enum GeneratorEnum {
        MYBATIS_XML,
        MYBATIS_ANNOTATION,
    }
}
