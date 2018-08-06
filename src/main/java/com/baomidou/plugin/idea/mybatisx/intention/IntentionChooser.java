package com.baomidou.plugin.idea.mybatisx.intention;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

/**
 * @author yanglin
 */
public interface IntentionChooser {

    boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file);

}
