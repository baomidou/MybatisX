package com.baomidou.plugin.idea.mybatisx.util;

import java.util.Collection;

/**
 * @author yanglin
 */
public final class CollectionUtils {

    private CollectionUtils() {
        throw new UnsupportedOperationException();
    }

    public static boolean isEmpty(Collection collection) {
        return null == collection || 0 == collection.size();
    }

    public static boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection);
    }

}
