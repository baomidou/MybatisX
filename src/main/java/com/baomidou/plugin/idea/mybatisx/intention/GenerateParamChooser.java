package com.baomidou.plugin.idea.mybatisx.intention;

import com.baomidou.plugin.idea.mybatisx.annotation.Annotation;
import com.baomidou.plugin.idea.mybatisx.util.JavaUtils;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

/**
 * The type Generate param chooser.
 *
 * @author yanglin
 */
public class GenerateParamChooser extends JavaFileIntentionChooser {

    /**
     * The constant INSTANCE.
     */
    public static final JavaFileIntentionChooser INSTANCE = new GenerateParamChooser();

    @Override
    public boolean isAvailable(@NotNull PsiElement element) {
        PsiParameter parameter = PsiTreeUtil.getParentOfType(element, PsiParameter.class);
        PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        return (null != parameter && !JavaUtils.isAnnotationPresent(parameter, Annotation.PARAM)) ||
            (null != method && !JavaUtils.isAllParameterWithAnnotation(method, Annotation.PARAM));
    }
}
