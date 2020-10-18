package com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.operator.suffix;


import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.JdbcTypeUtils;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest.ConditionFieldWrapper;
import com.intellij.psi.PsiParameter;

import java.util.LinkedList;

/**
 * 字段比较
 */
public class ParamAroundSuffixOperator implements SuffixOperator {
    /**
     * 比较符号
     */
    private String prefix;

    private String suffix;

    /**
     * Instantiates a new Param around suffix operator.
     *
     * @param prefix the prefix
     * @param suffix the suffix
     */
    public ParamAroundSuffixOperator(final String prefix, final String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String getTemplateText(String fieldName, LinkedList<PsiParameter> parameters, ConditionFieldWrapper conditionFieldWrapper) {

        PsiParameter parameter = parameters.poll();
        return fieldName
                + " "
                + prefix
                + " "
                + JdbcTypeUtils.wrapperField(parameter.getName(), parameter.getType().getCanonicalText())
                + suffix;
    }
}
