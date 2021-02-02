package com.baomidou.plugin.idea.mybatisx.action;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
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
        /**
         * 代码生成
         */
        Project project = e.getProject();
        ClassGenerateDialogWrapper classGenerateDialogWrapper = new ClassGenerateDialogWrapper(project);
        PsiElement[] tableElements = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        // 填充默认的选项
        classGenerateDialogWrapper.fillData(project,tableElements);
        classGenerateDialogWrapper.show();
        // 模态窗口选择 OK, 生成相关代码
        if (classGenerateDialogWrapper.getExitCode() == Messages.YES) {
            // 生成代码
            classGenerateDialogWrapper.generateCode(project,tableElements);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Boolean visible = null;
        PsiElement[] psiElements = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (psiElements == null || psiElements.length == 0) {
            visible = false;
        }
        Class<?> dbTableClass = null;
        if (visible == null) {
            try {
                dbTableClass = Class.forName("com.intellij.database.psi.DbTable");
            } catch (ClassNotFoundException ex) {
                visible = false;
            }
        }
        if (visible == null) {
            Class<?> checkClass = dbTableClass;
            if (!Stream.of(psiElements).allMatch(item -> checkClass.isAssignableFrom(item.getClass()))) {
                visible = false;
            }
        }
        // 未安装Database Tools插件时，不展示菜单
        if (visible != null) {
            e.getPresentation().setEnabledAndVisible(visible);
        }

    }

}
