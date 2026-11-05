package com.baomidou.plugin.idea.mybatisx.util;

import com.baomidou.plugin.idea.mybatisx.annotation.Annotation;
import com.baomidou.plugin.idea.mybatisx.dom.model.IdDomElement;
import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.dom.model.Sql;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiConstantEvaluationHelper;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypes;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.TypeConversionUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Utility for analyzing OGNL expressions in Mybatis XML.
 */
public class OgnlUtils {

    /**
     * Resolves an OGNL list/map property like "size", "isEmpty", "keys", etc.
     */
    @Nullable
    public static PsiElement resolveOgnlSpecialProperty(@Nullable PsiType type, String name, Project project) {
        if (type == null || name == null || name.isEmpty()) {
            return null;
        }

        if ("size".equals(name) || "isEmpty".equals(name)) {
            PsiClass col = JavaPsiFacade.getInstance(project).findClass("java.util.Collection", GlobalSearchScope.allScope(project));
            PsiClass map = JavaPsiFacade.getInstance(project).findClass("java.util.Map", GlobalSearchScope.allScope(project));
            boolean isCol = col != null && (type instanceof PsiArrayType || isInheritor(type, col));
            boolean isMap = isInheritor(type, map);

            if (isCol || isMap) {
                return resolveMethodInType(type, "size".equals(name) ? "size" : "isEmpty", project);
            }
        }
        if ("keys".equals(name) || "values".equals(name) || "entrySet".equals(name)) {
            PsiClass map = JavaPsiFacade.getInstance(project).findClass("java.util.Map", GlobalSearchScope.allScope(project));
            if (isInheritor(type, map)) {
                return resolveMethodInType(type, name, project);
            }
        }
        return null;
    }

    private static boolean isInheritor(@NotNull PsiType type, PsiClass clazz) {
        if (clazz == null) {
            return false;
        }
        if (type instanceof PsiClassType classType) {
            PsiClass resolve = classType.resolve();
            if (resolve != null) {
                return resolve.isInheritor(clazz, true);
            }
        }
        return false;
    }

    /**
     * Resolves a path expression starting from a given method context.
     *
     * @param contextElement The XML element context (to find the mapper method).
     * @param expression     The expression path (e.g. "user.name").
     * @return The resolved PsiElement (Field or Method), or null if not found.
     */
    @Nullable
    public static PsiElement resolveExpression(@NotNull PsiElement contextElement, @NotNull String expression) {
        String[] parts = expression.split("\\.");
        String rootName = parts[0];
        if (rootName.isEmpty()) return null;

        PsiElement foreachVar = resolveForeachVariable(contextElement, rootName);
        if (foreachVar != null) {
            PsiElement current = foreachVar;
            for (int i = 1; i < parts.length; i++) {
                if ("length".equals(parts[i]) && getType(current) instanceof PsiArrayType) continue;
                current = resolveMemberInType(getType(current), parts[i], contextElement.getProject());
                if (current == null) break;
            }
            return current;
        }

        List<PsiMethod> psiMethods = findCandidatePsiMethods(contextElement);
        for (PsiMethod psiMethod : psiMethods) {
            PsiElement target = resolveTarget(psiMethod, expression, contextElement.getProject());
            if (target != null) {
                return target;
            }
        }
        return null;
    }

    @Nullable
    private static PsiElement resolveForeachVariable(@NotNull PsiElement context, @NotNull String name) {
        PsiElement current = context;
        while (current != null) {
            if (current instanceof XmlTag tag) {
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
        List<PsiMethod> methods = findCandidatePsiMethods(contextElement);
        return methods.isEmpty() ? null : methods.get(0);
    }

    @NotNull
    public static List<PsiMethod> findCandidatePsiMethods(@NotNull PsiElement contextElement) {
        IdDomElement domElement = DomUtil.findDomElement(contextElement, IdDomElement.class);
        if (domElement == null) {
            return Collections.emptyList();
        }
        Object idValue = domElement.getId().getValue();
        if (idValue instanceof PsiMethod) {
            return Collections.singletonList((PsiMethod) idValue);
        }
        if (domElement instanceof Sql sql) {
            return findMethodsIncludingSql(sql, contextElement.getProject());
        }
        return Collections.emptyList();
    }

    @NotNull
    public static List<PsiMethod> findMethodsIncludingSql(@NotNull Sql sql, @NotNull Project project) {
        String sqlId = MapperUtils.getId(sql);
        if (sqlId == null || sqlId.isEmpty()) {
            return Collections.emptyList();
        }
        Mapper mapper = MapperUtils.getMapper(sql);
        String namespace = mapper != null ? MapperUtils.getNamespace(mapper) : null;
        String fullSqlId = (namespace != null && !namespace.isEmpty()) ? namespace + "." + sqlId : null;

        Set<String> targetIds = new HashSet<>();
        targetIds.add(sqlId);
        if (fullSqlId != null) {
            targetIds.add(fullSqlId);
        }

        // Handle transitive includes between <sql> tags in the current mapper
        if (mapper != null) {
            boolean added;
            do {
                added = false;
                for (Sql otherSql : mapper.getSqls()) {
                    if (otherSql.equals(sql)) continue;
                    String otherId = MapperUtils.getId(otherSql);
                    if (otherId != null && !targetIds.contains(otherId)) {
                        XmlTag xmlTag = otherSql.getXmlTag();
                        if (xmlTag != null && includesAny(xmlTag, targetIds)) {
                            targetIds.add(otherId);
                            if (namespace != null && !namespace.isEmpty()) {
                                targetIds.add(namespace + "." + otherId);
                            }
                            added = true;
                        }
                    }
                }
            } while (added);
        }

        List<PsiMethod> candidateMethods = new ArrayList<>();
        Set<PsiMethod> visitedMethods = new HashSet<>();

        // 1. Search in current mapper
        if (mapper != null) {
            for (IdDomElement daoElement : mapper.getDaoElements()) {
                XmlTag daoTag = daoElement.getXmlTag();
                if (daoTag != null && includesAny(daoTag, targetIds)) {
                    Object value = daoElement.getId().getValue();
                    if (value instanceof PsiMethod psiMethod) {
                        if (visitedMethods.add(psiMethod)) {
                            candidateMethods.add(psiMethod);
                        }
                    }
                }
            }
        }

        // 2. If none found in current mapper, search across all mappers in project
        if (candidateMethods.isEmpty()) {
            for (Mapper otherMapper : MapperUtils.findMappers(project)) {
                if (otherMapper.equals(mapper)) continue;
                for (IdDomElement daoElement : otherMapper.getDaoElements()) {
                    XmlTag daoTag = daoElement.getXmlTag();
                    if (daoTag != null && includesAny(daoTag, targetIds)) {
                        Object value = daoElement.getId().getValue();
                        if (value instanceof PsiMethod psiMethod) {
                            if (visitedMethods.add(psiMethod)) {
                                candidateMethods.add(psiMethod);
                            }
                        }
                    }
                }
            }
        }

        return candidateMethods;
    }

    private static boolean includesAny(@NotNull XmlTag rootTag, @NotNull Set<String> targetIds) {
        for (XmlTag child : rootTag.getSubTags()) {
            if ("include".equals(child.getName())) {
                String refid = child.getAttributeValue("refid");
                if (refid != null && targetIds.contains(refid)) {
                    return true;
                }
            }
            if (includesAny(child, targetIds)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断给定的方法是否定义了指定名称的参数或属性根对象。
     * 当处于公共 <sql> 时，可据此判断当前表达式是否针对该方法，
     * 从而决定是否应当作为硬引用（校验其字段是否存在）。
     */
    public static boolean hasRootParam(PsiMethod psiMethod, String rootParamName, Project project) {
        if (psiMethod == null || rootParamName == null || rootParamName.isEmpty()) return false;
        PsiParameterList parameterList = psiMethod.getParameterList();
        PsiConstantEvaluationHelper constantEvaluationHelper = JavaPsiFacade.getInstance(project).getConstantEvaluationHelper();
        int parametersCount = parameterList.getParametersCount();

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

            if (isAnnotated && Objects.equals(definedName, rootParamName)) {
                return true;
            } else if (!isAnnotated) {
                if (Objects.equals(definedName, rootParamName)) {
                    return true;
                } else if (parametersCount == 1) {
                    PsiElement field = resolveFieldInType(psiParameter.getType(), rootParamName, project);
                    if (field == null) {
                        field = resolveMethodInType(psiParameter.getType(), rootParamName, project);
                    }
                    if (field != null) return true;
                }
            }
        }
        return false;
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
                            if ("length".equals(parts[i]) && getType(current) instanceof PsiArrayType) continue;
                            current = resolveMemberInType(getType(current), parts[i], project);
                            if (current == null) break;
                        }
                        if (current != null) return current;
                    }
                }
            }

            if (match) {
                PsiElement current = psiParameter;
                for (int i = 1; i < parts.length; i++) {
                    if ("length".equals(parts[i]) && getType(current) instanceof PsiArrayType) continue;
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
        if (!(parent instanceof XmlAttribute attribute)) return null;
        XmlTag tag = attribute.getParent();
        if (tag == null || !"foreach".equals(tag.getName())) return null;

        String attrName = attribute.getName();
        if ("index".equals(attrName)) {
            return PsiTypes.intType().getBoxedType(attributeValue.getManager(), attributeValue.getResolveScope());
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
        if (type instanceof PsiClassType classType) {
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
                    return substitutor.substitute(mapClass.getTypeParameters()[1]);
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

            // 支持 Java Bean 属性 Getter 解析：如 businessTypes -> getBusinessTypes() / isBusinessTypes()
            if (!methodName.isEmpty()) {
                String capitalized = Character.toUpperCase(methodName.charAt(0)) + methodName.substring(1);
                PsiMethod[] getterMethods = psiClass.findMethodsByName("get" + capitalized, true);
                if (getterMethods.length > 0) return getterMethods[0];

                PsiMethod[] isMethods = psiClass.findMethodsByName("is" + capitalized, true);
                if (isMethods.length > 0) return isMethods[0];
            }
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

    /**
     * If the character/sequence at {@code currentIndex} starts a string literal
     * (e.g. {@code "}, {@code '}, {@code &quot;}, or {@code &apos;}), returns the index of the last character
     * of that literal so the caller can advance past it.
     * Otherwise returns -1.
     *
     * @param text         The full text containing the expression.
     * @param currentIndex The current index being examined.
     * @param endLimit     The boundary limit (usually excluding outer attribute quotes).
     * @return The end index of the literal (inclusive), or -1 if not a literal start.
     */
    public static int skipStringLiteral(@NotNull String text, int currentIndex, int endLimit) {
        if (text.startsWith("&quot;", currentIndex)) {
            int next = text.indexOf("&quot;", currentIndex + 6);
            return (next != -1 && next < endLimit) ? next + 5 : endLimit - 1;
        }
        if (text.startsWith("&apos;", currentIndex)) {
            int next = text.indexOf("&apos;", currentIndex + 6);
            return (next != -1 && next < endLimit) ? next + 5 : endLimit - 1;
        }
        char c = text.charAt(currentIndex);
        if (c == '"' || c == '\'') {
            int j = currentIndex + 1;
            while (j < endLimit) {
                if (text.charAt(j) == '\\') {
                    j += 2;
                    continue;
                }
                if (text.charAt(j) == c) {
                    return j;
                }
                j++;
            }
            return endLimit - 1;
        }
        return -1;
    }
}
