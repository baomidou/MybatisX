package com.baomidou.plugin.idea.mybatisx.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.ReferenceSetBase;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The type Result property reference set.
 *
 * @author yanglin
 */
public class ResultColumnReferenceSet extends ReferenceSetBase<PsiReference> {

    private PsiClass mapperClass;

    /**
     * Instantiates a new Result property reference set.
     *
     * @param text        the text
     * @param element     the element
     * @param offset      the offset
     * @param mapperClass
     */
    public ResultColumnReferenceSet(String text, @NotNull PsiElement element, int offset, PsiClass mapperClass) {
        super(text, element, offset, DOT_SEPARATOR);
        this.mapperClass = mapperClass;
    }

    @Nullable
    @NonNls
    @Override
    protected PsiReference createReference(TextRange range, int index) {
        XmlAttributeValue element = (XmlAttributeValue) getElement();
        if (null == element) {
            return null;
        }
        ContextPsiColumnReference contextPsiColumnReference = new ContextPsiColumnReference(element, range, index, mapperClass);
        if (contextPsiColumnReference.resolve() == null) {
            return null;
        }
        return contextPsiColumnReference;
    }


}
