package com.baomidou.plugin.idea.mybatisx.generate.action;

import com.intellij.database.model.DasTable;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * @author ls9527
 * @date 2024/11/23
 */
public class TableSelectionCondition  {
    public boolean value(@NotNull DataContext dataContext) {
        Project project = dataContext.getData(CommonDataKeys.PROJECT);
        if (project == null) {
            return false;
        }

        PsiElement psiElement = dataContext.getData(PlatformDataKeys.PSI_ELEMENT);
        if (psiElement == null) {
            return false;
        }

        if (psiElement instanceof DasTable) {
            return true;
        }

        return false;
    }
}
