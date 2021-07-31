package com.baomidou.plugin.idea.mybatisx.provider;

import com.baomidou.plugin.idea.mybatisx.dom.model.IdDomElement;
import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.provider.filter.AbstractElementFilter;
import com.baomidou.plugin.idea.mybatisx.provider.filter.EmptyAbstractElementFilter;
import com.baomidou.plugin.idea.mybatisx.service.JavaService;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.util.CommonProcessors;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * The type Mapper line marker provider.
 *
 * @author yanglin
 */
public class MapperLineMarkerProvider extends RelatedItemLineMarkerProvider {

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        AbstractElementFilter filter = determineElementFilter(element);
        filter.collectNavigationMarkers(element, result);
    }

    @NotNull
    private AbstractElementFilter determineElementFilter(@NotNull PsiElement element) {
        AbstractElementFilter filter = null;
        if (element instanceof PsiClass) {
            filter = new PsiClassAbstractElementFilter();
        }
        if (filter == null && element instanceof PsiMethod) {
            filter = new PsiMethodAbstractElementFilter();
        }
        if (filter == null) {
            filter = new EmptyAbstractElementFilter();
        }
        return filter;
    }



    /**
     * PsiClass过滤器
     */
    private class PsiClassAbstractElementFilter extends AbstractElementFilter {

        @Override
        protected Collection<? extends DomElement> getResults(@NotNull PsiElement element) {
            // 可跳转的节点加入跳转标识
            CommonProcessors.CollectProcessor<Mapper> processor = new CommonProcessors.CollectProcessor<>();
            JavaService.getInstance(element.getProject()).processClass((PsiClass) element, processor);
            return processor.getResults();
        }

    }

    /**
     * PsiMethod 过滤器
     */
    private class PsiMethodAbstractElementFilter extends AbstractElementFilter {

        @Override
        protected Collection<? extends DomElement> getResults(@NotNull PsiElement element) {
            CommonProcessors.CollectProcessor<IdDomElement> processor = new CommonProcessors.CollectProcessor<>();
            JavaService.getInstance(element.getProject()).processMethod(((PsiMethod) element), processor);
            return processor.getResults();
        }

    }
}
