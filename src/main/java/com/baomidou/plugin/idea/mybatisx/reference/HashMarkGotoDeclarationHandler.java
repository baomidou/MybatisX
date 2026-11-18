package com.baomidou.plugin.idea.mybatisx.reference;

import com.baomidou.plugin.idea.mybatisx.tip.hashmark.CompositeHashMarkTip;
import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

/**
 * Enables Ctrl+Click / Ctrl+B navigation from {@code #{param}} placeholders inside SQL
 * (including MyBatis Mapper annotations and nested {@code <script>} SQL) to the corresponding
 * Mapper method parameter or Java field.
 */
public class HashMarkGotoDeclarationHandler implements GotoDeclarationHandler {

    @Override
    public PsiElement @Nullable [] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {
        if (sourceElement == null) {
            return null;
        }
        PsiElement hashMarkElement = findHashMarkElement(sourceElement);
        if (hashMarkElement == null) {
            return null;
        }
        PsiElement target = new CompositeHashMarkTip(sourceElement.getProject()).findReference(hashMarkElement);
        if (target == null) {
            return null;
        }
        return new PsiElement[]{target};
    }

    private @Nullable PsiElement findHashMarkElement(@Nullable PsiElement sourceElement) {
        PsiElement current = sourceElement;
        for (int i = 0; i < 3 && current != null; i++) {
            String text = current.getText();
            if (text != null && text.startsWith("#{") && text.contains("}") && text.indexOf('#', 2) == -1) {
                return current;
            }
            current = current.getParent();
        }
        return null;
    }
}
