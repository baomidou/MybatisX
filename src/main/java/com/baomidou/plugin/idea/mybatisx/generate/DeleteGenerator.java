package com.baomidou.plugin.idea.mybatisx.generate;

import com.intellij.psi.PsiMethod;
import com.baomidou.plugin.idea.mybatisx.dom.model.GroupTwo;
import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;

import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 */
public class DeleteGenerator extends StatementGenerator {

    public DeleteGenerator(@NotNull String... patterns) {
        super(patterns);
    }

    @NotNull
    @Override
    protected GroupTwo getTarget(@NotNull Mapper mapper, @NotNull PsiMethod method) {
        return mapper.addDelete();
    }

    @NotNull
    @Override
    public String getId() {
        return "DeleteGenerator";
    }

    @NotNull
    @Override
    public String getDisplayText() {
        return "Delete Statement";
    }

}
