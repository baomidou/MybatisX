package com.baomidou.plugin.idea.mybatisx.contributor;

import com.baomidou.plugin.idea.mybatisx.dom.model.IdDomElement;
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
 * @author yanglin
 */
public class SqlParamCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(CompletionParameters parameters, final CompletionResultSet result) {
        if (parameters.getCompletionType() != CompletionType.BASIC) {
            return;
        }

        PsiElement position = parameters.getPosition();
        Editor editor = parameters.getEditor();
        InjectedLanguageManager injectedLanguageManager = InjectedLanguageManager.getInstance(editor.getProject());
        PsiFile topLevelFile = injectedLanguageManager.getTopLevelFile(position);
        if (DomUtils.isMybatisFile(topLevelFile)) {
            if (shouldAddElement(position.getContainingFile(), parameters.getOffset())) {
                Project project = editor.getProject();
                if(project!=null){
                    process(topLevelFile, result, position, project);
                }
            }
        }
    }

    private void process(PsiFile xmlFile, CompletionResultSet result, PsiElement position,  Project project) {
        InjectedLanguageManager injectedLanguageManager = InjectedLanguageManager.getInstance(project);

        int offset = injectedLanguageManager.injectedToHost(position, position.getTextOffset());
        Optional<IdDomElement> idDomElement = MapperUtils.findParentIdDomElement(xmlFile.findElementAt(offset));
        if (idDomElement.isPresent()) {
            // TODO 加入 jdbcType 的提示, 例如: #{age,jdbcType=NUMERIC}
            TestParamContributor.addElementForPsiParameter(position.getProject(), result, idDomElement.get());
            result.stopHere();
        }
    }

    private boolean shouldAddElement(PsiFile file, int offset) {
        String text = file.getText();
        for (int i = offset - 1; i > 0; i--) {
            char c = text.charAt(i);
            if (c == '{' && text.charAt(i - 1) == '#') return true;
        }
        return false;
    }
}
