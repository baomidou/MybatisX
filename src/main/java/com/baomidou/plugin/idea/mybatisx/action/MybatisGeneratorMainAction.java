package com.baomidou.plugin.idea.mybatisx.action;


import com.baomidou.plugin.idea.mybatisx.model.Config;
import com.baomidou.plugin.idea.mybatisx.model.TableInfo;
import com.baomidou.plugin.idea.mybatisx.ui.CodeGenerateUI;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.google.common.base.Joiner;
import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        /**
         * 代码生成
         */
        Project project = e.getProject();
        ClassGenerateDialogWrapper classGenerateDialogWrapper = new ClassGenerateDialogWrapper(project);
        classGenerateDialogWrapper.show();
        // 模态窗口选择 OK, 生成相关代码
        if (classGenerateDialogWrapper.getExitCode() == Messages.YES) {
            PsiElement[] tableElements = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
            classGenerateDialogWrapper.generateCode(tableElements);
        }
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
