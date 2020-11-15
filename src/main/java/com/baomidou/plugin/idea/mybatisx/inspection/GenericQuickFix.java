package com.baomidou.plugin.idea.mybatisx.inspection;

import com.intellij.codeInspection.LocalQuickFix;
import org.jetbrains.annotations.NotNull;

/**
 * <p>
 * 通用 检查
 * </p>
 *
 * @author yanglin
 * @since 2018 -07-30
 */
public abstract class GenericQuickFix implements LocalQuickFix {

    @NotNull
    @Override
    public String getFamilyName() {
        return getName();
    }

}
