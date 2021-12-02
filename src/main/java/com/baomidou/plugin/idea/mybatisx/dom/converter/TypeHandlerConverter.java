package com.baomidou.plugin.idea.mybatisx.dom.converter;

import com.baomidou.plugin.idea.mybatisx.util.JavaUtils;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.PsiClassConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * The type Jdbc type converter.
 *
 * @author ls9527
 */
public class TypeHandlerConverter extends ConverterAdaptor<PsiClass>
    implements CustomReferenceConverter<PsiClass> {

    private static final String ORG_APACHE_IBATIS_TYPE_TYPE_HANDLER = "org.apache.ibatis.type.TypeHandler";

    final PsiClassConverter psiClassConverter = new PsiClassConverter();

    @Override
    public String getErrorMessage(@Nullable String classStr, ConvertContext context) {
        final PsiClass psiClass = psiClassConverter.fromString(classStr, context);
        if (psiClass != null && checkIsTypeHandler(psiClass)) {
            return "the class is not a TypeHandler, class: " + classStr + " ";
        }
        return super.getErrorMessage(classStr, context);
    }

    @Nullable
    @Override
    public PsiClass fromString(@Nullable String classStr, ConvertContext context) {
        final PsiClass psiClass = psiClassConverter.fromString(classStr, context);
        if (psiClass != null && checkIsTypeHandler(psiClass)) {
            return null;
        }
        return psiClassConverter.fromString(classStr, context);
    }

    @NotNull
    @Override
    public PsiReference[] createReferences(GenericDomValue<PsiClass> value, PsiElement element, ConvertContext context) {
        return psiClassConverter.createReferences(value, element, context);
    }

    private boolean checkIsTypeHandler(PsiClass psiClass) {
        Optional<PsiClass> typeHandlerClassOptional = JavaUtils.findClazz(psiClass.getProject(),
            ORG_APACHE_IBATIS_TYPE_TYPE_HANDLER);
        if (!typeHandlerClassOptional.isPresent()) {
            return true;
        }
        PsiClass typeHandlerCla = typeHandlerClassOptional.get();
        if (!psiClass.isInheritor(typeHandlerCla, true)) {
            return true;
        }
        return false;
    }

}
