package com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.changer;


import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.MxParameterChanger;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest.ConditionFieldWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxParameter;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * The type Between parameter changer.
 */
public class BetweenParameterChanger implements MxParameterChanger {

    /**
     * The constant SPACE.
     */
    public static final String SPACE = " ";

    @Override
    public List<TxParameter> getParameter(TxParameter txParameter) {
        TxParameter beginParameter = TxParameter.createByOrigin(
            "begin" + StringUtils.upperCaseFirstChar(txParameter.getName()),
            txParameter.getTypeText(),
            txParameter.getCanonicalTypeText());

        TxParameter endParameter = TxParameter.createByOrigin(
            "end" + StringUtils.upperCaseFirstChar(txParameter.getName()),
            txParameter.getTypeText(),
            txParameter.getCanonicalTypeText());

        return Arrays.asList(beginParameter, endParameter);
    }

    @Override
    public String getTemplateText(String fieldName, LinkedList<TxParameter> parameters, ConditionFieldWrapper conditionFieldWrapper) {
        final TxParameter begin = parameters.poll();
        final TxParameter end = parameters.poll();
        assert begin != null;
        assert end != null;
        final String beginStr = conditionFieldWrapper.wrapperField(fieldName, begin.getName(), begin.getCanonicalTypeText());
        final String endStr = conditionFieldWrapper.wrapperField(fieldName, end.getName(), end.getCanonicalTypeText());

        return fieldName + SPACE + "between" + SPACE + beginStr + " and " + endStr;
    }
}
