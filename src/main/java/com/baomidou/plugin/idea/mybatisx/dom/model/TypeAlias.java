package com.baomidou.plugin.idea.mybatisx.dom.model;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;

import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 */
public interface TypeAlias extends DomElement {

    @NotNull
    @Attribute("type")
    public GenericAttributeValue<PsiClass> getType();

    @NotNull
    @Attribute("alias")
    public GenericAttributeValue<String> getAlias();

}
