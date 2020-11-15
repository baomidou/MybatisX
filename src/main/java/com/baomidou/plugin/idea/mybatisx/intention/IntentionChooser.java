package com.baomidou.plugin.idea.mybatisx.intention;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * The interface Intention chooser.
 *
 * @author yanglin
 */
public interface IntentionChooser {

    /**
     * Is available boolean.
     *
     * @param project the project
     * @param editor  the editor
     * @param file    the file
     * @return the boolean
     */
    boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file);

}
