package com.baomidou.plugin.idea.mybatisx.dom.converter;

import com.baomidou.plugin.idea.mybatisx.dom.model.IdDomElement;
import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.util.MapperUtils;
import com.baomidou.plugin.idea.mybatisx.util.MybatisConstants;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReferenceProvider;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.PsiClassConverter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * The type Id based tag converter.
 *
 * @author yanglin
 */
public abstract class IdBasedTagConverter extends ConverterAdaptor<XmlAttributeValue> implements CustomReferenceConverter<XmlAttributeValue> {

    private final boolean crossMapperSupported;

    /**
     * Instantiates a new Id based tag converter.
     */
    public IdBasedTagConverter() {
        this(true);
    }

    /**
     * Instantiates a new Id based tag converter.
     *
     * @param crossMapperSupported the cross mapper supported
     */
    protected IdBasedTagConverter(boolean crossMapperSupported) {
        this.crossMapperSupported = crossMapperSupported;
    }

    @Nullable
    @Override
    public XmlAttributeValue fromString(@Nullable @NonNls String value, ConvertContext context) {
        return matchIdDomElement(selectStrategy(context).getValue(), value, context).orElse(null);
    }

    private Optional<XmlAttributeValue> matchIdDomElement(Collection<? extends IdDomElement> idDomElements, String value, ConvertContext context) {
        Mapper contextMapper = MapperUtils.getMapper(context.getInvocationElement());
        for (IdDomElement idDomElement : idDomElements) {
            if (MapperUtils.getIdSignature(idDomElement).equals(value) ||
                    MapperUtils.getIdSignature(idDomElement, contextMapper).equals(value)) {
                return Optional.of(idDomElement.getId().getXmlAttributeValue());
            }
        }
        return Optional.empty();
    }

    @Nullable
    @Override
    public String toString(@Nullable XmlAttributeValue xmlAttribute, ConvertContext context) {
        DomElement domElement = DomUtil.getDomElement(xmlAttribute.getParent().getParent());
        if (!(domElement instanceof IdDomElement)) {
            return null;
        }
        Mapper contextMapper = MapperUtils.getMapper(context.getInvocationElement());
        return MapperUtils.getIdSignature((IdDomElement) domElement, contextMapper);
    }

    private TraverseStrategy selectStrategy(ConvertContext context) {
        return crossMapperSupported ? new CrossMapperStrategy(context) : new InsideMapperStrategy(context);
    }

    /**
     * Gets comparisons.
     *
     * @param mapper  mapper in the project, null if {@link #crossMapperSupported} is false
     * @param context the dom convert context
     * @return the comparisons
     */
    @NotNull
    public abstract Collection<? extends IdDomElement> getComparisons(@Nullable Mapper mapper, ConvertContext context);

    private abstract class TraverseStrategy {
        /**
         * The Context.
         */
        protected ConvertContext context;

        /**
         * Instantiates a new Traverse strategy.
         *
         * @param context the context
         */
        public TraverseStrategy(@NotNull ConvertContext context) {
            this.context = context;
        }

        /**
         * Gets value.
         *
         * @return the value
         */
        public abstract Collection<? extends IdDomElement> getValue();
    }

    private class InsideMapperStrategy extends TraverseStrategy {

        /**
         * Instantiates a new Inside mapper strategy.
         *
         * @param context the context
         */
        public InsideMapperStrategy(@NotNull ConvertContext context) {
            super(context);
        }

        @Override
        public Collection<? extends IdDomElement> getValue() {
            return getComparisons(null, context);
        }

    }

    private class CrossMapperStrategy extends TraverseStrategy {

        /**
         * Instantiates a new Cross mapper strategy.
         *
         * @param context the context
         */
        public CrossMapperStrategy(@NotNull ConvertContext context) {
            super(context);
        }

        @Override
        public Collection<? extends IdDomElement> getValue() {
            List<IdDomElement> result = Lists.newArrayList();
            for (Mapper mapper : MapperUtils.findMappers(context.getProject())) {
                result.addAll(getComparisons(mapper, context));
            }
            return result;
        }

    }

    @NotNull
    @Override
    public PsiReference[] createReferences(GenericDomValue<XmlAttributeValue> value, PsiElement element, ConvertContext context) {
        return PsiClassConverter.createJavaClassReferenceProvider(value, null, new ValueReferenceProvider(context)).getReferencesByElement(element);
    }

    private class ValueReferenceProvider extends JavaClassReferenceProvider {

        private ConvertContext context;

        private ValueReferenceProvider(ConvertContext context) {
            this.context = context;
        }

        @Nullable
        @Override
        public GlobalSearchScope getScope(Project project) {
            return GlobalSearchScope.allScope(project);
        }

        /**
         * It looks like hacking here, as it's a little hard to handle so many different cases as JetBrains does
         */
        @NotNull
        @Override
        public PsiReference[] getReferencesByString(String text, @NotNull PsiElement position, int offsetInPosition) {
            List<PsiReference> refs = Lists.newArrayList(super.getReferencesByString(text, position, offsetInPosition));
            ValueReference vr = new ValueReference(position, getTextRange(position), context, text);
            if (!refs.isEmpty() && 0 != vr.getVariants().length) {
                refs.remove(refs.size() - 1);
                refs.add(vr);
            }
            return refs.toArray(new PsiReference[refs.size()]);
        }

        private TextRange getTextRange(PsiElement element) {
            String text = element.getText();
            int index = text.lastIndexOf(MybatisConstants.DOT_SEPARATOR);
            return -1 == index ? ElementManipulators.getValueTextRange(element) : TextRange.create(text.substring(0, index).length() + 1, text.length() - 1);
        }
    }

    private class ValueReference extends PsiReferenceBase<PsiElement> {

        private ConvertContext context;
        private String text;

        /**
         * Instantiates a new Value reference.
         *
         * @param element the element
         * @param rng     the rng
         * @param context the context
         * @param text    the text
         */
        public ValueReference(@NotNull PsiElement element, TextRange rng, ConvertContext context, String text) {
            super(element, rng, false);
            this.context = context;
            this.text = text;
        }

        @Nullable
        @Override
        public PsiElement resolve() {
            return IdBasedTagConverter.this.fromString(text, context);
        }

        @NotNull
        @Override
        public Object[] getVariants() {
            Set<String> res = getElement().getText().contains(MybatisConstants.DOT_SEPARATOR) ? setupContextIdSignature() : setupGlobalIdSignature();
            return res.toArray(new String[res.size()]);
        }

        private Set<String> setupContextIdSignature() {
            Set<String> res = Sets.newHashSet();
            String ns = text.substring(0, text.lastIndexOf(MybatisConstants.DOT_SEPARATOR));
            for (IdDomElement ele : selectStrategy(context).getValue()) {
                if (MapperUtils.getNamespace(ele).equals(ns)) {
                    res.add(MapperUtils.getId(ele));
                }
            }
            return res;
        }

        private Set<String> setupGlobalIdSignature() {
            Mapper contextMapper = MapperUtils.getMapper(context.getInvocationElement());
            Collection<? extends IdDomElement> idDomElements = selectStrategy(context).getValue();
            Set<String> res = new HashSet<String>(idDomElements.size());
            for (IdDomElement ele : idDomElements) {
                res.add(MapperUtils.getIdSignature(ele, contextMapper));
            }
            return res;
        }

    }

}
