package com.baomidou.plugin.idea.mybatisx.dom.model;

import com.baomidou.plugin.idea.mybatisx.dom.converter.AliasConverter;
import com.intellij.psi.PsiClass;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

/**
 * The interface Collection.
 *
 * @author yanglin
 */
public interface Collection extends GroupFour, ResultMapGroup, PropertyGroup {

    /**
     * Gets of type.
     *
     * @return the of type
     */
    @NotNull
    @Attribute("ofType")
    @Convert(AliasConverter.class)
    GenericAttributeValue<PsiClass> getOfType();

}
