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
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * The type Smart jpa advance ui.
 */
public class SmartJpaAdvanceUI {
    public static final String BASE_RESULT_MAP = "BaseResultMap";
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
    private JSpinner splitFieldSpiner;
    private List<TxField> allFields;

    private boolean resultType = false;
    private List<String> resultFields;
    private PsiClass entityClass;
    public static final String BASE_COLUMN_LIST = "Base_Column_List";

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
            includeAllResults(BASE_COLUMN_LIST);
        });

        allColumnRadioButton.addItemListener(e -> {
            final Object value = splitFieldSpiner.getValue();
            Integer splitNumber = Integer.valueOf(value.toString());
            // splitNumber = 0 表示不换行
            if (splitNumber <= 0) {
                splitNumber = 0;
            }
            StringJoiner stringJoiner = new StringJoiner(",");
            for (int i = 0; i < allFields.size(); i++) {
                final TxField txField = allFields.get(i);
                String columnName = txField.getColumnName();
                if (splitNumber > 0 && i > 0 && i % splitNumber == 0) {
                    columnName = "\n" + columnName;
                }
                stringJoiner.add(columnName);
            }
            columnsTextArea.setText(stringJoiner.toString());
            resultType = false;
        });
        asFieldRadioButton.addItemListener(e -> {

            final Object value = splitFieldSpiner.getValue();
            Integer splitNumber = Integer.valueOf(value.toString());
            // splitNumber = 0 表示不换行
            if (splitNumber <= 0) {
                splitNumber = 0;
            }

            StringJoiner stringJoiner = new StringJoiner(",");
            for (int i = 0; i < allFields.size(); i++) {
                final TxField txField = allFields.get(i);
                String columnName = null;
                // 没有驼峰命名, 就不需要 as
                if (txField.getColumnName().equals(txField.getFieldName())) {
                    columnName = txField.getColumnName();
                } else {
                    // column as field
                    columnName = txField.getColumnName() + " as " + txField.getFieldName();
                }

                if (splitNumber > 0 && i > 0 && i % splitNumber == 0) {
                    columnName = "\n" + columnName;
                }
                stringJoiner.add(columnName);
            }

            columnsTextArea.setText(stringJoiner.toString());
            resultType = true;
        });

        radioCreateCustomClass.addItemListener(e -> {
            resultExecutor = new CustomResultExecutor();
        });
        radioUseEntityClass.addItemListener(e -> {
            resultExecutor = new CommonResultExecutor();
        });

        // 假装执行了选中 includeAllRadio的事件
        includeAllResults(BASE_COLUMN_LIST);

        splitFieldSpiner.setValue(3);
    }

    @Nullable
    private String getEntityClassName(PsiClass entityClass) {
        return resultExecutor.getEntityClassName(entityClass);
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

    private ResultExecutor resultExecutor = new CommonResultExecutor();

    /**
     * Gets result map.
     *
     * @return the result map
     */
    public String getResultMap() {
        return resultExecutor.getResultMap();

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

    public List<String> getDefaultDate() {
        String text = defaultDateTextField.getText();
        if (StringUtils.isEmpty(text)) {
            return Collections.emptyList();
        }
        return Arrays.stream(text.split(",")).map(String::trim).collect(Collectors.toList());
    }

    public List<String> getResultFields() {
        return resultExecutor.getResultFields();
    }

    private abstract class ResultExecutor {

        protected abstract String getResultMap();

        public abstract String getEntityClassName(PsiClass entityClass);

        public abstract List<String> getResultFields();

    }

    private class CommonResultExecutor extends ResultExecutor {
        @Override
        public String getResultMap() {
            return BASE_RESULT_MAP;
        }

        @Override
        public String getEntityClassName(PsiClass entityClass) {
            return  entityClass.getQualifiedName();
        }

        @Override
        public List<String> getResultFields() {
            return Collections.emptyList();
        }
    }

    private class CustomResultExecutor extends ResultExecutor {

        @Override
        protected String getResultMap() {
            String qualifiedName = null;
            if (CollectionUtils.isNotEmpty(resultFields)) {
                String fields = String.join("", resultFields);
                qualifiedName = entityClass.getName() + fields + "Map";
            }
            if (qualifiedName == null) {
                qualifiedName = entityClass.getName() + "ExtMap";
            }
            return qualifiedName;
        }

        @Override
        public String getEntityClassName(PsiClass entityClass) {
            String qualifiedName = null;
            // 自定义一个新的类名称
            // 根据指定的字段生成一个新的类
            if (CollectionUtils.isNotEmpty(resultFields)) {
                String collect = String.join("", resultFields);
                qualifiedName = entityClass.getQualifiedName() + collect + "DTO";
            }
            // 根据原先的实体类复制出一个新的实体类的名称
            if (qualifiedName == null) {
                qualifiedName = entityClass.getQualifiedName() + "ExtDTO";
            }
            return qualifiedName;
        }

        @Override
        public List<String> getResultFields() {
            return allFields.stream().map(TxField::getFieldName).distinct().collect(Collectors.toList());
        }
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
}
