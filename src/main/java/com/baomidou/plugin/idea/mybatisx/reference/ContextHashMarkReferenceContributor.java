package com.baomidou.plugin.idea.mybatisx.reference;

import com.baomidou.plugin.idea.mybatisx.tip.hashmark.CompositeHashMarkTip;
import com.baomidou.plugin.idea.mybatisx.util.OgnlUtils;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.xml.XmlToken;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * 参考 com.intellij.spring.el.contextProviders.extensions.SpringElCommentReferenceContributor
 *
 * @author ls9527
 */
public class ContextHashMarkReferenceContributor extends PsiReferenceContributor {

    private static final String SIMPLE_PREFIX_STR = "#{";
    public static final PsiElementPattern.Capture<PsiElement> EL_VAR_COMMENT = PlatformPatterns.psiElement(PsiElement.class)
        .withText(StandardPatterns.string().startsWith(SIMPLE_PREFIX_STR).contains("}"));


    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(EL_VAR_COMMENT, new PsiReferenceProvider() {
            @Override
            public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                if (!(element instanceof XmlToken) && !isSqlHashMarkElement(element)) {
                    return PsiReference.EMPTY_ARRAY;
                }
                String value = element.getText();
                if (value == null || !value.startsWith(SIMPLE_PREFIX_STR)) {
                    return PsiReference.EMPTY_ARRAY;
                }
                int braceIndex = value.indexOf('}');
                int prefixLength = SIMPLE_PREFIX_STR.length();
                if (braceIndex <= prefixLength) {
                    return PsiReference.EMPTY_ARRAY;
                }
                int commaIndex = value.indexOf(',');
                int endIndex = (commaIndex > prefixLength && commaIndex < braceIndex) ? commaIndex : braceIndex;
                TextRange property = new TextRange(prefixLength, endIndex);
                return new PsiReference[]{new HashMarkReference(element, property)};
            }
        });
    }

    private boolean isSqlHashMarkElement(@NotNull PsiElement element) {
        String text = element.getText();
        if (text.indexOf('#', SIMPLE_PREFIX_STR.length()) != -1) {
            return false;
        }
        PsiElement parent = element.getParent();
        if (parent != null && text.equals(parent.getText()) && element instanceof LeafPsiElement) {
            return false;
        }
        return !OgnlUtils.findCandidatePsiMethods(element).isEmpty();
    }

    private static class HashMarkReference extends PsiReferenceBase<PsiElement> {

        private final CompositeHashMarkTip compositeHashMarkTip;

        public HashMarkReference(@NotNull PsiElement element, TextRange rangeInElement) {
            super(element, rangeInElement);
            compositeHashMarkTip = new CompositeHashMarkTip(element.getProject());
        }

        @Override
        public @Nullable PsiElement resolve() {
            return compositeHashMarkTip.findReference(this.myElement);
        }
    }

}
