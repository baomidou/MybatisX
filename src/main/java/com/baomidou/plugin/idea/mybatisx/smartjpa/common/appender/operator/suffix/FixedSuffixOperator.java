package com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.operator.suffix;

import com.intellij.psi.PsiParameter;

import java.util.LinkedList;

/**
 * 固定后缀
 *
 * is null
 * is not null
 */
public class FixedSuffixOperator implements SuffixOperator{
    /**
     * 比较符号
     */
    private String operatorName;

    public FixedSuffixOperator(final String operatorName) {
        this.operatorName = operatorName;
    }

    @Override
    public String getTemplateText(String fieldName, LinkedList<PsiParameter> parameters) {

        return fieldName
                + " "
                + operatorName;
    }
}
