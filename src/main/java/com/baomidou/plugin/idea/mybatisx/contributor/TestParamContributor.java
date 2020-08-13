package com.baomidou.plugin.idea.mybatisx.contributor;

import com.baomidou.plugin.idea.mybatisx.annotation.Annotation;
import com.baomidou.plugin.idea.mybatisx.dom.model.IdDomElement;
import com.baomidou.plugin.idea.mybatisx.util.Icons;
import com.baomidou.plugin.idea.mybatisx.util.JavaUtils;
import com.baomidou.plugin.idea.mybatisx.util.MapperUtils;
import com.baomidou.plugin.idea.mybatisx.util.MybatisConstants;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * @author yanglin
 */
public class TestParamContributor extends CompletionContributor {

    private static final Logger logger = LoggerFactory.getLogger(TestParamContributor.class);
    public TestParamContributor() {
        extend(CompletionType.BASIC,
                XmlPatterns.psiElement().inside(XmlPatterns.xmlAttributeValue().inside(XmlPatterns.xmlAttribute().withName("test"))),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                        PsiElement position = parameters.getPosition();
                        addElementForPsiParameter(position.getProject(), result, MapperUtils.findParentIdDomElement(position).orElse(null));
                    }
                });
    }

    public static void addElementForPsiParameter(@NotNull Project project, @NotNull CompletionResultSet result, @Nullable IdDomElement element) {
        if (null == element) {
            return;
        }
        PsiMethod psiMethod= JavaUtils.findMethod(project, element).orElse(null);
        if(null == psiMethod ) {
            logger.info("psiMethod null");
            return;

        }
        PsiParameter[] psiParameters = psiMethod.getParameterList().getParameters();

        for (PsiParameter parameter : psiParameters ) {
            Optional<String> valueText = JavaUtils.getAnnotationValueText(parameter, Annotation.PARAM);
            if (valueText.isPresent()) {
                LookupElementBuilder builder = LookupElementBuilder.create(valueText.get()).withIcon(Icons.PARAM_COMPLETION_ICON);
                result.addElement(PrioritizedLookupElement.withPriority(builder, MybatisConstants.PRIORITY));
            }
        }
    }
}
