package com.baomidou.plugin.idea.mybatisx.dom.model;

import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.baomidou.plugin.idea.mybatisx.dom.converter.ResultMapConverter;

import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 */
public interface ResultMapGroup extends DomElement {

    @NotNull
    @Attribute("resultMap")
    @Convert(ResultMapConverter.class)
    GenericAttributeValue<XmlTag> getResultMap();
}
