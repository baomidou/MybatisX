package com.baomidou.plugin.idea.mybatisx.provider;

import com.baomidou.plugin.idea.mybatisx.dom.model.Delete;
import com.baomidou.plugin.idea.mybatisx.dom.model.IdDomElement;
import com.baomidou.plugin.idea.mybatisx.dom.model.Insert;
import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.dom.model.Select;
import com.baomidou.plugin.idea.mybatisx.dom.model.Update;
import com.baomidou.plugin.idea.mybatisx.util.Icons;
import com.baomidou.plugin.idea.mybatisx.util.JavaUtils;
import com.baomidou.plugin.idea.mybatisx.util.MapperUtils;
import com.google.common.collect.ImmutableList;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Optional;

/**
 * @author yanglin
 */
public class StatementLineMarkerProvider extends SimpleLineMarkerProvider<XmlTag, PsiElement> {

    private static final ImmutableList<Class<?>> TARGET_TYPES = ImmutableList.of(
        Mapper.class,
        Select.class,
        Update.class,
        Insert.class,
        Delete.class
    );

    @Override
    public boolean isTheElement(@NotNull PsiElement element) {
        return element instanceof XmlTag
            && MapperUtils.isElementWithinMybatisFile(element)
            && isTargetType(element);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<PsiElement> apply(@NotNull XmlTag from) {
        DomElement domElement = DomUtil.getDomElement(from);
        if (null == domElement) {
            return Optional.empty();
        }
        // 方法
        else if (domElement instanceof IdDomElement) {
            Optional<PsiMethod> method = JavaUtils.findMethod(from.getProject(), (IdDomElement) domElement);
            return method.map(m -> (PsiElement) m);
        } else {
            XmlTag xmlTag = domElement.getXmlTag();
            if (xmlTag == null) {
                return Optional.empty();
            }
            String namespace = xmlTag.getAttributeValue("namespace");
            Optional<PsiClass> psiClass = JavaUtils.findClazz(from.getProject(), namespace);
            return psiClass.map(m -> (PsiElement) m);
        }
    }

    private boolean isTargetType(PsiElement element) {
        DomElement domElement = DomUtil.getDomElement(element);
        for (Class<?> clazz : TARGET_TYPES) {
            if (clazz.isInstance(domElement))
                return true;
        }
        return false;
    }

    @NotNull
    @Override
    public Navigatable getNavigatable(@NotNull XmlTag from, @NotNull PsiElement target) {
        return (Navigatable) target.getNavigationElement();
    }

    @NotNull
    @Override
    public String getTooltip(@NotNull XmlTag from, @NotNull PsiElement target) {
        String text = null;
        if (target instanceof PsiMethod) {
            PsiMethod psiMethod = (PsiMethod) target;
            PsiClass containingClass = psiMethod.getContainingClass();
            if (containingClass != null) {
                text = containingClass.getQualifiedName();
            }
        }
        if (text == null && target instanceof PsiClass) {
            PsiClass psiClass = (PsiClass) target;
            text = psiClass.getQualifiedName();
        }
        if (text == null) {
            text = target.getContainingFile().getText();
        }
        return "Data access object found - " + text;
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return Icons.STATEMENT_LINE_MARKER_ICON;
    }

}
