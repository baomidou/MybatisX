package com.baomidou.plugin.idea.mybatisx.intention;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.baomidou.plugin.idea.mybatisx.annotation.Annotation;
import com.baomidou.plugin.idea.mybatisx.util.JavaUtils;

import org.jetbrains.annotations.NotNull;

/**
 * The type Generate statement chooser.
 *
 * @author yanglin
 */
public class GenerateStatementChooser extends JavaFileIntentionChooser {

    /**
     * The constant INSTANCE.
     */
    public static final JavaFileIntentionChooser INSTANCE = new GenerateStatementChooser();

    @Override
    public boolean isAvailable(@NotNull PsiElement element) {
        if (!isPositionOfMethodDeclaration(element)) {
            return false;
        }
        PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        PsiClass clazz = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        return null != method && null != clazz &&
                !JavaUtils.isAnyAnnotationPresent(method, Annotation.STATEMENT_SYMMETRIES) &&
                !isTargetPresentInXml(method) &&
                isTargetPresentInXml(clazz);
    }
}
