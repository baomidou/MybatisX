package com.baomidou.plugin.idea.mybatisx.reference;

import com.intellij.psi.PsiField;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.NotNull;

/**
 * The type Reference set resolver factory.
 *
 * @author yanglin
 */
public final class ReferenceSetResolverFactory {

    private ReferenceSetResolverFactory() {
        throw new UnsupportedOperationException();
    }

    /**
     * Create psi field resolver context reference set resolver.
     *
     * @param <F>    the type parameter
     * @param target the target
     * @return the context reference set resolver
     */
    public static <F extends XmlAttributeValue> ContextReferenceSetResolver<XmlAttributeValue, PsiField> createPsiFieldResolver(@NotNull F target) {
        return new PsiFieldReferenceSetResolver(target);
    }

}
