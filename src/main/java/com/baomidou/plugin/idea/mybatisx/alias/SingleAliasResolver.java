package com.baomidou.plugin.idea.mybatisx.alias;

import com.baomidou.plugin.idea.mybatisx.util.MapperUtils;
import com.google.common.collect.Sets;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * 用于支持 mybatis-.meta.xml 下的 configuration -> typeAliases -> typeAlias
 * <p>
 * The type Single alias resolver.
 *
 * @author yanglin
 */
public class SingleAliasResolver extends AliasResolver {

    /**
     * Instantiates a new Single alias resolver.
     *
     * @param project the project
     */
    public SingleAliasResolver(Project project) {
        super(project);
    }

    @NotNull
    @Override
    public Set<AliasDesc> getClassAliasDescriptions(@Nullable PsiElement element) {
        final Set<AliasDesc> result = Sets.newHashSet();
        MapperUtils.processConfiguredTypeAliases(project, typeAlias -> {
            addAliasDesc(result, typeAlias.getType().getValue(), typeAlias.getAlias().getStringValue());
            return true;
        });
        return result;
    }

}
