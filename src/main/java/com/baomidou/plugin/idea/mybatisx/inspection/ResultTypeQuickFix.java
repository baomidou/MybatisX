package com.baomidou.plugin.idea.mybatisx.inspection;

import com.baomidou.plugin.idea.mybatisx.dom.model.Select;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

/**
 * <p>
 * ResultType 检查
 * </p>
 *
 * @author yanglin
 * @since 2018 -07-30
 */
public class ResultTypeQuickFix extends GenericQuickFix {

    private Select select;
    private PsiClass target;

    /**
     * Instantiates a new Result type quick fix.
     *
     * @param select the select
     * @param target the target
     */
    public ResultTypeQuickFix(@NotNull Select select, @NotNull PsiClass target) {
        this.select = select;
        this.target = target;
    }

    @NotNull
    @Override
    public String getName() {
        return "Correct resultType";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        GenericAttributeValue<PsiClass> resultType = select.getResultType();
        resultType.setValue(target);
    }

    /**
     * Gets target.
     *
     * @return the target
     */
    @NotNull
    public PsiClass getTarget() {
        return target;
    }

    /**
     * Sets target.
     *
     * @param target the target
     */
    public void setTarget(@NotNull PsiClass target) {
        this.target = target;
    }

    /**
     * Gets select.
     *
     * @return the select
     */
    @NotNull
    public Select getSelect() {
        return select;
    }

    /**
     * Sets select.
     *
     * @param select the select
     */
    public void setSelect(@NotNull Select select) {
        this.select = select;
    }
}
