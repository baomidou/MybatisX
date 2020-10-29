package com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.operator.suffix;


import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.JdbcTypeUtils;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest.ConditionFieldWrapper;
import com.intellij.psi.PsiParameter;

import java.util.LinkedList;

/**
 * 忽略大小写
 */
public class ParamIgnoreCaseSuffixOperator implements SuffixOperator {


    @Override
    public String getTemplateText(String fieldName, LinkedList<PsiParameter> parameters, ConditionFieldWrapper conditionFieldWrapper) {

        PsiParameter parameter = parameters.poll();
        return "UPPER(" + fieldName + ")"
                + " "
                + "="
                + " "
                + "UPPER(" + JdbcTypeUtils.wrapperField(parameter.getName(), parameter.getType().getCanonicalText()) + ")";
    }


}
