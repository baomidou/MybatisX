package com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.changer;




import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.FieldWrapperUtils;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.MxParameterChanger;
import com.baomidou.plugin.idea.mybatisx.smartjpa.completion.parameter.MxParameter;
import com.github.hypfvieh.util.StringUtil;
import com.intellij.psi.PsiParameter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class BetweenParameterChanger implements MxParameterChanger {
    @Override
    public List<MxParameter> getParameter(MxParameter mxParameter) {
        MxParameter beginParameter = MxParameter.createByOrigin(
                "begin" + StringUtil.upperCaseFirstChar(mxParameter.getName()),
                mxParameter.getTypeText(),
                mxParameter.getCanonicalTypeText());

        MxParameter endParameter = MxParameter.createByOrigin(
                "end" + StringUtil.upperCaseFirstChar(mxParameter.getName()),
                mxParameter.getTypeText(),
                mxParameter.getCanonicalTypeText());

        return Arrays.asList(beginParameter, endParameter);
    }

    @Override
    public String getTemplateText(String fieldName, LinkedList<PsiParameter> parameters) {
        final PsiParameter begin = parameters.poll();
        final PsiParameter end = parameters.poll();
        final String beginStr = FieldWrapperUtils.wrapperField(begin.getName(), begin.getType().getCanonicalText());
        final String endStr = FieldWrapperUtils.wrapperField(end.getName(), end.getType().getCanonicalText());
        return fieldName + " " + "between" + " " + beginStr  + " and " + endStr;
    }
}
