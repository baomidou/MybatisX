package com.baomidou.plugin.idea.mybatisx.reference;

import com.baomidou.plugin.idea.mybatisx.dom.MapperBacktrackingUtils;
import com.baomidou.plugin.idea.mybatisx.dom.converter.PropertySetterFind;
import com.baomidou.plugin.idea.mybatisx.service.JavaService;
import com.baomidou.plugin.idea.mybatisx.util.MybatisConstants;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.xml.XmlAttributeValue;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * The type Context psi field reference.
 *
 * @author yanglin
 */
@Setter
@Getter
public class ContextPsiFieldReference extends PsiReferenceBase<XmlAttributeValue> {

    /**
     * The Resolver.
     * -- GETTER --
     * Gets resolver.
     * <p>
     * <p>
     * -- SETTER --
     * Sets resolver.
     *
     * @return the resolver
     * @param resolver the resolver
     */
    protected ContextReferenceSetResolver<XmlAttributeValue, PsiField> resolver;

    /**
     * The Index.
     * -- GETTER --
     * Gets index.
     * <p>
     * <p>
     * -- SETTER --
     * Sets index.
     *
     * @return the index
     * @param index the index
     */
    protected int index;

    /**
     * Instantiates a new Context psi field reference.
     *
     * @param element the element
     * @param range   the range
     * @param index   the index
     */
    public ContextPsiFieldReference(XmlAttributeValue element, TextRange range, int index) {
        super(element, range, false);
        this.index = index;
        resolver = ReferenceSetResolverFactory.createPsiFieldResolver(element);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        Optional<PsiField> resolved = resolver.resolve(index);
        if (resolved.isEmpty()) {
            final Optional<PsiClass> targetClazz = getTargetClazz();
            return targetClazz.orElse(null);
        }
        return resolved.get();
    }


    @Override
    public Object @NotNull[] getVariants() {
        Optional<PsiClass> clazz = getTargetClazz();
        if (clazz.isEmpty()) {
            return PsiReference.EMPTY_ARRAY;
        }
        final PsiClass psiClass = clazz.get();
        PropertySetterFind propertySetterFind = new PropertySetterFind();
        final List<PsiField> setterFields = propertySetterFind.getSetterFields(psiClass);
        return setterFields.toArray(new Object[0]);
    }

    private Optional<PsiClass> getTargetClazz() {
        if (getElement().getValue().contains(MybatisConstants.DOT_SEPARATOR)) {
            int ind = 0 == index ? 0 : index - 1;
            Optional<PsiField> resolved = resolver.resolve(ind);
            if (resolved.isPresent()) {
                return JavaService.getInstance(myElement.getProject()).getReferenceClazzOfPsiField(resolved.get());
            }
        } else {
            return MapperBacktrackingUtils.getPropertyClazz(myElement);
        }
        return Optional.empty();
    }

}
