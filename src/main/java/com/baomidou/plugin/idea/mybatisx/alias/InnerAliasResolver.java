package com.baomidou.plugin.idea.mybatisx.alias;

import com.google.common.collect.ImmutableSet;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.baomidou.plugin.idea.mybatisx.util.JavaUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * @author yanglin
 */
public class InnerAliasResolver extends AliasResolver {

    private final Set<AliasDesc> innerAliasDescs = ImmutableSet.of(
            AliasDesc.create(JavaUtils.findClazz(project, "java.lang.String").get(), "string"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.lang.Byte").get(), "byte"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.lang.Long").get(), "long"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.lang.Short").get(), "short"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.lang.Integer").get(), "int"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.lang.Integer").get(), "integer"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.lang.Double").get(), "double"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.lang.Float").get(), "float"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.lang.Boolean").get(), "boolean"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.util.Date").get(), "date"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.math.BigDecimal").get(), "decimal"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.lang.Object").get(), "object"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.util.Map").get(), "map"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.util.HashMap").get(), "hashmap"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.util.List").get(), "list"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.util.ArrayList").get(), "arraylist"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.util.Collection").get(), "collection"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.util.Iterator").get(), "iterator")
    );

    public InnerAliasResolver(Project project) {
        super(project);
    }

    @NotNull
    @Override
    public Set<AliasDesc> getClassAliasDescriptions(@Nullable PsiElement element) {
        return innerAliasDescs;
    }

}
