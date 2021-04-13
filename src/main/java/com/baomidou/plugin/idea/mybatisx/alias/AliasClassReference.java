package com.baomidou.plugin.idea.mybatisx.alias;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The type Alias class reference.
 *
 * @author yanglin
 */
public class AliasClassReference extends PsiReferenceBase<XmlAttributeValue> {

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
        return aliasFacade.getAliasDescs(getElement()).stream().map(AliasDesc::getAlias).toArray(String[]::new);
    }

}
