package com.baomidou.plugin.idea.mybatisx.dom.model;

import com.baomidou.plugin.idea.mybatisx.dom.converter.AliasConverter;
import com.baomidou.plugin.idea.mybatisx.dom.converter.ParameterMapConverter;
import com.intellij.psi.PsiClass;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The interface Group two.
 *
 * @author yanglin
 */
public interface GroupTwo extends GroupOne {

    /**
     * Gets binds.
     *
     * @return the binds
     */
    @SubTagList("bind")
    List<Bind> getBinds();

    /**
     * Gets parameter map.
     *
     * @return the parameter map
     */
    @NotNull
    @Attribute("parameterMap")
    @Convert(ParameterMapConverter.class)
    GenericAttributeValue<XmlTag> getParameterMap();

    /**
     * Gets parameter type.
     *
     * @return the parameter type
     */
    @NotNull
    @Attribute("parameterType")
    @Convert(AliasConverter.class)
    GenericAttributeValue<PsiClass> getParameterType();
}
