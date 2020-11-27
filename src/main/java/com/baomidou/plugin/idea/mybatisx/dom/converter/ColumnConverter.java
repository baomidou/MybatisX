package com.baomidou.plugin.idea.mybatisx.dom.converter;

import com.baomidou.plugin.idea.mybatisx.reference.ResultColumnReferenceSet;
import com.baomidou.plugin.idea.mybatisx.util.JavaUtils;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
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
 * The type Column converter.
 *
 * @author ls9527
 */
public class ColumnConverter extends ConverterAdaptor<XmlAttributeValue> implements CustomReferenceConverter<XmlAttributeValue> {

    public static final String NAMESPACE = "namespace";

    @NotNull
    @Override
    public PsiReference[] createReferences(GenericDomValue<XmlAttributeValue> value, PsiElement element, ConvertContext context) {
        String stringValue = value.getStringValue();
        if (stringValue == null) {
            return PsiReference.EMPTY_ARRAY;
        }
        int offsetInElement = ElementManipulators.getOffsetInElement(element);

        Optional<PsiClass> mapperClassOptional = findMapperClass(context);
        PsiClass mapperClass = mapperClassOptional.orElse(null);
        return new ResultColumnReferenceSet(stringValue, element, offsetInElement, mapperClass).getPsiReferences();
    }

    private Optional<PsiClass> findMapperClass(ConvertContext context) {
        XmlTag rootTag = context.getFile().getRootTag();
        if (rootTag != null) {
            XmlAttribute namespace = rootTag.getAttribute(NAMESPACE);
            if (namespace != null) {
                String value = namespace.getValue();
                if (!StringUtils.isEmpty(value)) {
                    return JavaUtils.findClazz(context.getProject(), value);
                }
            }
        }
        return Optional.empty();
    }

    @Nullable
    @Override
    public XmlAttributeValue fromString(@Nullable @NonNls String s, ConvertContext context) {
        DomElement ctxElement = context.getInvocationElement();
        return ctxElement instanceof GenericAttributeValue ? ((GenericAttributeValue) ctxElement).getXmlAttributeValue() : null;
    }

}
