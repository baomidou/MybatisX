package com.baomidou.plugin.idea.mybatisx.util;

import com.baomidou.plugin.idea.mybatisx.annotation.Annotation;
import com.baomidou.plugin.idea.mybatisx.dom.model.IdDomElement;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.xml.DomUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.util.TypeConversionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Utility for analyzing OGNL expressions in Mybatis XML.
 */
public class OgnlUtils {

    /**
     * Resolves an OGNL list/map property like "size", "isEmpty", "keys", etc.
     */
    @Nullable
    public static PsiElement resolveOgnlSpecialProperty(@Nullable PsiType type, String name, Project project) {
        if (type == null) return null;

        if ("size".equals(name) || "isEmpty".equals(name)) {
            PsiClass col = JavaPsiFacade.getInstance(project).findClass("java.util.Collection", GlobalSearchScope.allScope(project));
            PsiClass map = JavaPsiFacade.getInstance(project).findClass("java.util.Map", GlobalSearchScope.allScope(project));

            boolean isCol = col != null && (type instanceof PsiArrayType || (type instanceof PsiClassType && ((PsiClassType) type).resolve() != null && ((PsiClassType) type).resolve().isInheritor(col, true)));
            boolean isMap = map != null && type instanceof PsiClassType && ((PsiClassType) type).resolve() != null && ((PsiClassType) type).resolve().isInheritor(map, true);

            if (isCol || isMap) {
                return resolveMethodInType(type, "size".equals(name) ? "size" : "isEmpty", project);
            }
        }
        if ("keys".equals(name) || "values".equals(name) || "entrySet".equals(name)) {
            PsiClass map = JavaPsiFacade.getInstance(project).findClass("java.util.Map", GlobalSearchScope.allScope(project));
            if (map != null && type instanceof PsiClassType && ((PsiClassType) type).resolve() != null && ((PsiClassType) type).resolve().isInheritor(map, true)) {
                return resolveMethodInType(type, name, project);
            }
        }
        return null;
    }

    /**
     * Resolves a path expression starting from a given method context.
     * @param contextElement The XML element context (to find the mapper method).
     * @param expression The expression path (e.g. "user.name").
     * @return The resolved PsiElement (Field or Method), or null if not found.
     */
    @Nullable
    public static PsiElement resolveExpression(@NotNull PsiElement contextElement, @NotNull String expression) {
        String[] parts = expression.split("\\.");
        String rootName = parts[0];
        if (rootName.isEmpty()) return null;

        PsiElement foreachVar = resolveForeachVariable(contextElement, rootName);
        if (foreachVar != null) {
            if (parts.length == 1) return foreachVar;
            PsiType type = getType(foreachVar);
            if (type == null) return null;
            PsiElement current = resolveMemberInType(type, parts[1], contextElement.getProject());
            for (int i = 2; i < parts.length; i++) {
                current = resolveMemberInType(getType(current), parts[i], contextElement.getProject());
                if (current == null) break;
            }
            return current;
        }

        PsiMethod psiMethod = getPsiMethod(contextElement);
        if (psiMethod == null) return null;
        return resolveTarget(psiMethod, expression, contextElement.getProject());
    }

    @Nullable
    private static PsiElement resolveForeachVariable(@NotNull PsiElement context, @NotNull String name) {
        PsiElement current = context;
        while (current != null) {
            if (current instanceof XmlTag) {
                XmlTag tag = (XmlTag) current;
                if ("foreach".equals(tag.getName())) {
                    String item = tag.getAttributeValue("item");
                    if (name.equals(item)) {
                        XmlAttribute itemAttr = tag.getAttribute("item");
                        if (itemAttr != null) return itemAttr.getValueElement();
                    }
                    String index = tag.getAttributeValue("index");
                    if (name.equals(index)) {
                        XmlAttribute indexAttr = tag.getAttribute("index");
                        if (indexAttr != null) return indexAttr.getValueElement();
                    }
                }
            }
            current = current.getParent();
        }
        return null;
    }

    @Nullable
    public static PsiMethod getPsiMethod(@NotNull PsiElement contextElement) {
        IdDomElement domElement = DomUtil.findDomElement(contextElement, IdDomElement.class);
        if (domElement == null) return null;
        Object idValue = domElement.getId().getValue();
        if (!(idValue instanceof PsiMethod)) return null;
        return (PsiMethod) idValue;
    }

    @Nullable
    public static PsiElement resolveTarget(PsiMethod psiMethod, String targetPath, Project project) {
        PsiParameterList parameterList = psiMethod.getParameterList();
        PsiConstantEvaluationHelper constantEvaluationHelper = JavaPsiFacade.getInstance(project).getConstantEvaluationHelper();
        int parametersCount = parameterList.getParametersCount();

        String[] parts = targetPath.split("\\.");
        String rootParamName = parts[0];
        if (rootParamName.isEmpty()) return null;

        for (PsiParameter psiParameter : parameterList.getParameters()) {
            PsiAnnotation annotation = psiParameter.getAnnotation(Annotation.PARAM.getQualifiedName());
            String definedName = null;
            boolean isAnnotated = false;

            if (annotation != null) {
                PsiAnnotationMemberValue paramAnnotationValue = annotation.findAttributeValue("value");
                definedName = (String) constantEvaluationHelper.computeConstantExpression(paramAnnotationValue);
                isAnnotated = true;
            }
            if (definedName == null) definedName = psiParameter.getName();

            boolean match = false;
            // Check for match
            if (isAnnotated && Objects.equals(definedName, rootParamName)) {
                match = true;
            } else if (!isAnnotated) {
                if (Objects.equals(definedName, rootParamName)) {
                    match = true;
                } else if (parametersCount == 1) {
                    // Fallback Single Param
                    PsiElement field = resolveFieldInType(psiParameter.getType(), rootParamName, project);
                    // Check method as well
                    if (field == null) {
                        field = resolveMethodInType(psiParameter.getType(), rootParamName, project);
                    }

                    if (field != null) {
                        PsiElement current = field;
                        for (int i = 1; i < parts.length; i++) {
                            current = resolveMemberInType(getType(current), parts[i], project);
                            if (current == null) break;
                        }
                        if (current != null) return current;
                    }
                }
            }

            if (match) {
                if (parts.length == 1) return psiParameter;
                PsiElement current = resolveMemberInType(psiParameter.getType(), parts[1], project);
                for (int i = 2; i < parts.length; i++) {
                    current = resolveMemberInType(getType(current), parts[i], project);
                    if (current == null) break;
                }
                if (current != null) return current;
            }
        }
        return null;
    }

    @Nullable
    public static PsiType getType(PsiElement element) {
        if (element instanceof PsiVariable) return ((PsiVariable) element).getType();
        if (element instanceof PsiMethod) return ((PsiMethod) element).getReturnType();
        if (element instanceof XmlAttributeValue) {
            return getForeachVariableType((XmlAttributeValue) element);
        }
        return null;
    }

    @Nullable
    private static PsiType getForeachVariableType(@NotNull XmlAttributeValue attributeValue) {
        PsiElement parent = attributeValue.getParent();
        if (!(parent instanceof XmlAttribute)) return null;
        XmlAttribute attribute = (XmlAttribute) parent;
        XmlTag tag = attribute.getParent();
        if (tag == null || !"foreach".equals(tag.getName())) return null;

        String attrName = attribute.getName();
        if ("index".equals(attrName)) {
            return PsiType.INT.getBoxedType(attributeValue.getManager(), attributeValue.getResolveScope());
        }

        if ("item".equals(attrName)) {
            String collectionExpr = tag.getAttributeValue("collection");
            if (collectionExpr != null) {
                PsiElement resolvedCollection = resolveExpression(attributeValue, collectionExpr);
                if (resolvedCollection != null) {
                    PsiType collectionType = getType(resolvedCollection);
                    return extractComponentType(collectionType, attributeValue.getProject());
                }
            }
        }
        return null;
    }

    @Nullable
    private static PsiType extractComponentType(@Nullable PsiType type, @NotNull Project project) {
        if (type instanceof PsiArrayType) {
            return ((PsiArrayType) type).getComponentType();
        }
        if (type instanceof PsiClassType) {
            PsiClassType classType = (PsiClassType) type;
            PsiClassType.ClassResolveResult result = classType.resolveGenerics();
            PsiClass psiClass = result.getElement();
            if (psiClass != null) {
                JavaPsiFacade facade = JavaPsiFacade.getInstance(project);
                GlobalSearchScope scope = GlobalSearchScope.allScope(project);

                // Handle Collection
                PsiClass collectionClass = facade.findClass("java.util.Collection", scope);
                if (collectionClass != null && (psiClass.isEquivalentTo(collectionClass) || psiClass.isInheritor(collectionClass, true))) {
                    PsiSubstitutor substitutor = TypeConversionUtil.getSuperClassSubstitutor(collectionClass, psiClass, result.getSubstitutor());
                    PsiType substituted = substitutor.substitute(collectionClass.getTypeParameters()[0]);
                    if (substituted != null) return substituted;
                }

                // Handle Iterable
                PsiClass iterableClass = facade.findClass("java.lang.Iterable", scope);
                if (iterableClass != null && (psiClass.isEquivalentTo(iterableClass) || psiClass.isInheritor(iterableClass, true))) {
                    PsiSubstitutor substitutor = TypeConversionUtil.getSuperClassSubstitutor(iterableClass, psiClass, result.getSubstitutor());
                    PsiType substituted = substitutor.substitute(iterableClass.getTypeParameters()[0]);
                    if (substituted != null) return substituted;
                }

                // Handle Map (item is value)
                PsiClass mapClass = facade.findClass("java.util.Map", scope);
                if (mapClass != null && (psiClass.isEquivalentTo(mapClass) || psiClass.isInheritor(mapClass, true))) {
                    PsiSubstitutor substitutor = TypeConversionUtil.getSuperClassSubstitutor(mapClass, psiClass, result.getSubstitutor());
                    PsiType substituted = substitutor.substitute(mapClass.getTypeParameters()[1]); // Value
                    if (substituted != null) return substituted;
                }
            }
        }
        return null;
    }

    @Nullable
    public static PsiElement resolveMemberInType(PsiType type, String name, Project project) {
        if (type == null) return null;
        PsiElement member = resolveFieldInType(type, name, project);
        if (member == null) {
            member = resolveMethodInType(type, name, project);
        }
        if (member == null) {
            member = resolveOgnlSpecialProperty(type, name, project);
        }
        return member;
    }

    @Nullable
    public static PsiElement resolveFieldInType(PsiType type, String fieldName, Project project) {
        PsiClass psiClass = resolveClass(type);
        if (psiClass != null) {
            for (PsiField field : psiClass.getAllFields()) {
                if (Objects.equals(field.getName(), fieldName)) {
                    return field;
                }
            }
        }
        return null;
    }

    @Nullable
    public static PsiElement resolveMethodInType(PsiType type, String methodName, Project project) {
        PsiClass psiClass = resolveClass(type);
        if (psiClass != null) {
            PsiMethod[] methods = psiClass.findMethodsByName(methodName, true);
            if (methods.length > 0) return methods[0];
        }
        return null;
    }

    @Nullable
    public static PsiClass resolveClass(PsiType type) {
        if (type instanceof PsiClassType) {
            return ((PsiClassType) type).resolve();
        }
        return null;
    }
}
