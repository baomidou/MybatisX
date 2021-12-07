package com.baomidou.plugin.idea.mybatisx.dom.converter;

import com.baomidou.plugin.idea.mybatisx.reference.ResultPropertyReferenceSet;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.GenericDomValue;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * The type Property converter.
 *
 * @author yanglin
 */
public class PropertyConverter extends ConverterAdaptor<XmlAttributeValue> implements CustomReferenceConverter<XmlAttributeValue> {

    @Override
    public String getErrorMessage(@Nullable String property, ConvertContext context) {
        return "property :" + property + " can not be found ";
    }

    @NotNull
    @Override
    public PsiReference[] createReferences(GenericDomValue<XmlAttributeValue> value, PsiElement element, ConvertContext context) {
        final String fieldName = value.getStringValue();
        if (fieldName == null) {
            return PsiReference.EMPTY_ARRAY;
        }
        return new ResultPropertyReferenceSet(fieldName, element, ElementManipulators.getOffsetInElement(element)).getPsiReferences();
    }

    @Nullable
    @Override
    public XmlAttributeValue fromString(@Nullable @NonNls String firstText, ConvertContext context) {
        DomElement ctxElement = context.getInvocationElement();
        if (ctxElement instanceof GenericAttributeValue) {
            final XmlAttributeValue xmlAttributeValue = ((GenericAttributeValue) ctxElement).getXmlAttributeValue();
            PropertySetterFind propertySetterFind = new PropertySetterFind();
            final Optional<PsiField> startElement = propertySetterFind.getStartElement(firstText, xmlAttributeValue);
            if (startElement.isPresent()) {
                return xmlAttributeValue;
            }
        }
        return null;
    }

}
