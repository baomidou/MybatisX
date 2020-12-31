package com.baomidou.plugin.idea.mybatisx.util;

import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * 输入框提示
 * Created by kangtian on 2018/8/3.
 */
public class JTextFieldHintListener implements FocusListener {
    private final String hintText;
    private final JTextField textField;

    /**
     * Instantiates a new J text field hint listener.
     *
     * @param jTextField the j text field
     * @param hintText   the hint text
     */
    public JTextFieldHintListener(JTextField jTextField, String hintText) {
        this.textField = jTextField;
        this.hintText = hintText;
        jTextField.setText(hintText);  //默认直接显示
        jTextField.setForeground(JBColor.GRAY);
    }

    @Override
    public void focusGained(FocusEvent e) {
        //获取焦点时，清空提示内容
        String temp = textField.getText();
        if (temp.equals(hintText)) {
            textField.setText("");
            textField.setForeground(JBColor.BLACK);
        }

    }

    @Override
    public void focusLost(FocusEvent e) {
        //失去焦点时，没有输入内容，显示提示内容
        String temp = textField.getText();
        if ("".equals(temp)) {
            textField.setForeground(JBColor.GRAY);
            textField.setText(hintText);
        }

    }

}
