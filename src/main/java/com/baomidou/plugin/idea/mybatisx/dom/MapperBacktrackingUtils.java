package com.baomidou.plugin.idea.mybatisx.dom;

import com.baomidou.plugin.idea.mybatisx.dom.model.Association;
import com.baomidou.plugin.idea.mybatisx.dom.model.Collection;
import com.baomidou.plugin.idea.mybatisx.dom.model.ParameterMap;
import com.baomidou.plugin.idea.mybatisx.dom.model.ResultMap;
import com.baomidou.plugin.idea.mybatisx.util.JavaUtils;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.GenericAttributeValue;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
            PsiClass associationClass = association.getJavaType().getValue();

            if (associationClass == null) {
                String rawText = association.getProperty().getRawText();
                if (StringUtils.isNotBlank(rawText)) {
                    PsiClass leastParentType = findLeastParentType(domElement, attributeValue.getProject());
                    if (leastParentType != null) {
                        associationClass = leastParentType;
                    }
                }
            }
            return Optional.ofNullable(associationClass);
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

    @Nullable
    private static PsiClass findLeastParentType(DomElement domElement, @NotNull Project project) {
        DomElement currentElement = domElement;
        PsiClass currentClass = null;
        do {

            if (currentElement instanceof Association) {
                Association association = (Association) currentElement;
                PsiClass javaType = association.getJavaType().getValue();
                if (javaType != null) {
                    currentClass = javaType;
                }
                if (currentClass == null) {
                    XmlAttributeValue value = association.getProperty().getValue();
                    if (value != null) {
                        String propertyName = value.getValue();
                        PsiClass leastParentType = findLeastParentType(currentElement.getParent(), project);
                        if (leastParentType != null) {
                            Optional<PsiField> settablePsiField = JavaUtils.findSettablePsiField(leastParentType, propertyName);
                            if (settablePsiField.isPresent()) {
                                PsiField psiField = settablePsiField.get();
                                String fieldTypeClassName = psiField.getType().getCanonicalText();
                                Optional<PsiClass> fieldClassOptional = JavaUtils.findClazz(project, fieldTypeClassName);
                                if (fieldClassOptional.isPresent()) {
                                    currentClass = fieldClassOptional.get();
                                }
                            }
                        }
                    }
                }
                if (currentClass == null) {
                    break;
                }
            } else if (currentElement instanceof Collection) {
                Collection collection = (Collection) currentElement;
                GenericAttributeValue<PsiClass> type = collection.getOfType();
                currentClass = type.getValue();
                if (currentClass == null) {
                    XmlAttributeValue value = collection.getProperty().getValue();
                    if (value != null) {
                        String propertyName = value.getValue();
                        PsiClass leastParentType = findLeastParentType(currentElement.getParent(), project);
                        if (leastParentType != null) {
                            Optional<PsiField> settablePsiField = JavaUtils.findSettablePsiField(leastParentType, propertyName);
                            if (settablePsiField.isPresent()) {
                                PsiField psiField = settablePsiField.get();
                                // TODO fixed type java.lang.List<XXDto>
                                String fieldTypeClassName = psiField.getType().getCanonicalText();
                                Optional<PsiClass> fieldClassOptional = JavaUtils.findClazz(project, fieldTypeClassName);
                                if (fieldClassOptional.isPresent()) {
                                    currentClass = fieldClassOptional.get();
                                }
                            }
                        }
                    }
                }
                if (currentClass == null) {
                    break;
                }
            } else if (currentElement instanceof ResultMap) {
                ResultMap association = (ResultMap) currentElement;
                GenericAttributeValue<PsiClass> type = association.getType();
                currentClass = type.getValue();
            }
            currentElement = currentElement.getParent();
        } while (currentClass == null);
        return currentClass;
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
