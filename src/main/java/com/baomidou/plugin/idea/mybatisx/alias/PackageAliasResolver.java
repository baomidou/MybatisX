package com.baomidou.plugin.idea.mybatisx.alias;

import com.baomidou.plugin.idea.mybatisx.util.SpringStringUtils;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.google.common.collect.Sets;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPackage;
import com.intellij.spring.model.utils.AntPathMatcher;
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
            addPackages(result, pkgName);
        }
        return result;
    }

    /**
     *
     * @param result
     * @param pkgNameCandidate  可能具有通配符的包名
     */
    private void addPackages(Set<AliasDesc> result, String pkgNameCandidate) {
        // mybatis 通配符支持
        String[] packages = SpringStringUtils.tokenizeToStringArray(pkgNameCandidate, ",; \t\n");
        for (String packageName : packages) {
            String firstPackage = findPackageName(pkgNameCandidate, packageName);
            JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
            PsiPackage basePackage = javaPsiFacade.findPackage(firstPackage);
            if (basePackage != null) {
                if (AntPathMatcher.match(pkgNameCandidate, basePackage.getQualifiedName())) {
                    addPackage(result, basePackage.getQualifiedName());
                }
                matchSubPackages(pkgNameCandidate, basePackage, result);
            }
        }


    }

    @NotNull
    private String findPackageName(String pkgName, String packageName) {
        String firstPackage = null;
        int firstWildcard = packageName.indexOf(".*");
        if (firstWildcard > -1) {
            firstPackage = pkgName.substring(0, firstWildcard);
        }
        if (firstPackage == null) {
            firstPackage = packageName;
        }
        return firstPackage;
    }

    /**
     * 匹配子包
     * @param pkgName
     * @param basePackage
     * @param result
     */
    private void matchSubPackages(String pkgName, PsiPackage basePackage, Set<AliasDesc> result) {
        for (PsiPackage subPackage : basePackage.getSubPackages()) {
            if (AntPathMatcher.match(pkgName, subPackage.getQualifiedName())) {
                addPackage(result, subPackage.getQualifiedName());
            } else {
                matchSubPackages(pkgName, subPackage, result);
            }
        }
    }


    private void addPackage(Set<AliasDesc> result, String pkgName) {
        PsiPackage pkg = javaPsiFacade.findPackage(pkgName);
        if (null != pkg) {
            addAliasDesc(result, pkg);
            for (PsiPackage tmp : pkg.getSubPackages()) {
                addAliasDesc(result, tmp);
            }
        }
    }

    private void addAliasDesc(Set<AliasDesc> result, PsiPackage pkg) {
        for (PsiClass clazz : pkg.getClasses()) {
            String aliasName = StringUtils.lowerCaseFirstChar(clazz.getName());
            addAliasDesc(result, clazz, aliasName);
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
