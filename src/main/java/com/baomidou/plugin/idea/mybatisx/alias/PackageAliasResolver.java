package com.baomidou.plugin.idea.mybatisx.alias;

import com.baomidou.plugin.idea.mybatisx.util.SpringStringUtils;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.google.common.collect.Sets;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.search.GlobalSearchScope;
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

    public static final String MP3_FACTORY = "com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean";
    public static final String MP2_FACTORY = " com.baomidou.mybatisplus.spring.MybatisSqlSessionFactoryBean";
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
        boolean enableMybatisPlus = isEnableMybaitsPlus();
        Set<AliasDesc> result = Sets.newHashSet();
        for (String pkgName : getPackages(element)) {
            if (null == pkgName) {
                continue;
            }
            addPackages(enableMybatisPlus, result, pkgName);
        }
        return result;
    }

    private void addPackages(boolean enableMybatisPlus, Set<AliasDesc> result, String pkgName) {
        if (enableMybatisPlus) {
            // mybatis-plus 通配符支持
            if (pkgName.contains("*") &&
                !pkgName.contains(",")
                && !pkgName.contains(";")) {

                int firstWildcard = pkgName.indexOf(".*");
                String firstPackage = pkgName.substring(0, firstWildcard);
                JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
                PsiPackage basePackage = javaPsiFacade.findPackage(firstPackage);
                if (basePackage != null) {
                    matchPackages(pkgName, basePackage, result);
                }
            }

            String[] packages = SpringStringUtils.tokenizeToStringArray(pkgName, ",;");
            for (String string : packages) {
                addPackage(result, string);
            }

        } else {
            // 默认mybatis注册包
            addPackage(result, pkgName);
        }
    }

    private Boolean isEnableMybaitsPlus() {
        Boolean enable = null;
        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        PsiClass mp3Class = javaPsiFacade.findClass(MP3_FACTORY, scope);
        if (mp3Class != null) {
            enable = true;
        }

        if (enable != null) {
            PsiClass mp2Class = javaPsiFacade.findClass(MP2_FACTORY, scope);
            if (mp2Class != null) {
                enable = true;
            }
        }
        if (enable == null) {
            enable = false;
        }
        return enable;

    }

    private void matchPackages(String pkgName, PsiPackage basePackage, Set<AliasDesc> result) {
        for (PsiPackage subPackage : basePackage.getSubPackages()) {
            if (AntPathMatcher.match(pkgName, subPackage.getQualifiedName())) {
                addPackage(result, subPackage.getQualifiedName());
            } else {
                matchPackages(pkgName, subPackage, result);
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
