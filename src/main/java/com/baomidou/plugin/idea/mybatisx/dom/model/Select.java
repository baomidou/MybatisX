package com.baomidou.plugin.idea.mybatisx.dom.model;

import com.baomidou.plugin.idea.mybatisx.dom.converter.AliasConverter;
import com.intellij.psi.PsiClass;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

/**
 * The interface Select.
 *
 * @author yanglin
 */
public interface Select extends GroupTwo, ResultMapGroup, IdDomElement  {

    /**
     * Gets result type.
     *
     * @return the result type
     */
    @NotNull
    @Attribute("resultType")
    @Convert(AliasConverter.class)
    GenericAttributeValue<PsiClass> getResultType();

}
