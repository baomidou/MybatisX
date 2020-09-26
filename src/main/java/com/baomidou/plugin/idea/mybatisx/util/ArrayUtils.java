package com.baomidou.plugin.idea.mybatisx.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * The type Array utils.
 *
 * @author yanglin
 */
public final class ArrayUtils {

    private ArrayUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets only element.
     *
     * @param <T>      the type parameter
     * @param target   the target
     * @param defValue the def value
     * @return the only element
     */
    public static <T> Optional<T> getOnlyElement(@Nullable T[] target, @NotNull T defValue) {
        return Optional.ofNullable(getOnlyElement(target).orElse(defValue));
    }

    /**
     * Gets only element.
     *
     * @param <T>    the type parameter
     * @param target the target
     * @return the only element
     */
    public static <T> Optional<T> getOnlyElement(@Nullable T[] target) {
        return (null == target || 1 != target.length) ? Optional.<T>empty() : Optional.ofNullable(target[0]);
    }

}
