package com.baomidou.plugin.idea.mybatisx.provider.filter;

import com.baomidou.plugin.idea.mybatisx.provider.GotoMapperXmlSchemaTypeRendererProvider;
import com.baomidou.plugin.idea.mybatisx.util.Icons;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class AbstractElementFilter {

    protected abstract Collection<? extends DomElement> getResults(@NotNull PsiElement element);

    public void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
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
