package com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.operator.suffix;

import com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest.ConditionFieldWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxParameter;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

/**
 * The interface Suffix operator.
 */
public interface SuffixOperator {

    /**
     * Gets template text.
     *
     * @param fieldName             the field name
     * @param parameters            the parameters
     * @param conditionFieldWrapper
     * @return the template text
     */
    String getTemplateText(String fieldName, @NotNull LinkedList<TxParameter> parameters, ConditionFieldWrapper conditionFieldWrapper);
}
