package com.baomidou.plugin.idea.mybatisx.contributor;

import com.baomidou.plugin.idea.mybatisx.smartjpa.ui.SmartJpaCompletionProvider;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.psi.CustomHighlighterTokenType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * mapper的jpa方法提示
 */
public class MapperMethodCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        super.fillCompletionVariants(parameters, result);
        if (parameters.getCompletionType() != CompletionType.BASIC) {
            return;
        }
        if (inCommentOrLiteral(parameters)) {
            return;
        }
        logger.info("DaoCompletionContributor.fillCompletionVariants start");

        try {

            SmartJpaCompletionProvider daoCompletionProvider = new SmartJpaCompletionProvider();
            daoCompletionProvider.addCompletion(parameters, result);
        } catch (Throwable e) {
            logger.error("自动提示异常", e);
        }

        logger.info("DaoCompletionContributor.fillCompletionVariants end");

    }

    private static boolean inCommentOrLiteral(CompletionParameters parameters) {
        HighlighterIterator iterator = ((EditorEx) parameters.getEditor()).getHighlighter().createIterator(parameters.getOffset());
        if (iterator.atEnd()) return false;

        IElementType elementType = iterator.getTokenType();
        if (elementType == CustomHighlighterTokenType.WHITESPACE) {
            iterator.retreat();
            elementType = iterator.getTokenType();
        }
        return elementType == CustomHighlighterTokenType.LINE_COMMENT ||
            elementType == CustomHighlighterTokenType.MULTI_LINE_COMMENT ||
            elementType == CustomHighlighterTokenType.STRING ||
            elementType == CustomHighlighterTokenType.SINGLE_QUOTED_STRING;
    }

    private static final Logger logger = LoggerFactory.getLogger(MapperMethodCompletionContributor.class);


}
