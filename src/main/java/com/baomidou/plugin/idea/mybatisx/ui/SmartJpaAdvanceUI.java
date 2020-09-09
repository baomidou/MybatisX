package com.baomidou.plugin.idea.mybatisx.ui;

import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;

public class SmartJpaAdvanceUI {
    public JPanel conditionPanel;
    private JPanel rootPanel;
    private JCheckBox checkAllConditionField;

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
}
