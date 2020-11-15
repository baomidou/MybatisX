package com.baomidou.plugin.idea.mybatisx.dom.converter;

import com.baomidou.plugin.idea.mybatisx.reference.ContextReferenceSetResolver;
import com.baomidou.plugin.idea.mybatisx.util.JavaUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.ReferenceSetBase;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.GenericDomValue;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The type Jdbc type converter.
 *
 * @author ls9527
 */
public class JdbcTypeConverter extends ConverterAdaptor<XmlAttributeValue> implements CustomReferenceConverter<XmlAttributeValue> {

    @NotNull
    @Override
    public PsiReference[] createReferences(GenericDomValue<XmlAttributeValue> value, PsiElement element, ConvertContext context) {
        final String stringValue = value.getStringValue();
        if (stringValue == null) {
            return PsiReference.EMPTY_ARRAY;
        }
        return new JdbcReference(stringValue, element).getPsiReferences();
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

    private static String clazzName = "org.apache.ibatis.type.JdbcType";
    private Optional<PsiClass> findJdbcTypeClass(Project project) {
        return JavaUtils.findClazz(project, clazzName);
    }

    private class JdbcReference extends ReferenceSetBase<PsiReference> {

        public JdbcReference(String text, @NotNull PsiElement element) {
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

    JdbcTypeVariantHolder jdbcTypeVariantHolder = new JdbcTypeVariantHolder();

    /**
     * cache the variants
     */
    static class JdbcTypeVariantHolder{
        Object[] variants;

        public synchronized void setVariants(Object[] variants) {
            this.variants = variants;
        }

        public Object[] getVariants() {
            return variants;
        }
    }


    private class JdbcTypePsiReferenceBase extends PsiReferenceBase {

        private final ContextReferenceSetResolver<XmlAttributeValue, PsiField> resolver;
        private int index;

        public JdbcTypePsiReferenceBase(@NotNull XmlAttributeValue element, TextRange rangeInElement, int index) {
            super(element, rangeInElement, false);
            this.index = index;
            resolver = new JdbcTypeContextReferenceSetResolver(element);
        }

        @Override
        public @Nullable PsiElement resolve() {
            Optional<PsiField> resolve = resolver.resolve(index);
            return resolve.orElse(null);
        }


        @NotNull
        @Override
        public Object[] getVariants() {
            Project project = resolver.getProject();
            Object[] objects = jdbcTypeVariantHolder.getVariants();
            if (objects != null) {
                return objects;
            }
            // 找不到JdbcType类则始终不缓存
            Optional<PsiClass> classOptional = findJdbcTypeClass(project);
            if (!classOptional.isPresent()) {
                return PsiReference.EMPTY_ARRAY;
            }
            PsiClass psiClass = classOptional.get();
            List<Object> fields = new ArrayList<>(psiClass.getAllFields().length);
            // 过滤非JdbcType类型的变量
            for (PsiField enumField : psiClass.getAllFields()) {
                if (enumField.getType().getCanonicalText().equalsIgnoreCase(psiClass.getQualifiedName())) {
                    fields.add(enumField);
                }
            }
            objects = fields.toArray();
            jdbcTypeVariantHolder.setVariants(objects);
            return objects;
        }
    }

    private class JdbcTypeContextReferenceSetResolver extends ContextReferenceSetResolver<XmlAttributeValue, PsiField> {


        /**
         * Instantiates a new Context reference set resolver.
         *
         * @param element the element
         */
        protected JdbcTypeContextReferenceSetResolver(@NotNull XmlAttributeValue element) {
            super(element);
        }

        @Override
        public @NotNull Optional<PsiField> getStartElement(@Nullable String firstText) {
            if (firstText == null) {
                return Optional.empty();
            }
            Optional<PsiClass> jdbcTypeOptional = findJdbcTypeClass(project);
            if (!jdbcTypeOptional.isPresent()) {
                return Optional.empty();
            }
            PsiClass psiClass = jdbcTypeOptional.get();
            String paramText = firstText.toUpperCase();
            PsiField foundField = null;
            for (PsiField enumField : psiClass.getAllFields()) {
                if (enumField.getName() != null
                    && enumField.getName().equalsIgnoreCase(paramText)) {
                    if (enumField.getType().getCanonicalText().equalsIgnoreCase(psiClass.getQualifiedName())) {
                        foundField = enumField;
                        break;
                    }
                }
            }
            if (foundField != null) {
                return Optional.of(foundField);
            }
            return Optional.empty();
        }


        @Override
        public @NotNull String getText() {
            return getElement().getValue();
        }

    }
}
