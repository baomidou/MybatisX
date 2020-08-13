package com.baomidou.plugin.idea.mybatisx.alias;

import com.baomidou.plugin.idea.mybatisx.util.JavaUtils;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

/**
 * @author yanglin
 */
public abstract class AliasResolver {

    protected Project project;

    public AliasResolver(Project project) {
        this.project = project;
    }

    protected Optional<AliasDesc> addAliasDesc(@NotNull Set<AliasDesc> descs, @Nullable PsiClass clazz, @Nullable String alias) {
        if (null == alias || !JavaUtils.isModelClazz(clazz)) {
            return Optional.empty();
        }
        AliasDesc desc = new AliasDesc();
        descs.add(desc);
        desc.setClazz(clazz);
        desc.setAlias(alias);
        return Optional.of(desc);
    }

    @NotNull
    public abstract Set<AliasDesc> getClassAliasDescriptions(@Nullable PsiElement element);

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
