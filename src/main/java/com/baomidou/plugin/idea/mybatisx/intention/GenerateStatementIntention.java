package com.baomidou.plugin.idea.mybatisx.intention;

import com.baomidou.plugin.idea.mybatisx.setting.config.AbstractStatementGenerator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomFieldAppender;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Generate statement intention.
 *
 * @author yanglin
 */
public class GenerateStatementIntention extends GenericIntention {

    private static final Logger logger = LoggerFactory.getLogger(GenerateStatementIntention.class);

    /**
     * Instantiates a new Generate statement intention.
     */
    public GenerateStatementIntention() {
        super(GenerateStatementChooser.INSTANCE);
    }

    @NotNull
    @Override
    public String getText() {
        return "[MybatisX] Generate new statement";
    }


    @Override
    public void invoke(@NotNull final Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        try {
            AbstractStatementGenerator.applyGenerate(PsiTreeUtil.getParentOfType(element, PsiMethod.class), project);
        } catch (RuntimeException e) {
            logger.error("生成xml文件声明失败",e);
        }
    }

}
