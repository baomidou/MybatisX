package com.baomidou.plugin.idea.mybatisx.alias;

import com.baomidou.plugin.idea.mybatisx.util.MapperUtils;
import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * The type Config package alias resolver.
 *
 * @author yanglin
 */
public class ConfigPackageAliasResolver extends PackageAliasResolver {

    /**
     * Instantiates a new Config package alias resolver.
     *
     * @param project the project
     */
    public ConfigPackageAliasResolver(Project project) {
        super(project);
    }

    @NotNull
    @Override
    public Collection<String> getPackages(@Nullable PsiElement element) {
        final ArrayList<String> result = Lists.newArrayList();
        MapperUtils.processConfiguredPackage(project, pkg -> {
            result.add(pkg.getName().getStringValue());
            return true;
        });
        return result;
    }

}
