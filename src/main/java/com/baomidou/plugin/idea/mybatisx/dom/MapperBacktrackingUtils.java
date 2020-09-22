package com.baomidou.plugin.idea.mybatisx.dom;

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

import java.util.Optional;


/**
 * The type Mapper backtracking utils.
 *
 * @author yanglin
 */
public final class MapperBacktrackingUtils {

    private MapperBacktrackingUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets property clazz.
     *
     * @param attributeValue the attribute value
     * @return the property clazz
     */
    public static Optional<PsiClass> getPropertyClazz(XmlAttributeValue attributeValue) {
        DomElement domElement = DomUtil.getDomElement(attributeValue);
        if (null == domElement) {
            return Optional.empty();
        }

        Collection collection = DomUtil.getParentOfType(domElement, Collection.class, true);
        if (null != collection && !isWithinSameTag(collection, attributeValue)) {
            return Optional.ofNullable(collection.getOfType().getValue());
        }

        Association association = DomUtil.getParentOfType(domElement, Association.class, true);
        if (null != association && !isWithinSameTag(association, attributeValue)) {
            return Optional.ofNullable(association.getJavaType().getValue());
        }

        ParameterMap parameterMap = DomUtil.getParentOfType(domElement, ParameterMap.class, true);
        if (null != parameterMap && !isWithinSameTag(parameterMap, attributeValue)) {
            return Optional.ofNullable(parameterMap.getType().getValue());
        }

        ResultMap resultMap = DomUtil.getParentOfType(domElement, ResultMap.class, true);
        if (null != resultMap && !isWithinSameTag(resultMap, attributeValue)) {
            return Optional.ofNullable(resultMap.getType().getValue());
        }
        return Optional.empty();
    }

    /**
     * Is within same tag boolean.
     *
     * @param domElement the dom element
     * @param xmlElement the xml element
     * @return the boolean
     */
    public static boolean isWithinSameTag(@NotNull DomElement domElement, XmlAttributeValue xmlElement) {
        XmlTag xmlTag = PsiTreeUtil.getParentOfType(xmlElement, XmlTag.class);
        return null != xmlElement && domElement.getXmlTag().equals(xmlTag);
    }

}
