package com.baomidou.plugin.idea.mybatisx.service;

import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.formatting.FormatTextRanges;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.psi.impl.source.codeStyle.CodeFormatterFacade;
import org.jetbrains.annotations.NotNull;

/**
 * The type Editor service.
 *
 * @author yanglin
 */
public class EditorService {

    private Project project;

    private FileEditorManager fileEditorManager;

    private CodeFormatterFacade codeFormatterFacade;

    /**
     * Instantiates a new Editor service.
     *
     * @param project the project
     */
    public EditorService(Project project) {
        this.project = project;
        this.fileEditorManager = FileEditorManager.getInstance(project);
    }

    /**
     * Gets instance.
     *
     * @param project the project
     * @return the instance
     */
    public static EditorService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, EditorService.class);
    }

    /**
     * Format.
     *
     * @param file    the file
     * @param element the element
     */
    public void format(@NotNull PsiFile file, @NotNull PsiElement element) {
        CodeStyleSettingsManager instance = CodeStyleSettingsManager.getInstance(element.getProject());
        CodeStyleSettings settings = instance.getMainProjectCodeStyle();
        this.codeFormatterFacade = new CodeFormatterFacade(settings, element.getLanguage());
        codeFormatterFacade.processText(file, new FormatTextRanges(element.getTextRange(), true), true);
    }

    /**
     * Scroll to.
     *
     * @param element the element
     * @param offset  the offset
     */
    public void scrollTo(@NotNull PsiElement element, int offset) {
        NavigationUtil.activateFileWithPsiElement(element, true);
        Editor editor = fileEditorManager.getSelectedTextEditor();
        if (null != editor) {
            editor.getCaretModel().moveToOffset(offset);
            editor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
        }
    }

}
