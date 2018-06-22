package com.baomidou.plugin.idea.mybatisx.alias;

import com.intellij.openapi.project.Project;

import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 */
public class AliasResolverFactory {

    @NotNull
    public static AliasResolver createInnerAliasResolver(@NotNull Project project) {
        return new InnerAliasResolver(project);
    }

    @NotNull
    public static AliasResolver createAnnotationResolver(@NotNull Project project) {
        return new AnnotationAliasResolver(project);
    }

    @NotNull
    public static AliasResolver createBeanResolver(@NotNull Project project) {
        return new BeanAliasResolver(project);
    }

    @NotNull
    public static AliasResolver createConfigPackageResolver(@NotNull Project project) {
        return new ConfigPackageAliasResolver(project);
    }

    @NotNull
    public static AliasResolver createSingleAliasResolver(@NotNull Project project) {
        return new SingleAliasResolver(project);
    }
}
