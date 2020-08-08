package com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.operator.suffix;

import com.intellij.psi.PsiParameter;

import java.util.LinkedList;

public interface SuffixOperator {
    default boolean needField() {
        return true;
    }

    String getTemplateText(String fieldName, LinkedList<PsiParameter> parameters);
}
