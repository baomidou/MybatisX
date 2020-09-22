package com.baomidou.plugin.idea.mybatisx.provider;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.psi.PsiElement;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * The type Marker provider adaptor.
 *
 * @author yanglin
 */
public abstract class MarkerProviderAdaptor implements LineMarkerProvider {

    @Override
    public void collectSlowLineMarkers(@NotNull List<PsiElement> elements, @NotNull Collection<LineMarkerInfo> result) {
    }

    @Nullable
    @Override
    public LineMarkerInfo<? extends PsiElement> getLineMarkerInfo(@NotNull PsiElement element) {
        return null;
    }

}
