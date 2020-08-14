package com.baomidou.plugin.idea.mybatisx.provider;

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.util.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author yanglin
 */
public abstract class SimpleLineMarkerProvider<F extends PsiElement, T> extends MarkerProviderAdaptor {

    @Override
    public void collectSlowLineMarkers(@NotNull List<PsiElement> elements, @NotNull Collection<LineMarkerInfo> result) {
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public LineMarkerInfo<? extends PsiElement> getLineMarkerInfo(@NotNull PsiElement element) {
        if (!isTheElement(element)) return null;

        Optional<? extends T> processResult = apply((F) element);
        return processResult.map(t -> new LineMarkerInfo<>(
            (F) element,
            element.getTextRange(),
            getIcon(),
            getTooltipProvider(t),
            getNavigationHandler(t),
            GutterIconRenderer.Alignment.CENTER
        )).orElse(null);
    }

    private Function<F, String> getTooltipProvider(final T target) {
        return from -> getTooltip(from, target);
    }

    private GutterIconNavigationHandler<F> getNavigationHandler(final T target) {
        return (e, from) -> getNavigatable(from, target).navigate(true);
    }

    public abstract boolean isTheElement(@NotNull PsiElement element);

    public abstract Optional<? extends T> apply(@NotNull F from);

    @NotNull
    public abstract Navigatable getNavigatable(@NotNull F from, @NotNull T target);

    @NotNull
    public abstract String getTooltip(@NotNull F from, @NotNull T target);

    @NotNull
    public abstract Icon getIcon();
}
