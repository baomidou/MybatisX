package com.baomidou.plugin.idea.mybatisx.intention;

import com.baomidou.plugin.idea.mybatisx.service.JavaService;
import com.baomidou.plugin.idea.mybatisx.util.JavaUtils;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import org.jetbrains.annotations.NotNull;

/**
 * The type Java file intention chooser.
 *
 * @author yanglin
 */
public abstract class JavaFileIntentionChooser implements IntentionChooser {

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        if (!(file instanceof PsiJavaFile)) {
            return false;
        }
        PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        return null != element && JavaUtils.isElementWithinInterface(element) && isAvailable(element);
    }

    /**
     * Is available boolean.
     *
     * @param element the element
     * @return the boolean
     */
    public abstract boolean isAvailable(@NotNull PsiElement element);

    /**
     * Is position of parameter declaration boolean.
     *
     * @param element the element
     * @return the boolean
     */
    public boolean isPositionOfParameterDeclaration(@NotNull PsiElement element) {
        return element.getParent() instanceof PsiParameter;
    }

    /**
     * Is position of method declaration boolean.
     *
     * @param element the element
     * @return the boolean
     */
    public boolean isPositionOfMethodDeclaration(@NotNull PsiElement element) {
        return element.getParent() instanceof PsiMethod;
    }

    /**
     * Is position of interface declaration boolean.
     *
     * @param element the element
     * @return the boolean
     */
    public boolean isPositionOfInterfaceDeclaration(@NotNull PsiElement element) {
        return element.getParent() instanceof PsiClass;
    }

    /**
     * Is target present in xml boolean.
     *
     * @param element the element
     * @return the boolean
     */
    public boolean isTargetPresentInXml(@NotNull PsiElement element) {
        return JavaService.getInstance(element.getProject()).findWithFindFirstProcessor(element).isPresent();
    }

}
