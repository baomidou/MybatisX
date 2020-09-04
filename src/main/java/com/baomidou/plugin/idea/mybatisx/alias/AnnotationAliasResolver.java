package com.baomidou.plugin.idea.mybatisx.alias;

import com.baomidou.plugin.idea.mybatisx.annotation.Annotation;
import com.baomidou.plugin.idea.mybatisx.util.JavaUtils;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.AnnotatedElementsSearch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author yanglin
 */
public class AnnotationAliasResolver extends AliasResolver {

    public AnnotationAliasResolver(Project project) {
        super(project);
    }

    public static final AnnotationAliasResolver getInstance(@NotNull Project project) {
        return project.getComponent(AnnotationAliasResolver.class);
    }

    @NotNull
    @Override
    public Set<AliasDesc> getClassAliasDescriptions(@Nullable PsiElement element) {
        Optional<PsiClass> clazz = Annotation.ALIAS.toPsiClass(project);
        if (clazz.isPresent()) {
            Collection<PsiClass> res = AnnotatedElementsSearch.searchPsiClasses(clazz.get(), GlobalSearchScope.allScope(project)).findAll();
            return res.stream().map(psiClass-> {
                Optional<String> txt = JavaUtils.getAnnotationValueText(psiClass, Annotation.ALIAS);
                if (!txt.isPresent()){
                    return null;
                }
                AliasDesc ad = new AliasDesc();
                ad.setAlias(txt.get());
                ad.setClazz(psiClass);
                return ad;
            }).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

}
