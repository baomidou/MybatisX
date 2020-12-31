package com.baomidou.plugin.idea.mybatisx.tip.hashmark;

import com.baomidou.plugin.idea.mybatisx.annotation.Annotation;
import com.baomidou.plugin.idea.mybatisx.dom.model.GroupTwo;
import com.baomidou.plugin.idea.mybatisx.dom.model.IdDomElement;
import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.util.Icons;
import com.baomidou.plugin.idea.mybatisx.util.JavaUtils;
import com.baomidou.plugin.idea.mybatisx.util.MapperUtils;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiConstantEvaluationHelper;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.util.xml.DomUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * #{}提示的内容, 不仅仅只是字段， 还包括mybatis支持的7大属性配置
 *
 * @author ls9527
 */
public class CompositeHashMarkTip {

    private static final Logger logger = LoggerFactory.getLogger(CompositeHashMarkTip.class);
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
    private Project project;

    public CompositeHashMarkTip(Project project) {
        this.project = project;
    }

    /**
     * Add element for psi parameter.
     * <p>
     * 提示的内容参考:
     * see https://mybatis.org/mybatis-3/zh/sqlmap-xml.html#select
     *
     * @param wrappedText
     * @param editorCaret
     */
    public void addElementForPsiParameter(CompletionResultSet result,
                                          IdDomElement idDomElement,
                                          String wrappedText,
                                          int editorCaret) {
        Optional<PsiMethod> methodOptional = JavaUtils.findMethod(project, idDomElement);
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
            Mapper mapper = MapperUtils.getMapper(idDomElement);
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
                    .withPsiElement(idDomElement.getXmlTag());
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

    public PsiElement findReference(PsiElement myElement) {
        GroupTwo domElement = DomUtil.findDomElement(myElement, GroupTwo.class);
        if (domElement != null) {
            PsiMethod psiMethod = domElement.getId().getValue();
            if (psiMethod == null) {
                return null;
            }
            PsiParameterList parameterList = psiMethod.getParameterList();
            if (parameterList == null || parameterList.isEmpty()) {
                return null;
            }
            String fieldName = findFieldName(myElement);

            PsiConstantEvaluationHelper constantEvaluationHelper = JavaPsiFacade.getInstance(project).getConstantEvaluationHelper();
            for (PsiParameter psiParameter : parameterList.getParameters()) {
                PsiAnnotation annotation = psiParameter.getAnnotation(Annotation.PARAM.getQualifiedName());
                if (annotation != null) {
                    PsiAnnotationMemberValue paramAnnotationValue = annotation.findAttributeValue("value");
                    Object o = constantEvaluationHelper.computeConstantExpression(paramAnnotationValue);
                    if (o instanceof String && o.equals(fieldName)) {
                        return psiMethod;
                    }
                } else {
                    // 兼容java8开启 -parameters 参数的方式，这种方式不需要@Param参数
                    if (psiParameter.getName().equals(fieldName)) {
                        return psiMethod;
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    private String findFieldName(PsiElement myElement) {
        String text = myElement.getText();
        String latestText = null;
        int firstSplit = text.indexOf(",");
        int endIndex = text.indexOf("}");
        if (firstSplit != -1) {
            latestText = text.substring(2, firstSplit);
        }
        if (latestText == null && endIndex > -1) {
            latestText = text.substring(2, endIndex);
        }
        return latestText;
    }
}
