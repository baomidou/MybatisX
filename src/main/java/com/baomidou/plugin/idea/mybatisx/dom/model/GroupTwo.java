package com.baomidou.plugin.idea.mybatisx.dom.model;

import com.intellij.psi.PsiClass;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;
import com.intellij.util.xml.SubTagList;
import com.baomidou.plugin.idea.mybatisx.dom.converter.AliasConverter;
import com.baomidou.plugin.idea.mybatisx.dom.converter.DaoMethodConverter;
import com.baomidou.plugin.idea.mybatisx.dom.converter.ParameterMapConverter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author yanglin
 */
public interface GroupTwo extends GroupOne, IdDomElement {

    @SubTagList("bind")
    public List<Bind> getBinds();

    @NotNull
    @Attribute("parameterMap")
    @Convert(ParameterMapConverter.class)
    public GenericAttributeValue<XmlTag> getParameterMap();

    @Attribute("id")
    @Convert(DaoMethodConverter.class)
    public GenericAttributeValue<String> getId();

    @NotNull
    @Attribute("parameterType")
    @Convert(AliasConverter.class)
    public GenericAttributeValue<PsiClass> getParameterType();
}
