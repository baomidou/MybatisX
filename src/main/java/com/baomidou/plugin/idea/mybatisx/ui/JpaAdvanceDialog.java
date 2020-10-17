package com.baomidou.plugin.idea.mybatisx.ui;

import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The type Jpa advance dialog.
 *
 * @author ls9527  自动生成的高级提示框
 */
public class JpaAdvanceDialog extends DialogWrapper {

    private SmartJpaAdvanceUI smartJpaAdvanceUI = new SmartJpaAdvanceUI();

    /**
     * Instantiates a new Jpa advance dialog.
     *
     * @param project the project
     */
    public JpaAdvanceDialog(@Nullable Project project) {
        super(project);
        super.init();
        setTitle("Generate Options");
        setSize(600, 400);
    }


    @Override
    protected @Nullable JComponent createCenterPanel() {
        return smartJpaAdvanceUI.getRootPanel();
    }

    /**
     * Gets selected fields.
     *
     * @return the selected fields
     */
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

    /**
     * Gets all fields str.
     *
     * @return the all fields str
     */
    public String getAllFieldsStr() {
        return smartJpaAdvanceUI.getAllFieldsText();
    }

    private static final Logger logger = LoggerFactory.getLogger(JpaAdvanceDialog.class);

    /**
     * Init fields.
     *
     * @param conditionFields the condition fields
     * @param allFields       the all fields
     * @param entityClass     the entity class
     */
    public void initFields(List<String> conditionFields, List<TxField> allFields, String entityClass) {
        if (conditionFields.size() > 0) {
            JPanel conditionPanel = smartJpaAdvanceUI.getConditionPanel();
            // 默认 5 列
            int columns = 5;
            int biggerColumn = (int) Math.sqrt(conditionFields.size());
            // 如果 列的总数大于25个, 则取 开根号之后的整形值
            columns = Math.max(biggerColumn, columns);
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
        }

        smartJpaAdvanceUI.initResultFields(allFields);
        smartJpaAdvanceUI.setResultType(entityClass);

    }

    /**
     * Gets result map.
     *
     * @return the result map
     */
    public String getResultMap() {
        return smartJpaAdvanceUI.getResultMap();
    }


    /**
     * Gets result type class.
     *
     * @return the result type class
     */
    public String getResultTypeClass() {
        return smartJpaAdvanceUI.getResultTypeClass();
    }

    /**
     * Is result type boolean.
     *
     * @return the boolean
     */
    public boolean isResultType() {
        return smartJpaAdvanceUI.isResultType();
    }

    /**
     * Gets generator type.
     *
     * @return the generator type
     */
    public SmartJpaAdvanceUI.GeneratorEnum getGeneratorType() {
        return smartJpaAdvanceUI.getGeneratorType();
    }


}
