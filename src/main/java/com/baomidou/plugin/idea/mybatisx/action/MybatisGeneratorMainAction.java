package com.baomidou.plugin.idea.mybatisx.action;


import com.baomidou.plugin.idea.mybatisx.ui.MybatisGeneratorMainUI;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;

/**
 * The type Mybatis generator main action.
 */
public class MybatisGeneratorMainAction extends AnAction {


    /**
     * 点击后打开插件主页面
     *
     * @param e
     */
    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiElement[] psiElements = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (psiElements == null || psiElements.length == 0) {
            Messages.showMessageDialog("Please select one or more tables", "Notice", Messages.getInformationIcon());
            return;
        }
        Class<?> dbTableClass = null;
        try {
            dbTableClass = Class.forName("com.intellij.database.psi.DbTable");
        } catch (ClassNotFoundException ex) {
            return;
        }
        for (PsiElement psiElement : psiElements) {
            if (dbTableClass.isAssignableFrom(psiElement.getClass())) {
                continue;
            }
            Messages.showMessageDialog("Please select one or more tables", "Notice", Messages.getInformationIcon());
            return;
        }
        new MybatisGeneratorMainUI(e);
    }

}
