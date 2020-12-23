package com.baomidou.plugin.idea.mybatisx.tip.hashmark;

import com.baomidou.plugin.idea.mybatisx.annotation.Annotation;
import com.baomidou.plugin.idea.mybatisx.dom.model.IdDomElement;
import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.util.*;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * #提示的内容, 不仅仅只是字段， 还包括mybatis支持的7大属性配置
 *
 * @author ls9527
 */
public class CompositeHashMarkTip {

    private Project project;
    private CompletionResultSet result;
    private IdDomElement element;

    public CompositeHashMarkTip(Project project, CompletionResultSet result, IdDomElement element) {
        this.project = project;
        this.result = result;
        this.element = element;
    }

    private static List<HashMarkTip> hashMarkTips = new ArrayList<HashMarkTip>() {
        {
            add(new JdbcTypeHashMarkTip());
            add(new TypeHandlerHashMarkTip());
            add(new JavaTypeHashMark());
            add(new ResultMapHashMarkTip());
            add(new ModeHashMarkTip());
            add(new NumericScaleHashMarkTip());
            add(new JdbcTypeNameHashMarkTip());
        }
    };

    private static final Logger logger = LoggerFactory.getLogger(CompositeHashMarkTip.class);

    /**
     * Add element for psi parameter.
     * <p>
     * 提示的内容参考:
     * see https://mybatis.org/mybatis-3/zh/sqlmap-xml.html#select
     *
     * @param wrappedText
     * @param editorCaret
     */
    public void addElementForPsiParameter(String wrappedText,
                                          int editorCaret) {
        Optional<PsiMethod> methodOptional = JavaUtils.findMethod(project, element);
        if (!methodOptional.isPresent()) {
            logger.info("the psiMethod is null");
            return;
        }
        PsiMethod psiMethod = methodOptional.get();
        logger.info("CompletionContributor xml start");
        // tip fields
        Optional<PsiParameter> parameter = findParameter(psiMethod, wrappedText);
        boolean foundProperty = false;
        if (parameter.isPresent()) {
            PsiParameter psiParameter = parameter.get();

            String fieldName = psiParameter.getName();
            if (fieldName == null) {
                logger.info("字段名称为空");
                return;
            }
            LookupElementBuilder fieldValueElement =
                LookupElementBuilder.create(fieldName).withIcon(Icons.PARAM_COMPLETION_ICON);
            result.addElement(fieldValueElement);

            foundProperty = wrappedText.startsWith(fieldName);
        }

        CompletionResultSet completionResultSet = result;
        // 已经提示了字段,才能提示二级类型或者一级类型
        // see mybatis : org.apache.ibatis.builder.SqlSourceBuilder#PARAMETER_PROPERTIES
        boolean tipOfSecondLevel = false;
        // tip 7 types
        String latestText = findLatestText(wrappedText, editorCaret);
        if (!StringUtils.isEmpty(latestText)) {
            Mapper mapper = MapperUtils.getMapper(element);
            for (HashMarkTip hashMarkTip : hashMarkTips) {
                String tipText = "," + hashMarkTip.getName() + "=";
                if (latestText.startsWith(tipText)) {
                    tipOfSecondLevel = true;
                    String prefixMatcher = latestText.substring(tipText.length());
                    completionResultSet = result.withPrefixMatcher(prefixMatcher);
                    hashMarkTip.tipValue(completionResultSet, mapper);
                    break;
                }

            }
        }
        // 提示了字段，并且已经是`=`符号
        if (foundProperty && !tipOfSecondLevel) {
            HashMarkTipInsertHandler hashMarkTipInsertHandler = new HashMarkTipInsertHandler();
            if (!StringUtils.isEmpty(latestText)) {
                completionResultSet = result.withPrefixMatcher(latestText);
            }
            for (HashMarkTip hashMarkTip : hashMarkTips) {
                String tipText = "," + hashMarkTip.getName() + "=";
                LookupElementBuilder element = LookupElementBuilder.create(tipText)
                    .withInsertHandler(hashMarkTipInsertHandler)
                    .withPsiElement(this.element.getXmlTag());
                completionResultSet.addElement(element);
            }
        }


        logger.info("CompletionContributor xml end");

    }

    private String findFieldNameByWrappedText(String wrappedText) {
        int firstSymbol = wrappedText.indexOf(",");
        if (firstSymbol > -1) {
            return wrappedText.substring(0, firstSymbol);
        }
        return wrappedText;
    }

    private Optional<PsiParameter> findParameter(PsiMethod psiMethod, String wrappedText) {
        @NotNull PsiParameter[] psiParameters = psiMethod.getParameterList().getParameters();
        String latestText = findFieldNameByWrappedText(wrappedText);
        for (PsiParameter parameter : psiParameters) {
            Optional<String> valueText = JavaUtils.getAnnotationValueText(parameter, Annotation.PARAM);
            if (valueText.isPresent()
                && !StringUtils.isEmpty(latestText)
                && valueText.get().startsWith(latestText)) {
                return Optional.of(parameter);
            }
        }
        return Optional.empty();
    }

    private String findLatestText(String wrappedText, int editorCaret) {
        for (int caret = editorCaret - 1; caret >= 0; caret--) {
            if (',' == wrappedText.charAt(caret)) {
                return wrappedText.substring(caret, editorCaret);
            }
        }
        return wrappedText.substring(editorCaret);
    }

}
