package com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.operator.suffix;


import androidx.annotation.NonNull;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.JdbcTypeUtils;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest.ConditionFieldWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxParameter;

import java.util.LinkedList;

/**
 * 忽略大小写
 */
public class ParamIgnoreCaseSuffixOperator implements SuffixOperator {
    @Override
    public String getTemplateText(String fieldName, @NonNull LinkedList<TxParameter> parameters, ConditionFieldWrapper conditionFieldWrapper) {
        TxParameter parameter = parameters.poll();
        final String templateText = "UPPER(%s) = UPPER(%s)"
            .formatted(fieldName, JdbcTypeUtils.wrapperField(parameter.getName(), parameter.getCanonicalTypeText()));
        return conditionFieldWrapper.wrapConditionText(fieldName, templateText);
    }
}
