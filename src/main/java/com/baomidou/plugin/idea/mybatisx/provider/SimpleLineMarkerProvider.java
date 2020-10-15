package com.baomidou.plugin.idea.mybatisx.provider;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Collection;
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
        if (!isTheElement(element)) {
            return;
        }
        logger.info("getLineMarkerInfo start, element: {}", element);
        Optional<? extends T[]> processResult = apply((F) element);
        if (processResult.isPresent()) {
            T[] arrays = processResult.get();
            NavigationGutterIconBuilder navigationGutterIconBuilder = NavigationGutterIconBuilder.create(getIcon());
            if (arrays != null && arrays.length > 0) {
                navigationGutterIconBuilder.setTooltipTitle(getTooltip(arrays[0], element));
            }
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
    @Override
    @NotNull
    public abstract Icon getIcon();

    @NotNull
    public abstract String getTooltip(T array, @NotNull PsiElement target);
}
