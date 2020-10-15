package com.baomidou.plugin.idea.mybatisx.intention;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaToken;
import com.intellij.psi.util.PsiTreeUtil;

import jdk.nashorn.internal.parser.TokenType;
import org.jetbrains.annotations.NotNull;

/**
 * The type Generate mapper chooser.
 *
 * @author yanglin
 */
public class GenerateMapperChooser extends JavaFileIntentionChooser {

    /**
     * The constant INSTANCE.
     */
    public static final JavaFileIntentionChooser INSTANCE = new GenerateMapperChooser();

    @Override
    public boolean isAvailable(@NotNull PsiElement element) {
        if (isPositionOfInterfaceDeclaration(element)) {
            // ensure parent element is a PsiClass
            if (element.getParent() instanceof PsiClass) {
                PsiJavaToken nextSiblingOfType = PsiTreeUtil.getNextSiblingOfType(element, PsiJavaToken.class);
                return nextSiblingOfType != null &&
                    nextSiblingOfType.getTokenType().isLeftBound();
            }
        }
        return false;
    }

}
