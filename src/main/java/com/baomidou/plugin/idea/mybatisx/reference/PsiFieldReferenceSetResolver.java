package com.baomidou.plugin.idea.mybatisx.reference;

import com.baomidou.plugin.idea.mybatisx.dom.MapperBacktrackingUtils;
import com.baomidou.plugin.idea.mybatisx.util.JavaUtils;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @author yanglin
 */
public class PsiFieldReferenceSetResolver extends ContextReferenceSetResolver<XmlAttributeValue, PsiField> {

    protected PsiFieldReferenceSetResolver(XmlAttributeValue from) {
        super(from);
    }

    @NotNull
    @Override
    public String getText() {
        return getElement().getValue();
    }

    @Override
    public Optional<PsiField> resolve(@NotNull PsiField current, @NotNull String text) {
        PsiType type = current.getType();
        if (type instanceof PsiClassReferenceType && !((PsiClassReferenceType) type).hasParameters()) {
            PsiClass clazz = ((PsiClassReferenceType) type).resolve();
            if (null != clazz) {
                return JavaUtils.findSettablePsiField(clazz, text);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<PsiField> getStartElement(@Nullable String firstText) {
        Optional<PsiClass> clazz = MapperBacktrackingUtils.getPropertyClazz(getElement());
        PsiClass psiClass = clazz.get();
        assert firstText != null;
        // 原先可以使用    JavaUtils.findSettablePsiField(clazz.get(), firstText) ,
        //  这种方式无法找到 lombok 插件的 setter方法返回值是Class类型的方式， 所以用这种方式来兼容
        PsiMethod propertySetter = PropertyUtil.findPropertySetter(psiClass, firstText, false, true);
        return null == propertySetter ? Optional.empty() : Optional.ofNullable(PropertyUtil.findPropertyField(psiClass,firstText,false));
    }

}
