package com.baomidou.plugin.idea.mybatisx.locator;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiPackage;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * The type Package provider.
 *
 * @author yanglin
 */
public abstract class PackageProvider {

    /**
     * Gets packages.
     *
     * @param project the project
     * @return the packages
     */
    @NotNull
    public abstract Set<PsiPackage> getPackages(@NotNull Project project);

}
