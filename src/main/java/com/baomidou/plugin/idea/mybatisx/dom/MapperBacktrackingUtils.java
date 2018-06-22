package com.baomidou.plugin.idea.mybatisx.dom;

import com.google.common.base.Optional;

import com.intellij.psi.PsiClass;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import com.baomidou.plugin.idea.mybatisx.dom.model.Association;
import com.baomidou.plugin.idea.mybatisx.dom.model.Collection;
import com.baomidou.plugin.idea.mybatisx.dom.model.ParameterMap;
import com.baomidou.plugin.idea.mybatisx.dom.model.ResultMap;

import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 */
public final class MapperBacktrackingUtils {

    private MapperBacktrackingUtils() {
        throw new UnsupportedOperationException();
    }

    public static Optional<PsiClass> getPropertyClazz(XmlAttributeValue attributeValue) {
        DomElement domElement = DomUtil.getDomElement(attributeValue);
        if (null == domElement) {
            return Optional.absent();
        }

        Collection collection = DomUtil.getParentOfType(domElement, Collection.class, true);
        if (null != collection && !isWithinSameTag(collection, attributeValue)) {
            return Optional.fromNullable(collection.getOfType().getValue());
        }

        Association association = DomUtil.getParentOfType(domElement, Association.class, true);
        if (null != association && !isWithinSameTag(association, attributeValue)) {
            return Optional.fromNullable(association.getJavaType().getValue());
        }

        ParameterMap parameterMap = DomUtil.getParentOfType(domElement, ParameterMap.class, true);
        if (null != parameterMap && !isWithinSameTag(parameterMap, attributeValue)) {
            return Optional.fromNullable(parameterMap.getType().getValue());
        }

        ResultMap resultMap = DomUtil.getParentOfType(domElement, ResultMap.class, true);
        if (null != resultMap && !isWithinSameTag(resultMap, attributeValue)) {
            return Optional.fromNullable(resultMap.getType().getValue());
        }
        return Optional.absent();
    }

    public static boolean isWithinSameTag(@NotNull DomElement domElement, @NotNull XmlElement xmlElement) {
        XmlTag xmlTag = PsiTreeUtil.getParentOfType(xmlElement, XmlTag.class);
        return null != xmlElement && domElement.getXmlTag().equals(xmlTag);
    }

}
