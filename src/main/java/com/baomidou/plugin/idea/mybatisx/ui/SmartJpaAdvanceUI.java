package com.baomidou.plugin.idea.mybatisx.ui;

import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.util.CollectionUtils;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.Nullable;

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
    private JRadioButton includeRadioButton;
    private JRadioButton allColumnRadioButton;
    private JRadioButton asFieldRadioButton;
    private JTextArea columnsTextArea;
    private JRadioButton xmlGenerateType;
    private JRadioButton annotationScriptType;
    private JTextField defaultDateTextField;
    private JScrollPane fieldsScrollPanel;
    private JRadioButton radioUseEntityClass;
    private JRadioButton radioCreateCustomClass;
    private List<TxField> allFields;

    private boolean resultType = false;
    private List<String> resultFields;
    private PsiClass entityClass;

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
        String baseColumnList = "BaseColumnList";
        includeRadioButton.addItemListener(e -> {
            includeAllResults(baseColumnList);
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

        radioCreateCustomClass.addItemListener(e -> {
            this.useDefaultEntityClass = false;
        });
        radioUseEntityClass.addItemListener(e -> {
            this.useDefaultEntityClass = true;
        });

        // 假装执行了选中 includeAllRadio的事件
        includeAllResults(baseColumnList);
    }

    private boolean useDefaultEntityClass = true;

    @Nullable
    private String getEntityClassName(PsiClass entityClass) {
        String qualifiedName = null;
        if (useDefaultEntityClass) {
            qualifiedName = entityClass.getQualifiedName();
        }
        if (qualifiedName == null) {
            if (CollectionUtils.isNotEmpty(resultFields)) {
                String collect = String.join("", resultFields);
                qualifiedName = entityClass.getQualifiedName() + collect + "DTO";
            }
        }
        if (qualifiedName == null) {
            qualifiedName = entityClass.getQualifiedName();
        }
        return qualifiedName;
    }

    private void includeAllResults(String columnNames) {
        String includeSql = "<include refid=\"" + columnNames + "\"/>";
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
        } else if (xmlGenerateType.isSelected()) {
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
     * Gets result map.
     *
     * @return the result map
     */
    public String getResultMap() {
        String qualifiedName = null;
        if (useDefaultEntityClass) {
            qualifiedName = "BaseResultMap";
        }
        if (qualifiedName == null) {
            if (CollectionUtils.isNotEmpty(resultFields)) {
                String collect = String.join("", resultFields);
                qualifiedName = entityClass.getName() + collect + "Map";
            }
        }
        if (qualifiedName == null) {
            qualifiedName = "BaseResultMap";
        }
        return qualifiedName;
    }

    /**
     * Gets result type class.
     *
     * @return the result type class
     */
    public String getResultTypeClass() {
        return getEntityClassName(entityClass);
    }

    /**
     * Is result type boolean.
     *
     * @return the boolean
     */
    public boolean isResultType() {
        return resultType;
    }

    public void init(List<TxField> allFields, PsiClass entityClass, List<String> resultFields) {
        this.allFields = allFields;
        this.resultFields = resultFields;
        this.entityClass = entityClass;
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
