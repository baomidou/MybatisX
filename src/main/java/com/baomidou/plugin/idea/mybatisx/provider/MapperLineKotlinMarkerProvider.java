package com.baomidou.plugin.idea.mybatisx.provider;

import com.baomidou.plugin.idea.mybatisx.dom.model.IdDomElement;
import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.provider.filter.AbstractElementFilter;
import com.baomidou.plugin.idea.mybatisx.provider.filter.EmptyAbstractElementFilter;
import com.baomidou.plugin.idea.mybatisx.service.KotlinService;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.psi.PsiElement;
import com.intellij.util.CommonProcessors;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtClass;
import org.jetbrains.kotlin.psi.KtNamedFunction;

import java.util.Collection;

/**
 * The type Mapper line marker provider.
 *
 * @author yanglin
 */
public class MapperLineKotlinMarkerProvider extends RelatedItemLineMarkerProvider {

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        AbstractElementFilter filter = getElementInnerFilter(element);
        filter.collectNavigationMarkers(element, result);
    }

    @NotNull
    private AbstractElementFilter getElementInnerFilter(@NotNull PsiElement element) {
        AbstractElementFilter filter = null;
        if (element instanceof KtClass) {
            filter = new KtClassAbstractElementFilter();
        }
        if (filter == null && element instanceof KtNamedFunction) {
            filter = new KtFunctionAbstractElementFilter();
        }
        if (filter == null) {
            filter = new EmptyAbstractElementFilter();
        }
        return filter;
    }


    /**
     * KtClass过滤器
     */
    private class KtClassAbstractElementFilter extends AbstractElementFilter {

        @Override
        protected Collection<? extends DomElement> getResults(@NotNull PsiElement element) {
            // 可跳转的节点加入跳转标识
            CommonProcessors.CollectProcessor<Mapper> processor = new CommonProcessors.CollectProcessor<>();
            KotlinService.getInstance(element.getProject()).processKotlinClass((KtClass) element, processor);
            return processor.getResults();
        }

    }

    /**
     * KtNamedFunction 过滤器
     */
    private class KtFunctionAbstractElementFilter extends AbstractElementFilter {

        @Override
        protected Collection<? extends DomElement> getResults(@NotNull PsiElement element) {
            CommonProcessors.CollectProcessor<IdDomElement> processor = new CommonProcessors.CollectProcessor<>();
            KotlinService.getInstance(element.getProject()).processKotlinMethod(((KtNamedFunction) element), processor);
            return processor.getResults();
        }

    }
}
