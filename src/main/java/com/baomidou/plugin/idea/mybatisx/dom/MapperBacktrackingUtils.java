package com.baomidou.plugin.idea.mybatisx.dom;

import com.baomidou.plugin.idea.mybatisx.dom.model.Association;
import com.baomidou.plugin.idea.mybatisx.dom.model.Collection;
import com.baomidou.plugin.idea.mybatisx.dom.model.ParameterMap;
import com.baomidou.plugin.idea.mybatisx.dom.model.Result;
import com.baomidou.plugin.idea.mybatisx.dom.model.ResultMap;
import com.baomidou.plugin.idea.mybatisx.util.JavaUtils;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.GenericAttributeValue;
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
        if (null != collection) {
            DomElement parentOfType = null;
            Result propertyResult = DomUtil.getParentOfType(domElement, Result.class, true);
            if (propertyResult == null) {
                parentOfType = DomUtil.getParentOfType(collection, DomElement.class, true);
            }
            if (parentOfType == null) {
                parentOfType = collection;
            }
            PsiClass collectionClass = findLeastParentType(parentOfType, attributeValue.getProject());
            return Optional.ofNullable(collectionClass);
        }

        Association association = DomUtil.getParentOfType(domElement, Association.class, true);
        if (null != association && !isWithinSameTag(association, attributeValue)) {
            PsiClass associationClass = findLeastParentType(association, attributeValue.getProject());
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
        while (true) {
            if (currentElement instanceof Association) {
                boolean foundClass = false;
                Association association = (Association) currentElement;
                PsiClass javaType = association.getJavaType().getValue();
                if (javaType != null) {
                    currentClass = javaType;
                    foundClass = true;
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
                                    foundClass = true;
                                }
                            }
                        }
                    }
                }
                if (foundClass) {
                    break;
                }
            } else if (currentElement instanceof Collection) {
                boolean foundClass = false;
                Collection collection = (Collection) currentElement;
                GenericAttributeValue<PsiClass> type = collection.getOfType();
                if (type.getValue() != null) {
                    currentClass = type.getValue();
                    foundClass = true;
                }
                if (currentClass == null) {
                    XmlAttributeValue value = collection.getProperty().getValue();
                    if (value != null) {
                        String propertyName = value.getValue();
                        PsiClass leastParentType = findLeastParentType(currentElement.getParent(), project);
                        if (leastParentType != null) {
                            Optional<PsiField> settablePsiField = JavaUtils.findSettablePsiField(leastParentType, propertyName);
                            if (settablePsiField.isPresent()) {
                                PsiField psiField = settablePsiField.get();
                                Optional<String> genericClassOfListByField = findGenericClassOfListByField(psiField);
                                if (genericClassOfListByField.isPresent()) {
                                    String fieldTypeClassName = genericClassOfListByField.get();
                                    Optional<PsiClass> fieldClassOptional = JavaUtils.findClazz(project, fieldTypeClassName);
                                    if (foundClass = fieldClassOptional.isPresent()) {
                                        currentClass = fieldClassOptional.get();
                                    }
                                }
                            }
                        }
                    }
                }
                if (foundClass) {
                    break;
                }
            } else if (currentElement instanceof ResultMap) {
                ResultMap association = (ResultMap) currentElement;
                GenericAttributeValue<PsiClass> type = association.getType();
                currentClass = type.getValue();
            }

            if (currentElement == null) {
                break;
            }
            currentElement = currentElement.getParent();
        }
        return currentClass;
    }

    private static Optional<String> findGenericClassOfListByField(PsiField psiField) {
        PsiTypeElement typeElement = psiField.getTypeElement();
        if (typeElement != null) {
            PsiJavaCodeReferenceElement innermostComponentReferenceElement = typeElement.getInnermostComponentReferenceElement();
            if (innermostComponentReferenceElement != null) {
                PsiType[] typeParameters = innermostComponentReferenceElement.getTypeParameters();
                if (typeParameters.length == 1) {
                    PsiType typeParameter = typeParameters[0];
                    return Optional.of(typeParameter.getCanonicalText());
                }
            }
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
        return xmlTag!=null && domElement.getXmlTag() != null && domElement.getXmlTag().equals(xmlTag);
    }

    public static Optional<PsiClass> getEntityClass(XmlAttributeValue attributeValue) {
        DomElement domElement = DomUtil.getDomElement(attributeValue);
        if (null == domElement) {
            return Optional.empty();
        }
        ResultMap resultMap = DomUtil.getParentOfType(domElement, ResultMap.class, true);
        if (null != resultMap && !isWithinSameTag(resultMap, attributeValue)) {
            return Optional.ofNullable(resultMap.getType().getValue());
        }
        return Optional.empty();
    }
}
