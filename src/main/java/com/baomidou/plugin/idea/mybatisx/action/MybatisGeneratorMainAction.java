package com.baomidou.plugin.idea.mybatisx.action;


import com.baomidou.plugin.idea.mybatisx.ui.MybatisGeneratorMainUI;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

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
    public void actionPerformed(@NotNull AnActionEvent e) {
        new MybatisGeneratorMainUI(e);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        PsiElement[] psiElements = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (psiElements == null || psiElements.length == 0) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        Class<?> dbTableClass;
        try {
            dbTableClass = Class.forName("com.intellij.database.psi.DbTable");
        } catch (ClassNotFoundException ex) {
            // 未安装Database Tools插件时，不展示菜单
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        if (!Stream.of(psiElements).allMatch(item -> dbTableClass.isAssignableFrom(item.getClass()))) {
            // 选中元素中存在非表元素时，不展示菜单
            e.getPresentation().setEnabledAndVisible(false);
        }
    }

}
