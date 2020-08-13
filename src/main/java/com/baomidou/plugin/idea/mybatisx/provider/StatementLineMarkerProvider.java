package com.baomidou.plugin.idea.mybatisx.provider;

import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiJvmMember;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import com.baomidou.plugin.idea.mybatisx.dom.model.Delete;
import com.baomidou.plugin.idea.mybatisx.dom.model.GroupTwo;
import com.baomidou.plugin.idea.mybatisx.dom.model.IdDomElement;
import com.baomidou.plugin.idea.mybatisx.dom.model.Insert;
import com.baomidou.plugin.idea.mybatisx.dom.model.Select;
import com.baomidou.plugin.idea.mybatisx.dom.model.Update;
import com.baomidou.plugin.idea.mybatisx.util.Icons;
import com.baomidou.plugin.idea.mybatisx.util.JavaUtils;
import com.baomidou.plugin.idea.mybatisx.util.MapperUtils;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;

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
    @NotNull
    @Override
    public Optional<PsiElement> apply(@NotNull XmlTag from) {
        DomElement domElement = DomUtil.getDomElement(from);
        if (null == domElement) {
            return Optional.absent();
        }
        // 方法
        else if (domElement instanceof IdDomElement) {
            Optional<PsiMethod> method = JavaUtils.findMethod(from.getProject(), (IdDomElement) domElement);
            return method.isPresent() ? Optional.fromNullable(method.get()) : Optional.absent();
        } else {
            Optional<PsiClass> psiClass = JavaUtils.findClazz(from.getProject(), domElement.getXmlTag().getAttributeValue("namespace"));
            return psiClass.isPresent() ? Optional.fromNullable(psiClass.get()) : Optional.absent();
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

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public Navigatable getNavigatable(@NotNull XmlTag from, @NotNull PsiElement target) {
        return (Navigatable) target.getNavigationElement();
    }

    @NotNull
    @Override
    public String getTooltip(@NotNull XmlTag from, @NotNull PsiElement target) {
//        return "Data access object found - " + target.getContainingClass().getQualifiedName();
        String text = null;
        if (target instanceof PsiMethod) {
            PsiMethod psiMethod = (PsiMethod) target;
            text = psiMethod.getContainingClass().getQualifiedName();
        }
        if (text == null && target instanceof PsiClass) {
            PsiClass psiClass = (PsiClass) target;
            text = psiClass.getQualifiedName();
        }
        if(text == null){
            text = target.getText();
        }
        return "Data access object found - " + text;
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return Icons.STATEMENT_LINE_MARKER_ICON;
    }

}
