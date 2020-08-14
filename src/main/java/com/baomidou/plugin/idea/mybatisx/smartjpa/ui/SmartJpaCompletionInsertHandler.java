package com.baomidou.plugin.idea.mybatisx.smartjpa.ui;

import com.intellij.codeInsight.completion.CodeCompletionHandlerBase;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.editorActions.CompletionAutoPopupHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.application.AppUIExecutor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 选择自动填充时, 再次提示
 */
class SmartJpaCompletionInsertHandler implements InsertHandler<LookupElement> {
    private final Editor editor;
    private final Project project;

    public SmartJpaCompletionInsertHandler(Editor editor, Project project) {
        this.editor = editor;
        this.project = project;
    }

    @Override
    public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item) {
        AppUIExecutor.onUiThread().later().execute(()->{
            boolean committed = PsiDocumentManager.getInstance(project).isCommitted(editor.getDocument());
            logger.info("document committed: {}", committed);
            if (committed) {
                new CodeCompletionHandlerBase(CompletionType.BASIC).invokeCompletion(project, editor, 1);
            }
        });
    }

    private static final Logger logger = LoggerFactory.getLogger(SmartJpaCompletionInsertHandler.class);
}
