package com.baomidou.plugin.idea.mybatisx.dom.model;

import com.baomidou.plugin.idea.mybatisx.dom.converter.PropertyConverter;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * @author yanglin
 */
public interface Id extends PropertyGroup {

    @Attribute("property")
    @Convert(PropertyConverter.class)
    GenericAttributeValue<XmlAttributeValue> getProperty();

    @Attribute("column")
    @Convert(PropertyConverter.class)
    GenericAttributeValue<XmlAttributeValue> getColumn();

    @Attribute("jdbcType")
    @Convert(PropertyConverter.class)
    GenericAttributeValue<XmlAttributeValue> getJdbcType();

}
