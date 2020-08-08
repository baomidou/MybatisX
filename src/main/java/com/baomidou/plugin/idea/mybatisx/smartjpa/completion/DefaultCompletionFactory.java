package com.baomidou.plugin.idea.mybatisx.smartjpa.completion;

import com.intellij.codeInsight.completion.CodeCompletionHandlerBase;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultCompletionFactory implements CompletionWordFactory {
    private final Project project;
    private final Editor editor;

    public DefaultCompletionFactory(Project project, Editor editor) {
        this.project = project;
        this.editor = editor;
    }

    private static final Logger logger = LoggerFactory.getLogger(DefaultCompletionFactory.class);
    @Override
    public void complete() {
        logger.info("complete start");
//        FeatureUsageTracker.getInstance().triggerFeatureUsed(CodeCompletionFeatures.EDITING_COMPLETION_BASIC);

        try {
            CodeCompletionHandlerBase codeCompletionHandlerBase = new CodeCompletionHandlerBase(CompletionType.BASIC,
                    true,
                    false,
                    false);
            codeCompletionHandlerBase.invokeCompletion(project, editor);
        } catch (Exception e) {
            logger.error("complete error", e);
        }
        logger.info("complete end");

    }
}
