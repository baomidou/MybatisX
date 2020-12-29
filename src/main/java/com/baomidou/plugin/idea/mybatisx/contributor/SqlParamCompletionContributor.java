package com.baomidou.plugin.idea.mybatisx.contributor;

import com.baomidou.plugin.idea.mybatisx.dom.model.IdDomElement;
import com.baomidou.plugin.idea.mybatisx.tip.hashmark.CompositeHashMarkTip;
import com.baomidou.plugin.idea.mybatisx.util.DomUtils;
import com.baomidou.plugin.idea.mybatisx.util.MapperUtils;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import java.util.Optional;


/**
 * The type Sql param completion contributor.
 *
 * @author yanglin
 */
public class SqlParamCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(CompletionParameters parameters, final CompletionResultSet result) {
        if (parameters.getCompletionType() != CompletionType.BASIC) {
            return;
        }
        PsiElement position = parameters.getOriginalPosition();
        Editor editor = parameters.getEditor();
        Project project = editor.getProject();
        if (project == null) {
            return;
        }
        InjectedLanguageManager injectedLanguageManager = InjectedLanguageManager.getInstance(project);
        PsiFile topLevelFile = injectedLanguageManager.getTopLevelFile(position);
        if (DomUtils.isMybatisFile(topLevelFile)) {
            if (shouldAddElement(position.getContainingFile(), parameters.getOffset())) {
                int editorCaret = parameters.getOffset() - position.getTextOffset();
                // 根据SQL语言的位置找到XML语言的位置， 获取当前提示的CRUD节点
                int offset = injectedLanguageManager.injectedToHost(position, position.getTextOffset());
                Optional<IdDomElement> idDomElement = MapperUtils.findParentIdDomElement(topLevelFile.findElementAt(offset));
                // 如果当前的内容在CRUD节点内
                idDomElement.ifPresent(domElement -> new CompositeHashMarkTip(position.getProject(), result, domElement)
                    .addElementForPsiParameter(position.getText(), editorCaret));
                // 如果在#{}里面输入字符, 则阻断原生SQL提示
                result.stopHere();
            }
        }

    }

    private boolean shouldAddElement(PsiFile file, int offset) {
        String text = file.getText();
        for (int i = offset - 1; i > 0; i--) {
            char c = text.charAt(i);
            if (c == '{' && text.charAt(i - 1) == '#') {
                return true;
            }
        }
        return false;
    }
}
