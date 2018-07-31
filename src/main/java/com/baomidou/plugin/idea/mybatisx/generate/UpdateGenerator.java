package com.baomidou.plugin.idea.mybatisx.generate;

import com.intellij.psi.PsiMethod;
import com.baomidou.plugin.idea.mybatisx.dom.model.GroupTwo;
import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;

import org.jetbrains.annotations.NotNull;

/**
 * <p>
 * Update 代码生成器
 * </p>
 *
 * @author yanglin jobob
 * @since 2018-07-30
 */
public class UpdateGenerator extends AbstractStatementGenerator {

    public UpdateGenerator(@NotNull String... patterns) {
        super(patterns);
    }

    @NotNull
    @Override
    protected GroupTwo getTarget(@NotNull Mapper mapper, @NotNull PsiMethod method) {
        return mapper.addUpdate();
    }

    @NotNull
    @Override
    public String getId() {
        return "UpdateGenerator";
    }

    @NotNull
    @Override
    public String getDisplayText() {
        return "Update Statement";
    }

}
