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
    public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement element) {
        if (!isTheElement(element)) return null;

        Optional<T> processResult = apply((F) element);
        return processResult.isPresent() ? new LineMarkerInfo<F>(
                (F) element,
                element.getTextRange(),
                getIcon(),
                Pass.UPDATE_ALL,
                getTooltipProvider(processResult.get()),
                getNavigationHandler(processResult.get()),
                GutterIconRenderer.Alignment.CENTER
        ) : null;
    }

    private Function<F, String> getTooltipProvider(final T target) {
        return new Function<F, String>() {
            @Override
            public String fun(F from) {
                return getTooltip(from, target);
            }
        };
    }

    private GutterIconNavigationHandler<F> getNavigationHandler(final T target) {
        return new GutterIconNavigationHandler<F>() {
            @Override
            public void navigate(MouseEvent e, F from) {
                getNavigatable(from, target).navigate(true);
            }
        };
    }

    public abstract boolean isTheElement(@NotNull PsiElement element);

    public abstract Optional<T> apply(@NotNull F from);

    @NotNull
    public abstract Navigatable getNavigatable(@NotNull F from, @NotNull T target);

    @NotNull
    public abstract String getTooltip(@NotNull F from, @NotNull T target);

    @NotNull
    public abstract Icon getIcon();
}
