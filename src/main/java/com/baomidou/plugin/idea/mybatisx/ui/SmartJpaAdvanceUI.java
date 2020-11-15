package com.baomidou.plugin.idea.mybatisx.ui;

import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Smart jpa advance ui.
 */
public class SmartJpaAdvanceUI {
    /**
     * The Condition panel.
     */
    public JPanel conditionPanel;
    private JPanel rootPanel;
    private JCheckBox checkAllConditionField;
    private JTextField baseResultMapTextField;
    private JTextField resultMapClassTextField;
    private JRadioButton includeRadioButton;
    private JRadioButton allColumnRadioButton;
    private JRadioButton asFieldRadioButton;
    private JTextField baseColumnListTextField;
    private JTextArea columnsTextArea;
    private JRadioButton xmlGenerateType;
    private JRadioButton annotationScriptType;
    private JTextField defaultDateTextField;
    private JScrollPane fieldsScrollPanel;
    private List<TxField> allFields;

    private boolean resultType = false;

    /**
     * Instantiates a new Smart jpa advance ui.
     */
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

        includeRadioButton.addItemListener(e -> {
            includeAllResults();
        });

        allColumnRadioButton.addItemListener(e -> {
            String collect = allFields.stream().map(TxField::getColumnName).collect(Collectors.joining(",\n"));
            columnsTextArea.setText(collect);
            resultType = false;
        });
        asFieldRadioButton.addItemListener(e -> {
            String collect = allFields.stream().map(x -> {
                // 没有驼峰命名, 就不需要 as
                if (x.getColumnName().equals(x.getFieldName())) {
                    return x.getColumnName();
                } else {
                    // column as field
                    return x.getColumnName() + " as " + x.getFieldName();
                }
            }).collect(Collectors.joining(",\n"));
            columnsTextArea.setText(collect);
            resultType = true;
        });

        // 假装执行了选中 includeAllRadio的事件
        includeAllResults();
    }

    private void includeAllResults() {
        String includeSql = "<include refid=\"" + baseColumnListTextField.getText() + "\"/>";
        columnsTextArea.setText(includeSql);
        resultType = false;
    }


    /**
     * Gets generator type.
     *
     * @return the generator type
     */
    public GeneratorEnum getGeneratorType() {
        if (annotationScriptType.isSelected()) {
            return GeneratorEnum.MYBATIS_ANNOTATION;
        }else if (xmlGenerateType.isSelected()) {
            return GeneratorEnum.MYBATIS_XML;
        }
        return GeneratorEnum.MYBATIS_XML;
    }

    /**
     * Gets root panel.
     *
     * @return the root panel
     */
    public JPanel getRootPanel() {
        return rootPanel;
    }

    /**
     * Gets condition panel.
     *
     * @return the condition panel
     */
    public JPanel getConditionPanel() {
        return conditionPanel;
    }

    /**
     * Gets check all condition field.
     *
     * @return the check all condition field
     */
    public JCheckBox getCheckAllConditionField() {
        return checkAllConditionField;
    }

    /**
     * Gets all fields text.
     *
     * @return the all fields text
     */
    public String getAllFieldsText() {
        return columnsTextArea.getText();
    }

    /**
     * Init result fields.
     *
     * @param allFields the all fields
     */
    public void initResultFields(List<TxField> allFields) {
        this.allFields = allFields;
    }

    /**
     * Gets result map.
     *
     * @return the result map
     */
    public String getResultMap() {
        return baseResultMapTextField.getText();
    }

    /**
     * Gets result type class.
     *
     * @return the result type class
     */
    public String getResultTypeClass() {
        return resultMapClassTextField.getText();
    }

    /**
     * Is result type boolean.
     *
     * @return the boolean
     */
    public boolean isResultType() {
        return resultType;
    }

    /**
     * Sets result type.
     *
     * @param entityClass the entity class
     */
    public void setResultType(String entityClass) {
        resultMapClassTextField.setText(entityClass);
    }


    /**
     * The enum Generator enum.
     */
    public enum GeneratorEnum {
        /**
         * Mybatis xml generator enum.
         */
        MYBATIS_XML,
        /**
         * Mybatis annotation generator enum.
         */
        MYBATIS_ANNOTATION,
    }

    public List<String> getDefaultDate() {
        String text = defaultDateTextField.getText();
        if (StringUtils.isEmpty(text)) {
            return Collections.emptyList();
        }
        return Arrays.stream(text.split(",")).map(String::trim).collect(Collectors.toList());
    }
}
