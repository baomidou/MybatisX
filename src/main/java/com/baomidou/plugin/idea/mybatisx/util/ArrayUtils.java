package com.baomidou.plugin.idea.mybatisx.util;

import com.google.common.base.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    @NotNull
    public static <T> Optional<T> getOnlyElement(@Nullable T[] target, @NotNull T defValue) {
        return Optional.fromNullable(getOnlyElement(target).or(defValue));
    }

    /**
     * Gets only element.
     *
     * @param <T>    the type parameter
     * @param target the target
     * @return the only element
     */
    @NotNull
    public static <T> Optional<T> getOnlyElement(@Nullable T[] target) {
        return (null == target || 1 != target.length) ? Optional.<T>absent() : Optional.fromNullable(target[0]);
    }

}
