package com.baomidou.plugin.idea.mybatisx.inspection;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import com.baomidou.plugin.idea.mybatisx.generate.AbstractStatementGenerator;

import org.jetbrains.annotations.NotNull;

/**
 * <p>
 * Statement 是否存在 检查
 * </p>
 *
 * @author yanglin
 * @since 2018-07-30
 */
public class StatementNotExistsQuickFix extends GenericQuickFix {

    private PsiMethod method;

    public StatementNotExistsQuickFix(@NotNull PsiMethod method) {
        this.method = method;
    }

    @NotNull
    @Override
    public String getName() {
        return "Generate statement";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        AbstractStatementGenerator.applyGenerate(method);
    }

    @NotNull
    public PsiMethod getMethod() {
        return method;
    }

    public void setMethod(@NotNull PsiMethod method) {
        this.method = method;
    }

}
