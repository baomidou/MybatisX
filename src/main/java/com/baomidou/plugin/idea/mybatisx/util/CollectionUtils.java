package com.baomidou.plugin.idea.mybatisx.util;

import java.util.Collection;

/**
 * The type Collection utils.
 *
 * @author yanglin
 */
public final class CollectionUtils {

    private CollectionUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Is empty boolean.
     *
     * @param collection the collection
     * @return the boolean
     */
    public static boolean isEmpty(Collection collection) {
        return null == collection || 0 == collection.size();
    }

    /**
     * Is not empty boolean.
     *
     * @param collection the collection
     * @return the boolean
     */
    public static boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection);
    }

}
