package com.baomidou.plugin.idea.mybatisx.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

/**
 * The interface Bean property.
 *
 * @author yanglin
 */
public interface BeanProperty extends DomElement {

    /**
     * Gets name.
     *
     * @return the name
     */
    @NotNull
    @Attribute("name")
    GenericAttributeValue<String> getName();

    /**
     * Gets value.
     *
     * @return the value
     */
    @NotNull
    @Attribute("value")
    GenericAttributeValue<String> getValue();
}
