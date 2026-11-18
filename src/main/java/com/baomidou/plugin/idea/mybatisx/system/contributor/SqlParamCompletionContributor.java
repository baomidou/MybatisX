package com.baomidou.plugin.idea.mybatisx.system.contributor;

import com.baomidou.plugin.idea.mybatisx.dom.model.IdDomElement;
import com.baomidou.plugin.idea.mybatisx.tip.hashmark.CompositeHashMarkTip;
import com.baomidou.plugin.idea.mybatisx.util.DomUtils;
import com.baomidou.plugin.idea.mybatisx.util.MapperUtils;
import com.baomidou.plugin.idea.mybatisx.util.OgnlUtils;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


/**
 * The type Sql param completion contributor.
 *
 * @author yanglin
 */
public class SqlParamCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(CompletionParameters parameters, final @NotNull CompletionResultSet result) {
        if (parameters.getCompletionType() != CompletionType.BASIC) {
            return;
        }
        PsiElement position = parameters.getOriginalPosition();
        if (position == null) {
            position = parameters.getPosition();
        }
        Editor editor = parameters.getEditor();
        Project project = editor.getProject();
        if (project == null) {
            return;
        }
        PsiFile containingFile = position.getContainingFile();
        int caretOffset = parameters.getOffset();
        int openBraceIdx = findOpenBraceIndex(containingFile.getText(), caretOffset);
        if (openBraceIdx == -1) {
            return;
        }
        String wrappedText = extractWrappedText(containingFile.getText(), openBraceIdx, caretOffset);
        int editorCaret = Math.min(caretOffset - (openBraceIdx + 1), wrappedText.length());

        InjectedLanguageManager injectedLanguageManager = InjectedLanguageManager.getInstance(project);
        PsiFile topLevelFile = injectedLanguageManager.getTopLevelFile(position);
        if (DomUtils.isMybatisFile(topLevelFile)) {
            PsiElement xmlElement = resolveXmlElementForTopLevel(injectedLanguageManager, topLevelFile, position, containingFile);
            Optional<IdDomElement> idDomElement = MapperUtils.findParentIdDomElement(xmlElement);
            idDomElement.ifPresent(domElement -> new CompositeHashMarkTip(project)
                .addElementForPsiParameter(result, domElement, wrappedText, editorCaret, xmlElement));
            result.stopHere();
            return;
        }

        PsiMethod annotationMethod = OgnlUtils.findAnnotationMethod(position);
        if (annotationMethod != null) {
            PsiElement xmlElement = resolveXmlElementForAnnotation(injectedLanguageManager, position, containingFile);
            new CompositeHashMarkTip(project)
                .addElementForPsiParameter(result, annotationMethod, null, null, wrappedText, editorCaret, xmlElement);
            result.stopHere();
        }
    }

    private @Nullable PsiElement resolveXmlElementForTopLevel(@NotNull InjectedLanguageManager manager,
                                                               @NotNull PsiFile topLevelFile,
                                                               @NotNull PsiElement position,
                                                               @NotNull PsiFile containingFile) {
        if (topLevelFile.equals(containingFile)) {
            return position;
        }
        int offset = manager.injectedToHost(position, position.getTextOffset());
        return topLevelFile.findElementAt(offset);
    }

    private @Nullable PsiElement resolveXmlElementForAnnotation(@NotNull InjectedLanguageManager manager,
                                                                @NotNull PsiElement position,
                                                                @NotNull PsiFile containingFile) {
        if (DomUtils.isMybatisFile(containingFile)) {
            return position;
        }
        PsiLanguageInjectionHost directHost = manager.getInjectionHost(position);
        if (directHost != null && DomUtils.isMybatisFile(directHost.getContainingFile())) {
            return directHost;
        }
        return null;
    }

    private int findOpenBraceIndex(@NotNull String text, int offset) {
        int limit = Math.min(offset, text.length());
        for (int i = limit - 1; i > 0; i--) {
            char c = text.charAt(i);
            if (c == '}' || c == '\n' || c == '\r') {
                return -1;
            }
            if (c == '{' && (text.charAt(i - 1) == '#' || text.charAt(i - 1) == '$')) {
                return i;
            }
        }
        return -1;
    }

    private @NotNull String extractWrappedText(@NotNull String text, int openBraceIdx, int caretOffset) {
        int start = openBraceIdx + 1;
        int end = Math.min(caretOffset, text.length());
        // Strip IntelliJ's completion dummy identifier if present before caret.
        String raw = text.substring(start, end);
        int dummyIdx = raw.indexOf("IntellijIdeaRulezzz");
        if (dummyIdx != -1) {
            raw = raw.substring(0, dummyIdx);
        }
        return raw;
    }
}
