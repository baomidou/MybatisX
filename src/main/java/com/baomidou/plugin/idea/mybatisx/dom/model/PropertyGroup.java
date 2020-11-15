package com.baomidou.plugin.idea.mybatisx.dom.model;

import com.baomidou.plugin.idea.mybatisx.dom.converter.ColumnConverter;
import com.baomidou.plugin.idea.mybatisx.dom.converter.JdbcTypeConverter;
import com.baomidou.plugin.idea.mybatisx.dom.converter.PropertyConverter;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * The interface Property group.
 *
 * @author yanglin
 */
public interface PropertyGroup extends DomElement {

    /**
     * Gets property.
     *
     * @return the property
     */
    @Attribute("property")
    @Convert(PropertyConverter.class)
    GenericAttributeValue<XmlAttributeValue> getProperty();

    @Attribute("column")
    @Convert(ColumnConverter.class)
    GenericAttributeValue<XmlAttributeValue> getColumn();

    @Attribute("jdbcType")
    @Convert(JdbcTypeConverter.class)
    GenericAttributeValue<XmlAttributeValue> getJdbcType();
}
