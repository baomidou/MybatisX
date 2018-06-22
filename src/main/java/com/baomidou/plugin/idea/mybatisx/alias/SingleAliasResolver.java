package com.baomidou.plugin.idea.mybatisx.alias;

import com.google.common.collect.Sets;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.Processor;
import com.baomidou.plugin.idea.mybatisx.dom.model.TypeAlias;
import com.baomidou.plugin.idea.mybatisx.util.MapperUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * @author yanglin
 */
public class SingleAliasResolver extends AliasResolver {

    public SingleAliasResolver(Project project) {
        super(project);
    }

    @NotNull
    @Override
    public Set<AliasDesc> getClassAliasDescriptions(@Nullable PsiElement element) {
        final Set<AliasDesc> result = Sets.newHashSet();
        MapperUtils.processConfiguredTypeAliases(project, new Processor<TypeAlias>() {
            @Override
            public boolean process(TypeAlias typeAlias) {
                addAliasDesc(result, typeAlias.getType().getValue(), typeAlias.getAlias().getStringValue());
                return true;
            }
        });
        return result;
    }

}
