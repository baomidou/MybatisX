package com.baomidou.plugin.idea.mybatisx.smartjpa.ui;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmartJpaCompletionInsertHandler implements InsertHandler<LookupElement> {
    private Editor editor;
    private Project project;
    private CompletionParameters completionParameters;

    public SmartJpaCompletionInsertHandler(Editor editor,
                                           Project project,
                                           CompletionParameters completionParameters) {

        this.editor = editor;
        this.project = project;
        this.completionParameters = completionParameters;
    }

    @Override
    public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item) {
        logger.info("InsertHandler.handleInsert, context: {}, complete start", context);
        CompletionService.getCompletionService().performCompletion(completionParameters, new Consumer<CompletionResult>() {
            @Override
            public void consume(CompletionResult completionResult) {
                LookupElement lookupElement = completionResult.getLookupElement();
                logger.info("InsertHandler.handleInsert, lookupElement: {}", lookupElement);
//                CompletionAutoPopupHandler.runLaterWithCommitted(project, editor.getDocument(), new Runnable() {
//                    @Override
//                    public void run() {
//                        boolean committed = PsiDocumentManager.getInstance(project).isCommitted(editor.getDocument());
//                        logger.info("document committed: {}", committed);
//                        if (committed) {
//                            new CodeCompletionHandlerBase(CompletionType.BASIC).invokeCompletion(project, editor, 1);
//                        }
//                    }
//                });

            }
        });


    }

    private static final Logger logger = LoggerFactory.getLogger(SmartJpaCompletionInsertHandler.class);
}
