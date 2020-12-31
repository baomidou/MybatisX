package com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.operator.suffix;


import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.JdbcTypeUtils;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest.ConditionFieldWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxParameter;

import java.util.LinkedList;

/**
 * 字段比较
 */
public class ParamBeforeSuffixOperator implements SuffixOperator {
    /**
     * 比较符号
     */
    private String operatorName;

    /**
     * Instantiates a new Param before suffix operator.
     *
     * @param operatorName the operator name
     */
    public ParamBeforeSuffixOperator(final String operatorName) {
        this.operatorName = operatorName;
    }

    @Override
    public String getTemplateText(String columnName, LinkedList<TxParameter> parameters, ConditionFieldWrapper conditionFieldWrapper) {

        TxParameter parameter = parameters.poll();
        return columnName
            + " "
            + operatorName
            + " "
            + JdbcTypeUtils.wrapperField(parameter.getName(), parameter.getCanonicalTypeText());
    }
}
