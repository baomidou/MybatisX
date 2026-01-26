package com.baomidou.plugin.idea.mybatisx.reference;

import com.baomidou.plugin.idea.mybatisx.annotation.Annotation;
import com.baomidou.plugin.idea.mybatisx.dom.model.IdDomElement;
import com.baomidou.plugin.idea.mybatisx.util.JavaUtils;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiConstantEvaluationHelper;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import com.intellij.util.xml.DomUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * <if test="username != null">
 * <when test="username != null">
 * 提示
 */
public class TestParamReferenceContributor extends PsiReferenceContributor {

    public static final PsiElementPattern.Capture<XmlAttributeValue> TEST_ATTRIBUTE_VALUE = PlatformPatterns.psiElement(XmlAttributeValue.class)
        .withParent(PlatformPatterns.psiElement(XmlAttribute.class)
            .withName("test")
            .withParent(PlatformPatterns.psiElement(XmlTag.class)
                .withName(StandardPatterns.string().oneOf("if", "when", "foreach", "bind"))));

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(TEST_ATTRIBUTE_VALUE, new PsiReferenceProvider() {
            @Override
            public @NotNull PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                if (!(element instanceof XmlAttributeValue)) {
                    return PsiReference.EMPTY_ARRAY;
                }
                String text = element.getText();
                List<PsiReference> references = new ArrayList<>();
                int offset = 0;
                if (text.startsWith("\"") || text.startsWith("'")) {
                    offset = 1;
                }

                StringBuilder sb = new StringBuilder();
                int startCoords = -1;

                // Simple state machine for tokenizer
                // Needs to handle:
                // 1. Identifiers (user, name, etc.)
                // 2. Dots (.)
                // 3. Method calls ()
                // 4. Static access @Class@Member

                // For now, keeping the simple scanner but allowing @ in identifier
                // Ideally, we should parse tokens properly.
                // Let's enhance scanner to support @ and dot scanning more robustly.

                for (int i = offset; i < text.length(); i++) {
                    char c = text.charAt(i);
                    // Check for closing quote
                    if ((c == '"' || c == '\'') && i == text.length() - 1) {
                         break;
                    }

                    if (Character.isJavaIdentifierPart(c) || c == '.' || c == '@') {
                        if (startCoords == -1) {
                            startCoords = i;
                        }
                        sb.append(c);
                    } else if (c == '(') {
                        // Method start. If we have a pending token, register it.
                         if (startCoords != -1) {
                            String value = sb.toString();
                            // Mark as method call? resolve() will check next chars?
                            // No, resolve() is lazy. We scan here.
                            // If we encounter '(', the previous token is likely a method name.
                            // We can just add it as a reference, and the resolver will check context.
                             addReference(element, references, value, startCoords);
                            sb = new StringBuilder();
                            startCoords = -1;
                        }
                    } else {
                        if (startCoords != -1) {
                            addReference(element, references, sb.toString(), startCoords);
                            sb = new StringBuilder();
                            startCoords = -1;
                        }
                    }
                }
                if (startCoords != -1) {
                    addReference(element, references, sb.toString(), startCoords);
                }

                return references.toArray(new PsiReference[0]);
            }
        });
    }

    private void addReference(PsiElement element, List<PsiReference> references, String value, int startOffset) {
        if (isKeyword(value)) return;
        if (value.matches("[\\d\\.]+")) return;

        // Handle Static: @Class@
        if (value.startsWith("@") && value.endsWith("@")) {
             // Class reference
             references.add(new TestParamReference(element, new TextRange(startOffset, startOffset + value.length()), value));
             return;
        }

        references.add(new TestParamReference(element, new TextRange(startOffset, startOffset + value.length()), value));
    }

    private boolean isKeyword(String value) {
        return "and".equals(value) || "or".equals(value) || "null".equals(value) || "true".equals(value) || "false".equals(value)
             || "not".equals(value) || "new".equals(value) || "instanceof".equals(value)
             || "lt".equals(value) || "gt".equals(value) || "lte".equals(value) || "gte".equals(value)
             || "eq".equals(value) || "neq".equals(value);
    }

    private static class TestParamReference extends PsiReferenceBase<PsiElement> {
        private final String value;

        public TestParamReference(@NotNull PsiElement element, TextRange rangeInElement, String value) {
            super(element, rangeInElement);
            this.value = value;
        }

        @Override
        public @Nullable PsiElement resolve() {
            // Check for Static Access
            if (value.startsWith("@")) {
                return resolveStatic(value);
            }

            PsiMethod psiMethod = getPsiMethod();
            if (psiMethod == null) return null;
            return resolveTarget(psiMethod, this.value);
        }

        private PsiElement resolveStatic(String value) {
            Project project = myElement.getProject();
            JavaPsiFacade facade = JavaPsiFacade.getInstance(project);
            GlobalSearchScope scope = GlobalSearchScope.allScope(project);

            // Case 1: @Class@  (e.g. @java.lang.Math@)
            if (value.endsWith("@")) {
                 String className = value.substring(1, value.length() - 1);
                 return facade.findClass(className, scope);
            }

            // Case 2: @Class@Member (e.g. @java.lang.Math@PI)
            // Scanner splits this into one token? or multiple?
            // Our scanner: Character.isJavaIdentifierPart includes letters, numbers.
            // '.' and '@' are manually included.
            // So "@java.lang.Math@PI" is one token.

            int secondAt = value.indexOf('@', 1);
            if (secondAt > 1 && secondAt < value.length() - 1) {
                String className = value.substring(1, secondAt);
                String memberName = value.substring(secondAt + 1);

                PsiClass psiClass = facade.findClass(className, scope);
                if (psiClass != null) {
                    // Try field
                    PsiField field = psiClass.findFieldByName(memberName, true);
                    if (field != null && field.hasModifierProperty(PsiModifier.STATIC)) {
                        return field;
                    }
                    // Try method
                    PsiMethod[] methods = psiClass.findMethodsByName(memberName, true);
                    for (PsiMethod m : methods) {
                         if (m.hasModifierProperty(PsiModifier.STATIC)) {
                             return m;
                         }
                    }
                }
            }
            return null;
        }

        @Nullable
        private PsiMethod getPsiMethod() {
            IdDomElement domElement = DomUtil.findDomElement(myElement, IdDomElement.class);
            if (domElement == null) return null;
            Object idValue = domElement.getId().getValue();
            if (!(idValue instanceof PsiMethod)) return null;
            return (PsiMethod) idValue;
        }

        @Nullable
        private PsiElement resolveTarget(PsiMethod psiMethod, String targetPath) {
            PsiParameterList parameterList = psiMethod.getParameterList();
            Project project = myElement.getProject();
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

        private PsiType getType(PsiElement element) {
            if (element instanceof PsiVariable) return ((PsiVariable) element).getType();
            if (element instanceof PsiMethod) return ((PsiMethod) element).getReturnType();
            return null;
        }

        private PsiElement resolveMemberInType(PsiType type, String name, Project project) {
            if (type == null) return null;
            PsiElement member = resolveFieldInType(type, name, project);
            if (member == null) {
                member = resolveMethodInType(type, name, project);
            }
            return member;
        }

        @Nullable
        private PsiElement resolveFieldInType(PsiType type, String fieldName, Project project) {
            String canonicalText = type.getCanonicalText();
            Optional<PsiClass> clazzOpt = JavaUtils.findClazz(project, canonicalText);
            if (clazzOpt.isPresent()) {
                PsiClass psiClass = clazzOpt.get();
                for (PsiField field : psiClass.getAllFields()) {
                    if (Objects.equals(field.getName(), fieldName)) {
                        return field;
                    }
                }
            }
            return null;
        }

        @Nullable
        private PsiElement resolveMethodInType(PsiType type, String methodName, Project project) {
            String canonicalText = type.getCanonicalText();
            Optional<PsiClass> clazzOpt = JavaUtils.findClazz(project, canonicalText);
            if (clazzOpt.isPresent()) {
                 PsiClass psiClass = clazzOpt.get();
                 PsiMethod[] methods = psiClass.findMethodsByName(methodName, true);
                 if (methods.length > 0) return methods[0];
            }
            return null;
        }

        @Override
        public Object @NotNull [] getVariants() {
            PsiMethod psiMethod = getPsiMethod();
            if (psiMethod == null) return new Object[0];

            int lastDot = value.lastIndexOf('.');
            if (lastDot == -1) {
                return getTopLevelVariants(psiMethod);
            } else {
                String prefix = value.substring(0, lastDot);
                String lookupPrefix = value.substring(0, lastDot + 1);

                PsiElement target = resolveTarget(psiMethod, prefix);
                PsiType type = getType(target);

                if (type != null) {
                   return getMemberVariants(type, lookupPrefix, myElement.getProject());
                }
            }
            return new Object[0];
        }

        private Object[] getTopLevelVariants(PsiMethod psiMethod) {
            List<Object> variants = new ArrayList<>();
            PsiParameterList parameterList = psiMethod.getParameterList();
            Project project = myElement.getProject();
            PsiConstantEvaluationHelper constantEvaluationHelper = JavaPsiFacade.getInstance(project).getConstantEvaluationHelper();

            for (PsiParameter psiParameter : parameterList.getParameters()) {
                 PsiAnnotation annotation = psiParameter.getAnnotation(Annotation.PARAM.getQualifiedName());
                 if (annotation != null) {
                     PsiAnnotationMemberValue paramAnnotationValue = annotation.findAttributeValue("value");
                     String paramValue = (String) constantEvaluationHelper.computeConstantExpression(paramAnnotationValue);
                     if (paramValue != null) {
                        variants.add(LookupElementBuilder.create(paramValue)
                            .withTypeText(psiParameter.getType().getPresentableText())
                            .withIcon(psiParameter.getIcon(0)));
                     }
                 } else {
                     String name = psiParameter.getName();
                     if (name != null) {
                         variants.add(LookupElementBuilder.create(name)
                             .withTypeText(psiParameter.getType().getPresentableText())
                             .withIcon(psiParameter.getIcon(0)));
                     }
                     if (parameterList.getParametersCount() == 1) {
                         addMemberVariants(variants, psiParameter.getType(), "", project);
                     }
                 }
            }
            // Add keywords
            addKeywords(variants);
            return variants.toArray();
        }

        private void addKeywords(List<Object> variants) {
            String[] keywords = { "and", "or", "null", "true", "false", "new", "not", "eq", "neq", "lt", "gt", "lte", "gte" };
            for (String k : keywords) {
                variants.add(LookupElementBuilder.create(k).bold());
            }
        }

        private Object[] getMemberVariants(PsiType type, String prefix, Project project) {
            List<Object> variants = new ArrayList<>();
            addMemberVariants(variants, type, prefix, project);
            return variants.toArray();
        }

        private void addMemberVariants(List<Object> variants, PsiType type, String prefix, Project project) {
            String canonicalText = type.getCanonicalText();
            Optional<PsiClass> clazzOpt = JavaUtils.findClazz(project, canonicalText);
             if (clazzOpt.isPresent()) {
                PsiClass psiClass = clazzOpt.get();
                for (PsiField field : psiClass.getAllFields()) {
                    variants.add(LookupElementBuilder.create(prefix + field.getName())
                        .withTypeText(field.getType().getPresentableText())
                        .withIcon(field.getIcon(0)));
                }
                for (PsiMethod method : psiClass.getAllMethods()) {
                     if (!method.isConstructor() && BaseJpaTestIsOk(method)) {
                         variants.add(LookupElementBuilder.create(prefix + method.getName())
                             .withTailText("()")
                             .withTypeText(method.getReturnType().getPresentableText())
                             .withIcon(method.getIcon(0)));
                     }
                }
            }
        }

        private boolean BaseJpaTestIsOk(PsiMethod method) {
            // Filter Object methods?
            String name = method.getName();
            return !"wait".equals(name) && !"notify".equals(name) && !"notifyAll".equals(name)
                && !"getClass".equals(name) && !"clone".equals(name) && !"finalize".equals(name);
        }
    }
}
