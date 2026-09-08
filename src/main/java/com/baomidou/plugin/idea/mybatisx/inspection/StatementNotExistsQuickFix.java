package com.baomidou.plugin.idea.mybatisx.inspection;

import com.baomidou.plugin.idea.mybatisx.setting.config.AbstractStatementGenerator;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 * Statement 是否存在 检查
 * </p>
 *
 * @author yanglin
 * @since 2018 -07-30
 */
public class StatementNotExistsQuickFix extends GenericQuickFix {

    @SafeFieldForPreview
    private SmartPsiElementPointer<PsiMethod> method;

    /**
     * Instantiates a new Statement not exists quick fix.
     *
     * @param method the method
     */
    public StatementNotExistsQuickFix(@NotNull PsiMethod method) {
        this.method = SmartPointerManager.getInstance(method.getProject()).createSmartPsiElementPointer(method);
    }

    @NotNull
    @Override
    public String getName() {
        return "Generate statement";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        AbstractStatementGenerator.applyGenerate(method.dereference(), project);
    }

    /**
     * Gets method.
     *
     * @return the method
     */
    @Nullable
    public PsiMethod getMethod() {
        return method.dereference();
    }

    /**
     * Sets method.
     *
     * @param method the method
     */
    public void setMethod(@NotNull PsiMethod method) {
        this.method = SmartPointerManager.getInstance(method.getProject()).createSmartPsiElementPointer(method);
    }

}
