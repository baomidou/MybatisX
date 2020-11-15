package com.baomidou.plugin.idea.mybatisx.locator;

import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;

/**
 * The type Locate strategy.
 *
 * @author yanglin
 */
public abstract class LocateStrategy {

    /**
     * Apply boolean.
     *
     * @param clazz the clazz
     * @return the boolean
     */
    public abstract boolean apply(@NotNull PsiClass clazz);

}
