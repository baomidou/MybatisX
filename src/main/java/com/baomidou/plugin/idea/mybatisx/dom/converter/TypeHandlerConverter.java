package com.baomidou.plugin.idea.mybatisx.dom.converter;

import com.baomidou.plugin.idea.mybatisx.reference.ContextReferenceSetResolver;
import com.baomidou.plugin.idea.mybatisx.util.JavaUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.psi.util.ReferenceSetBase;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.Query;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * The type Jdbc type converter.
 *
 * @author ls9527
 */
public class TypeHandlerConverter extends ConverterAdaptor<XmlAttributeValue> implements CustomReferenceConverter<XmlAttributeValue> {

    @NotNull
    @Override
    public PsiReference[] createReferences(GenericDomValue<XmlAttributeValue> value, PsiElement element, ConvertContext context) {
        final String stringValue = value.getStringValue();
        if (stringValue == null) {
            return PsiReference.EMPTY_ARRAY;
        }
        return new TypeHandlerReference(stringValue, element).getPsiReferences();
    }

    @Nullable
    @Override
    public XmlAttributeValue fromString(@Nullable @NonNls String s, ConvertContext context) {
        DomElement ctxElement = context.getInvocationElement();
        if (ctxElement instanceof GenericAttributeValue) {
            GenericAttributeValue genericAttributeValue = (GenericAttributeValue) ctxElement;
            return genericAttributeValue.getXmlAttributeValue();
        }
        return null;
    }


    private class TypeHandlerReference extends ReferenceSetBase<PsiReference> {

        public TypeHandlerReference(String text, @NotNull PsiElement element) {
            super(text, element, ElementManipulators.getOffsetInElement(element), DOT_SEPARATOR);
        }

        @Nullable
        @NonNls
        @Override
        protected PsiReference createReference(TextRange range, int index) {
            XmlAttributeValue element = (XmlAttributeValue) getElement();
            return null == element ? null : new JdbcTypePsiReferenceBase(element, range, index);
        }

    }

    private static final String ORG_APACHE_IBATIS_TYPE_TYPE_HANDLER = "org.apache.ibatis.type.TypeHandler";

    private Collection<PsiClass> searchTypeHandler(Project project) {
        Optional<PsiClass> typeHandlerOptional = JavaUtils.findClazz(project, ORG_APACHE_IBATIS_TYPE_TYPE_HANDLER);
        if (typeHandlerOptional.isPresent()) {
            PsiClass psiClass = typeHandlerOptional.get();
            Query<PsiClass> search = ClassInheritorsSearch.search(psiClass, true);
            return search.findAll();

        }
        return Collections.emptyList();
    }


    private class JdbcTypePsiReferenceBase extends PsiReferenceBase<XmlAttributeValue> {
        private final ContextReferenceSetResolver<XmlAttributeValue, PsiClass> resolver;
        private int index;

        public JdbcTypePsiReferenceBase(@NotNull XmlAttributeValue element, TextRange rangeInElement, int index) {
            super(element, rangeInElement, false);
            this.index = index;
            resolver = new JdbcTypeContextReferenceSetResolver(element);
        }


        @Override
        public @Nullable PsiElement resolve() {
            String value = myElement.getValue();
            Optional<PsiClass> psiClassOptional = JavaUtils.findClazz(myElement.getProject(), value);
            if (!psiClassOptional.isPresent()) {
                return null;
            }
            PsiClass psiClass = psiClassOptional.get();
            Optional<PsiClass> typeHandlerClassOptional = JavaUtils.findClazz(myElement.getProject(), ORG_APACHE_IBATIS_TYPE_TYPE_HANDLER);
            if (!typeHandlerClassOptional.isPresent()) {
                return null;
            }
            PsiClass typeHandlerCla = typeHandlerClassOptional.get();
            if (psiClass.isInheritor(typeHandlerCla, true)) {
                return psiClass;
            }
            return null;
        }


        @NotNull
        @Override
        public Object[] getVariants() {
            Project project = myElement.getProject();
            Collection<PsiClass> psiClasses = searchTypeHandler(project);
            return psiClasses.stream().filter(Objects::nonNull)
                .filter(psiClazz -> !psiClazz.hasModifierProperty(PsiModifier.ABSTRACT))
                .map(PsiClass::getQualifiedName)
                .toArray();
        }
    }

    private class JdbcTypeContextReferenceSetResolver extends ContextReferenceSetResolver<XmlAttributeValue, PsiClass> {


        /**
         * Instantiates a new Context reference set resolver.
         *
         * @param element the element
         */
        protected JdbcTypeContextReferenceSetResolver(@NotNull XmlAttributeValue element) {
            super(element);
        }

        @Override
        public @NotNull Optional<PsiClass> getStartElement(@Nullable String firstText) {
            if (firstText == null) {
                return Optional.empty();
            }
            Collection<PsiClass> psiClasses = searchTypeHandler(project);
            for (PsiClass psiClass : psiClasses) {
                if (psiClass.getQualifiedName() != null
                    && psiClass.getQualifiedName().equals(firstText)) {
                    return Optional.of(psiClass);
                }
            }
            return Optional.empty();
        }


        @Override
        public @NotNull String getText() {
            return getElement().getValue();
        }

    }
}
