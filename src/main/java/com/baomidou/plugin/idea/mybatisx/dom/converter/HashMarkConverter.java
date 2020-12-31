package com.baomidou.plugin.idea.mybatisx.dom.converter;

import com.baomidou.plugin.idea.mybatisx.annotation.Annotation;
import com.baomidou.plugin.idea.mybatisx.dom.model.GroupTwo;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.GenericDomValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HashMarkConverter extends ConverterAdaptor<PsiMember> implements CustomReferenceConverter<XmlAttributeValue> {

    /**
     * 在 1.0 版本的跳转中， 只支持方法跳转
     *
     * @param s
     * @param context
     * @return
     */
    @Override
    public @Nullable PsiMember fromString(@Nullable String s, ConvertContext context) {
        DomElement domElement = context.getInvocationElement();
        GroupTwo parentOfType = DomUtil.getParentOfType(domElement, GroupTwo.class, true);
        if (parentOfType == null) {
            return null;
        }
        PsiMethod method = getPsiMethod(s, context, parentOfType);
        if (method != null) {
            return method;
        }
        return null;
    }

    @Nullable
    private PsiMethod getPsiMethod(@Nullable String inputText, ConvertContext context, GroupTwo parentOfType) {
        GenericAttributeValue<PsiMethod> id = parentOfType.getId();
        PsiMethod method = id.getValue();
        PsiParameterList parameterList = method.getParameterList();

        JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(context.getProject());
        for (PsiParameter parameter : parameterList.getParameters()) {
            PsiAnnotation annotation = parameter.getAnnotation(Annotation.PARAM.getQualifiedName());
            if (annotation != null) {
                PsiAnnotationMemberValue value = annotation.findDeclaredAttributeValue("value");
                if (value != null) {
                    Object valueObject = psiFacade.getConstantEvaluationHelper().computeConstantExpression(value);
                    if (valueObject != null) {
                        String text = valueObject.toString();
                        String[] split = inputText.split(",");
                        if (text.equals(split[0])) {
                            return method;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public @NotNull PsiReference[] createReferences(GenericDomValue<XmlAttributeValue> value, PsiElement element, ConvertContext context) {
        GroupTwo parentOfType = DomUtil.getParentOfType(value, GroupTwo.class, true);
        PsiMethod psiMethod = getPsiMethod(value.getRawText(), context, parentOfType);
        if (psiMethod != null) {
            assert psiMethod.getReference() != null;
            return new PsiReference[]{psiMethod.getReference()};
        }
        return new PsiReference[0];
    }
}
