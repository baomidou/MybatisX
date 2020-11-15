package com.baomidou.plugin.idea.mybatisx.dom.model;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

/**
 * The interface Type alias.
 *
 * @author yanglin
 */
public interface TypeAlias extends DomElement {

    /**
     * Gets type.
     *
     * @return the type
     */
    @NotNull
    @Attribute("type")
    GenericAttributeValue<PsiClass> getType();

    /**
     * Gets alias.
     *
     * @return the alias
     */
    @NotNull
    @Attribute("alias")
    GenericAttributeValue<String> getAlias();

}
