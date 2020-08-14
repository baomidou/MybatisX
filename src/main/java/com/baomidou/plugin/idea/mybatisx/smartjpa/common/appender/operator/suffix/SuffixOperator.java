package com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.operator.suffix;

import com.intellij.psi.PsiParameter;

import java.util.LinkedList;

public interface SuffixOperator {

    String getTemplateText(String fieldName, LinkedList<PsiParameter> parameters);
}
