package com.baomidou.plugin.idea.mybatisx.alias;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * The type Alias resolver factory.
 *
 * @author yanglin
 */
public class AliasResolverFactory {

    /**
     * Create inner alias resolver alias resolver.
     *
     * @param project the project
     * @return the alias resolver
     */
    @NotNull
    public static AliasResolver createInnerAliasResolver(@NotNull Project project) {
        return new InnerAliasResolver(project);
    }

    /**
     * Create annotation resolver alias resolver.
     *
     * @param project the project
     * @return the alias resolver
     */
    @NotNull
    public static AliasResolver createAnnotationResolver(@NotNull Project project) {
        return new AnnotationAliasResolver(project);
    }

    /**
     * Create bean resolver alias resolver.
     *
     * @param project the project
     * @return the alias resolver
     */
    @NotNull
    public static AliasResolver createBeanResolver(@NotNull Project project) {
        return new BeanAliasResolver(project);
    }

    /**
     * Create config package resolver alias resolver.
     *
     * @param project the project
     * @return the alias resolver
     */
    @NotNull
    public static AliasResolver createConfigPackageResolver(@NotNull Project project) {
        return new ConfigPackageAliasResolver(project);
    }

    /**
     * Create single alias resolver alias resolver.
     *
     * @param project the project
     * @return the alias resolver
     */
    @NotNull
    public static AliasResolver createSingleAliasResolver(@NotNull Project project) {
        return new SingleAliasResolver(project);
    }

    public static AliasResolver createSpringBootResolver(Project project) {
        return new SpringBootPackageResolver(project);
    }
}
