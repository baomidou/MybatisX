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
import com.google.common.collect.ImmutableSet;
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

    private static final ImmutableSet<String> TARGET_TYPES = ImmutableSet.of(
        Mapper.class.getSimpleName().toLowerCase(),
        Select.class.getSimpleName().toLowerCase(),
        Insert.class.getSimpleName().toLowerCase(),
        Update.class.getSimpleName().toLowerCase(),
        Delete.class.getSimpleName().toLowerCase()
    );

    @Override
    public boolean isTheElement(@NotNull PsiElement element) {
        return element instanceof XmlTag
            && isTargetType(((XmlTag)element).getName())
            && MapperUtils.isElementWithinMybatisFile(element);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<? extends PsiElement> apply(@NotNull XmlTag from) {
        DomElement domElement = DomUtil.getDomElement(from);
        if (null == domElement) {
            return Optional.empty();
        }
        // 方法
        else if (domElement instanceof IdDomElement) {
            logger.info("method 查找方法开始");
            Optional<PsiMethod> method = JavaUtils.findMethod(from.getProject(), (IdDomElement) domElement);
            logger.info("method  查找方法结束, {}", method.orElse(null));
            return method;
        } else {
            logger.info("clazz 查找类开始");
            XmlTag xmlTag = domElement.getXmlTag();
            if (xmlTag == null) {
                return Optional.empty();
            }
            String namespace = xmlTag.getAttributeValue("namespace");
            Optional<PsiClass> clazz = JavaUtils.findClazz(from.getProject(), namespace);
            logger.info("clazz 查找类结束, {}",clazz.orElse(null));
            return clazz;
        }
    }

    private boolean isTargetType(@NotNull String name) {
        return TARGET_TYPES.contains(name);
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
                text = containingClass.getQualifiedName() + "#" +psiMethod.getName();
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
