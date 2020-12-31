package com.baomidou.plugin.idea.mybatisx.util;

import com.intellij.psi.util.ReferenceSetBase;

/**
 * The type Mybatis constants.
 *
 * @author yanglin
 */
public final class MybatisConstants {

    /**
     * The constant DOT_SEPARATOR.
     */
    public static final String DOT_SEPARATOR = String.valueOf(ReferenceSetBase.DOT_SEPARATOR);
    /**
     * The constant PRIORITY.
     */
    public static final double PRIORITY = 400.0;

    private MybatisConstants() {
        throw new UnsupportedOperationException();
    }

}
