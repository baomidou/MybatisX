package com.baomidou.plugin.idea.mybatisx.alias;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * The type Alias class reference.
 *
 * @author yanglin
 */
public class AliasClassReference extends PsiReferenceBase<XmlAttributeValue> {

    private Function<AliasDesc, String> function = new Function<AliasDesc, String>() {
        @Override
        public String apply(AliasDesc input) {
            return input.getAlias();
        }
    };

    /**
     * Instantiates a new Alias class reference.
     *
     * @param element the element
     */
    public AliasClassReference(@NotNull XmlAttributeValue element) {
        super(element, true);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        XmlAttributeValue attributeValue = getElement();
        AliasFacade instance = AliasFacade.getInstance(attributeValue.getProject());
        return instance.findPsiClass(attributeValue, attributeValue.getValue()).orElse(null);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        AliasFacade aliasFacade = AliasFacade.getInstance(getElement().getProject());
        Collection<String> result = Collections2.transform(aliasFacade.getAliasDescs(getElement()), function);
        return result.toArray(new String[result.size()]);
    }

}
