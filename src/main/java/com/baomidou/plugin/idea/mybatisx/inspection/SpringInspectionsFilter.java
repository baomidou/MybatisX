package com.baomidou.plugin.idea.mybatisx.inspection;

import com.intellij.codeInspection.InspectionSuppressor;
import com.intellij.codeInspection.SuppressQuickFix;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * spring自带代码检测过滤
 *
 * @author makejava
 * @version 1.0.0
 * @since 2018/08/05 22:00
 */
public class SpringInspectionsFilter implements InspectionSuppressor {

    @Override
    public boolean isSuppressedFor(@NotNull PsiElement element, @NotNull String toolId) {
        return "SpringJavaAutowiredFieldsWarningInspection".equals(toolId);
    }

    @NotNull
    @Override
    public SuppressQuickFix[] getSuppressActions(@Nullable PsiElement element, @NotNull String toolId) {
        return new SuppressQuickFix[0];
    }
}
