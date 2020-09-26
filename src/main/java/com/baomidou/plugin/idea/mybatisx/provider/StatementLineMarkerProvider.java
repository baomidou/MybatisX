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
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.google.common.collect.ImmutableSet;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.List;
import java.util.Optional;

/**
 * The type Statement line marker provider.
 *
 * @author yanglin
 */
public class StatementLineMarkerProvider extends SimpleLineMarkerProvider<XmlTag, PsiElement> {

    private static final Logger logger = LoggerFactory.getLogger(SimpleLineMarkerProvider.class);

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
            && isTargetType(((XmlTag) element).getName())
            && MapperUtils.isElementWithinMybatisFile(element);
    }

    @SuppressWarnings("unchecked")
    public Optional<? extends PsiElement[]> apply(@NotNull XmlTag from) {
        DomElement domElement = DomUtil.getDomElement(from);
        if (null == domElement) {
            return Optional.empty();
        }
        // 方法
        else if (domElement instanceof IdDomElement) {
            return JavaUtils.findMethods(from.getProject(),
                MapperUtils.getNamespace(domElement),
                MapperUtils.getId((IdDomElement) domElement));
        } else {
            XmlTag xmlTag = domElement.getXmlTag();
            if (xmlTag == null) {
                return Optional.empty();
            }
            String namespace = xmlTag.getAttributeValue("namespace");
            if (StringUtils.isEmpty(namespace)) {
                return Optional.empty();
            }
            return JavaUtils.findClasses(from.getProject(), namespace);
        }
    }

    private boolean isTargetType(@NotNull String name) {
        return TARGET_TYPES.contains(name);
    }


    @Override
    public @Nullable("null means disabled") @Nls(
        capitalization = Nls.Capitalization.Sentence
    ) String getName() {
        return "statement line marker";
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return Icons.STATEMENT_LINE_MARKER_ICON;
    }

}
