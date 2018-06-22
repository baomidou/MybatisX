package com.baomidou.plugin.idea.mybatisx.util;

import com.google.common.base.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author yanglin
 */
public final class ArrayUtils {

    private ArrayUtils() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public static <T> Optional<T> getOnlyElement(@Nullable T[] target, @NotNull T defValue) {
        return Optional.fromNullable(getOnlyElement(target).or(defValue));
    }

    @NotNull
    public static <T> Optional<T> getOnlyElement(@Nullable T[] target) {
        return (null == target || 1 != target.length) ? Optional.<T>absent() : Optional.fromNullable(target[0]);
    }

}
