package com.baomidou.plugin.idea.mybatisx.alias;

import com.google.common.collect.Sets;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPackage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

/**
 * The type Package alias resolver.
 *
 * @author yanglin
 */
public abstract class PackageAliasResolver extends AliasResolver {

    private JavaPsiFacade javaPsiFacade;

    /**
     * Instantiates a new Package alias resolver.
     *
     * @param project the project
     */
    public PackageAliasResolver(Project project) {
        super(project);
        this.javaPsiFacade = JavaPsiFacade.getInstance(project);
    }

    @NotNull
    @Override
    public Set<AliasDesc> getClassAliasDescriptions(@Nullable PsiElement element) {
        Set<AliasDesc> result = Sets.newHashSet();
        for (String pkgName : getPackages(element)) {
            if (null == pkgName) {
                continue;
            }
            PsiPackage pkg = javaPsiFacade.findPackage(pkgName);
            if (null != pkg) {
                addAliasDesc(result, pkg);
                for (PsiPackage tmp : pkg.getSubPackages()) {
                    addAliasDesc(result, tmp);
                }
            }
        }
        return result;
    }

    private void addAliasDesc(Set<AliasDesc> result, PsiPackage pkg) {
        for (PsiClass clazz : pkg.getClasses()) {
            addAliasDesc(result, clazz, clazz.getName());
        }
    }

    /**
     * Gets packages.
     *
     * @param element the element
     * @return the packages
     */
    @NotNull
    public abstract Collection<String> getPackages(@Nullable PsiElement element);
}
