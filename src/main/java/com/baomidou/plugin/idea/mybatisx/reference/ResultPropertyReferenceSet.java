package com.baomidou.plugin.idea.mybatisx.reference;

import com.intellij.openapi.util.TextRange;
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
public class ResultPropertyReferenceSet extends ReferenceSetBase<PsiReference> {

    /**
     * Instantiates a new Result property reference set.
     *
     * @param text    the text
     * @param element the element
     * @param offset  the offset
     */
    public ResultPropertyReferenceSet(String text, @NotNull PsiElement element, int offset) {
        super(text, element, offset, DOT_SEPARATOR);
    }

    @Nullable
    @NonNls
    @Override
    protected PsiReference createReference(TextRange range, int index) {
        XmlAttributeValue element = (XmlAttributeValue) getElement();
        return null == element ? null : new ContextPsiFieldReference(element, range, index);
    }

}
