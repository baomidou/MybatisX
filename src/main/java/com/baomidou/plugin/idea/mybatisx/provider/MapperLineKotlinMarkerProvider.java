package com.baomidou.plugin.idea.mybatisx.provider;

import com.baomidou.plugin.idea.mybatisx.dom.model.IdDomElement;
import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.service.JavaService;
import com.baomidou.plugin.idea.mybatisx.service.KotlinService;
import com.baomidou.plugin.idea.mybatisx.util.Icons;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.CommonProcessors;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtClass;
import org.jetbrains.kotlin.psi.KtNamedFunction;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The type Mapper line marker provider.
 *
 * @author yanglin
 */
public class MapperLineKotlinMarkerProvider extends RelatedItemLineMarkerProvider {

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {
        ElementInnerFilter filter = null;
        if (element instanceof KtClass) {
            filter = new KtClassElementInnerFilter();
        }
        if (filter == null && element instanceof KtNamedFunction) {
            filter = new KtFunctionElementInnerFilter();
        }
        if (filter != null) {
            filter.collectNavigationMarkers(element, result);
        }
    }


    /**
     * 元素内部过滤器
     */
    private abstract class ElementInnerFilter {
        protected abstract Collection<? extends DomElement> getResults(@NotNull PsiElement element);

        private void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
            final Collection<? extends DomElement> results = getResults(element);
            if (!results.isEmpty()) {
                final List<XmlTag> xmlTags = results.stream().map(DomElement::getXmlTag).collect(Collectors.toList());
                NavigationGutterIconBuilder<PsiElement> builder =
                    NavigationGutterIconBuilder.create(Icons.MAPPER_LINE_MARKER_ICON)
                        .setAlignment(GutterIconRenderer.Alignment.CENTER)
                        .setCellRenderer(new GotoMapperXmlSchemaTypeRendererProvider.MyRenderer())
                        .setTargets(xmlTags)
                        .setTooltipTitle("Navigation to target in mapper xml");
                final PsiElement targetMarkerInfo = Objects.requireNonNull(((PsiNameIdentifierOwner) element).getNameIdentifier());
                result.add(builder.createLineMarkerInfo(targetMarkerInfo));
            }
        }
    }

    /**
     * KtClass过滤器
     */
    private class KtClassElementInnerFilter extends ElementInnerFilter {

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
    private class KtFunctionElementInnerFilter extends ElementInnerFilter {

        @Override
        protected Collection<? extends DomElement> getResults(@NotNull PsiElement element) {
            CommonProcessors.CollectProcessor<IdDomElement> processor = new CommonProcessors.CollectProcessor<>();
            KotlinService.getInstance(element.getProject()).processKotlinMethod(((KtNamedFunction) element), processor);
            return processor.getResults();
        }

    }
}
