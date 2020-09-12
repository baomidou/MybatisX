package com.baomidou.plugin.idea.mybatisx.smartjpa.ui;

import com.intellij.codeInsight.completion.CodeCompletionHandlerBase;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.application.AppUIExecutor;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 选择自动填充时, 再次提示
 */
public class SmartJpaCompletionInsertHandler implements InsertHandler<LookupElement> {
    private final Editor editor;
    private final Project project;

    public SmartJpaCompletionInsertHandler(Editor editor, Project project) {
        this.editor = editor;
        this.project = project;
    }

    @Override
    public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item) {
        context.setLaterRunnable(() -> {
            CodeCompletionHandlerBase handler = CodeCompletionHandlerBase.createHandler(CompletionType.BASIC);
            handler.invokeCompletion(project,editor,1,true);
        });
    }

    private static final Logger logger = LoggerFactory.getLogger(SmartJpaCompletionInsertHandler.class);
}
