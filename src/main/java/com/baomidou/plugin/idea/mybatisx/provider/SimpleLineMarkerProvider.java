package com.baomidou.plugin.idea.mybatisx.provider;

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * The type Simple line marker provider.
 *
 * @param <F> the type parameter
 * @param <T> the type parameter
 * @author yanglin
 */
public abstract class SimpleLineMarkerProvider<F extends PsiElement, T> extends RelatedItemLineMarkerProvider {


    private static final Logger logger = LoggerFactory.getLogger(SimpleLineMarkerProvider.class);

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {
        if (!isTheElement(element))
            return;
        logger.info("getLineMarkerInfo start, element: {}", element);
        Optional<? extends T[]> processResult = apply((F) element);
        if (processResult.isPresent()) {
            T[] arrays = processResult.get();
            NavigationGutterIconBuilder navigationGutterIconBuilder = NavigationGutterIconBuilder.create(getIcon());
            navigationGutterIconBuilder.setTargets(arrays);
            RelatedItemLineMarkerInfo<PsiElement> lineMarkerInfo = navigationGutterIconBuilder.createLineMarkerInfo(element);
            result.add(lineMarkerInfo);
        }

        logger.info("getLineMarkerInfo end");
    }


    /**
     * Is the element boolean.
     *
     * @param element the element
     * @return the boolean
     */
    public abstract boolean isTheElement(@NotNull PsiElement element);

    /**
     * Apply optional.
     *
     * @param from the from
     * @return the optional
     */
    public abstract Optional<? extends T[]> apply(@NotNull F from);


    /**
     * Gets icon.
     *
     * @return the icon
     */
    @NotNull
    public abstract Icon getIcon();
}
