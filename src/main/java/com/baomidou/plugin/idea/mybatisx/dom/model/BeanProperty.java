package com.baomidou.plugin.idea.mybatisx.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;

import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 */
public interface BeanProperty extends DomElement {

    @NotNull
    @Attribute("name")
    GenericAttributeValue<String> getName();

    @NotNull
    @Attribute("value")
    GenericAttributeValue<String> getValue();
}
