package com.baomidou.plugin.idea.mybatisx.locator;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiPackage;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @author yanglin
 */
public abstract class PackageProvider {

    @NotNull
    public abstract Set<PsiPackage> getPackages(@NotNull Project project);

}