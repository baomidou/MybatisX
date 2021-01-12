package com.baomidou.plugin.idea.mybatisx.tip.hashmark;

import com.baomidou.plugin.idea.mybatisx.annotation.Annotation;
import com.baomidou.plugin.idea.mybatisx.dom.model.GroupTwo;
import com.baomidou.plugin.idea.mybatisx.dom.model.IdDomElement;
import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.util.JavaUtils;
import com.baomidou.plugin.idea.mybatisx.util.MapperUtils;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.JavaLookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiConstantEvaluationHelper;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.util.xml.DomUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * #{}提示的内容, 不仅仅只是字段， 还包括mybatis支持的7大属性配置
 *
 * @author ls9527
 */
public class CompositeHashMarkTip {

    public static final String DOT = ".";
    private Project project;

    public CompositeHashMarkTip(Project project) {
        this.project = project;
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
        // 焦点前面的字段名称
        String fieldFrontOfCaret = wrappedText.substring(0, editorCaret);

        boolean hasHashMark = fieldFrontOfCaret.contains(",");
        if (!hasHashMark) {
            PsiParameterList parameterList = psiMethod.getParameterList();
            boolean canUseFields = parameterList.getParametersCount() == 1;
            for (PsiParameter psiParameter : parameterList.getParameters()) {
                String fieldName = findFieldNameByParam(psiParameter);

                promptFields(result, psiParameter, fieldFrontOfCaret, fieldName, canUseFields);
            }

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
        // 存在逗号`,`标记，并且已经是`=`符号
        if (hasHashMark && !tipOfSecondLevel) {
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

    /**
     * 参考
     * com.intellij.codeInsight.completion.JavaLookupElementBuilder
     * com.intellij.codeInsight.completion.JavaCompletionUtil
     * <p>
     * 提示类的全称: com.intellij.codeInsight.completion.JavaPsiClassReferenceElement (com.baomidou.mybatis3.domain.Blog)
     * 提示类的简称: com.intellij.codeInsight.lookup.PsiTypeLookupItem  (Blog)
     * 提示指定的单个属性: com.intellij.codeInsight.lookup.VariableLookupItem
     *
     * @param result
     * @param psiParameter
     * @param fieldFrontOfCaret
     * @param fieldName
     * @param canUseFields      是否能使用参数的类型的属性名称
     */
    private void promptFields(CompletionResultSet result, PsiParameter psiParameter, String fieldFrontOfCaret, String fieldName, boolean canUseFields) {
        Optional<PsiClass> clazz = JavaUtils.findClazz(psiParameter.getProject(), psiParameter.getType().getCanonicalText());
        boolean isPrimitive = true;
        // 是一个类型:
        if (clazz.isPresent()) {
            PsiClass psiClass = clazz.get();
            // Integer, String 等
            if (psiClass.getQualifiedName().startsWith("java.lang")
                || psiClass.getQualifiedName().startsWith("java.math")) {
                // do nothing
            } else {
                // 如果只有一个字段， 并且字段没有配置 @Param
                if (canUseFields && fieldName == null) {
                    for (PsiField allField : psiClass.getAllFields()) {
                        LookupElementBuilder lookupElementBuilder = JavaLookupElementBuilder.forField(allField);
                        result.addElement(lookupElementBuilder);
                    }
                    // 引用类型已经提示了, 基本类型不应该再次提示
                    isPrimitive = false;
                }

                // 如果有@Param注解, 支持二级连续提示
                if (fieldName != null) {
                    // 提示当前类型的所有字段
                    if (fieldFrontOfCaret.startsWith(fieldName + DOT)) {
                        result = result.withPrefixMatcher(fieldFrontOfCaret.substring(fieldName.length() + 1));

                        for (PsiField allField : psiClass.getAllFields()) {
                            LookupElementBuilder lookupElementBuilder = JavaLookupElementBuilder.forField(allField);
                            // 如果字段上面有注释, 把注释加到末尾
                            PsiDocComment docComment = allField.getDocComment();
                            if (docComment != null) {
                                String text = Arrays.stream(docComment.getDescriptionElements())
                                    .map(PsiElement::getText)
                                    .collect(Collectors.joining());

                                lookupElementBuilder = lookupElementBuilder.withTailText(text);
                            }
                            result.addElement(lookupElementBuilder);
                        }
                        // 引用类型已经提示了, 基本类型不应该再次提示
                        isPrimitive = false;
                    }
                }

            }

        }
        // 基本类型不用关心连续提示的问题
        if (isPrimitive && fieldName != null) {
            result.addElement(LookupElementBuilder.create(fieldName));
        }


    }


    private String findFieldNameByParam(PsiParameter psiParameter) {
        String fieldName = null;
        PsiConstantEvaluationHelper constantEvaluationHelper = JavaPsiFacade.getInstance(project).getConstantEvaluationHelper();
        PsiAnnotation annotation = psiParameter.getAnnotation(Annotation.PARAM.getQualifiedName());
        // 有注解, 优先使用注解名称
        if (annotation != null) {
            PsiAnnotationMemberValue valueAttr = annotation.findAttributeValue("value");
            fieldName = Objects.requireNonNull(constantEvaluationHelper.computeConstantExpression(valueAttr)).toString();
        }

        return fieldName;
    }


    private String findLatestText(String wrappedText, int editorCaret) {
        for (int caret = editorCaret - 1; caret >= 0; caret--) {
            if (',' == wrappedText.charAt(caret)) {
                return wrappedText.substring(caret, editorCaret);
            }
        }
        return wrappedText.substring(0, editorCaret);
    }

    public PsiElement findReference(PsiElement myElement) {
        GroupTwo domElement = DomUtil.findDomElement(myElement, GroupTwo.class);
        if (domElement != null) {
            PsiMethod psiMethod = (PsiMethod) domElement.getId().getValue();
            if (psiMethod == null) {
                return null;
            }
            PsiParameterList parameterList = psiMethod.getParameterList();
            if (parameterList.isEmpty()) {
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
