package com.baomidou.plugin.idea.mybatisx.alias;


import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * The type Alias facade.
 *
 * @author yanglin
 */
public class AliasFacade {

    private Project project;

    private JavaPsiFacade javaPsiFacade;

    private List<AliasResolver> resolvers;

    /**
     * Gets instance.
     *
     * @param project the project
     * @return the instance
     */
    public static final AliasFacade getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, AliasFacade.class);
    }

    /**
     * Instantiates a new Alias facade.
     *
     * @param project the project
     */
    public AliasFacade(Project project) {
        this.project = project;
        this.resolvers = new ArrayList<>();
        this.javaPsiFacade = JavaPsiFacade.getInstance(project);
        initResolvers();
    }

    private void initResolvers() {
        try {
            Class.forName("com.intellij.spring.model.utils.SpringModelUtils");
            this.registerResolver(AliasResolverFactory.createBeanResolver(project));
        } catch (ClassNotFoundException e) {
        }
        this.registerResolver(AliasResolverFactory.createSingleAliasResolver(project));
        this.registerResolver(AliasResolverFactory.createConfigPackageResolver(project));
        this.registerResolver(AliasResolverFactory.createAnnotationResolver(project));
        this.registerResolver(AliasResolverFactory.createInnerAliasResolver(project));
    }

    /**
     * Find psi class optional.
     *
     * @param element   the element
     * @param shortName the short name
     * @return the optional
     */
    public Optional<PsiClass> findPsiClass(@Nullable PsiElement element, @NotNull String shortName) {
        PsiClass clazz = javaPsiFacade.findClass(shortName, GlobalSearchScope.allScope(project));
        if (null != clazz) {
            return Optional.of(clazz);
        }
        for (AliasResolver resolver : resolvers) {
            for (AliasDesc desc : resolver.getClassAliasDescriptions(element)) {
                if (desc.getAlias().equals(shortName)) {
                    return Optional.of(desc.getClazz());
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Gets alias descs.
     *
     * @param element the element
     * @return the alias descs
     */
    @NotNull
    public Collection<AliasDesc> getAliasDescs(@Nullable PsiElement element) {
        ArrayList<AliasDesc> result = new ArrayList<>();
        for (AliasResolver resolver : resolvers) {
            result.addAll(resolver.getClassAliasDescriptions(element));
        }
        return result;
    }

    /**
     * Find alias desc optional.
     *
     * @param clazz the clazz
     * @return the optional
     */
    public Optional<AliasDesc> findAliasDesc(@Nullable PsiClass clazz) {
        if (null == clazz) {
            return Optional.empty();
        }
        for (AliasResolver resolver : resolvers) {
            for (AliasDesc desc : resolver.getClassAliasDescriptions(clazz)) {
                if (desc.getClazz().equals(clazz)) {
                    return Optional.of(desc);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Register resolver.
     *
     * @param resolver the resolver
     */
    public void registerResolver(@NotNull AliasResolver resolver) {
        this.resolvers.add(resolver);
    }

}
