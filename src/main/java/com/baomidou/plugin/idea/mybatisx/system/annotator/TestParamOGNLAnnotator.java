package com.baomidou.plugin.idea.mybatisx.system.annotator;

import com.baomidou.plugin.idea.mybatisx.reference.TestParamReferenceContributor;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TestParamOGNLAnnotator implements Annotator {

    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
        "and", "or", "eq", "neq", "lt", "gt", "lte", "gte", "not", "null", "true", "false", "new", "instanceof"
    ));

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!TestParamReferenceContributor.TEST_ATTRIBUTE_VALUE.accepts(element)) {
            return;
        }
        if (!(element instanceof XmlAttributeValue)) {
            return;
        }

        String text = element.getText();
        if (text == null || text.isEmpty()) {
            return;
        }

        int offset = 0;
        if (text.startsWith("\"") || text.startsWith("'")) {
            offset = 1;
        }

        StringBuilder sb = new StringBuilder();
        int startCoords = -1;

        for (int i = offset; i < text.length(); i++) {
            char c = text.charAt(i);
            if ((c == '"' || c == '\'') && i == text.length() - 1) {
                break;
            }

            if (Character.isJavaIdentifierPart(c)) {
                if (startCoords == -1) {
                    startCoords = i;
                }
                sb.append(c);
            } else {
                if (startCoords != -1) {
                    highlightIfKeyword(element, holder, sb.toString(), startCoords);
                    sb = new StringBuilder();
                    startCoords = -1;
                }
            }
        }
        if (startCoords != -1) {
            highlightIfKeyword(element, holder, sb.toString(), startCoords);
        }
    }

    private void highlightIfKeyword(PsiElement element, AnnotationHolder holder, String word, int startOffset) {
        if (KEYWORDS.contains(word)) {
            TextRange range = new TextRange(element.getTextRange().getStartOffset() + startOffset,
                element.getTextRange().getStartOffset() + startOffset + word.length());
            holder.newAnnotation(HighlightSeverity.INFORMATION, "")
                .range(range)
                .textAttributes(DefaultLanguageHighlighterColors.KEYWORD)
                .create();
        }
    }
}
