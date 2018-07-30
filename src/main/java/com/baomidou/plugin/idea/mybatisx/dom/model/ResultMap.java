package com.baomidou.plugin.idea.mybatisx.dom.model;

import com.intellij.psi.PsiClass;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.baomidou.plugin.idea.mybatisx.dom.converter.AliasConverter;
import com.baomidou.plugin.idea.mybatisx.dom.converter.ResultMapConverter;

import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 */
public interface ResultMap extends GroupFour, IdDomElement {

    @NotNull
    @Attribute("extends")
    @Convert(ResultMapConverter.class)
    GenericAttributeValue<XmlAttributeValue> getExtends();

    @NotNull
    @Attribute("type")
    @Convert(AliasConverter.class)
    GenericAttributeValue<PsiClass> getType();

}
