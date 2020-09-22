package com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.operator.suffix;

import com.intellij.psi.PsiParameter;

import java.util.LinkedList;

/**
 * The interface Suffix operator.
 */
public interface SuffixOperator {

    /**
     * Gets template text.
     *
     * @param fieldName  the field name
     * @param parameters the parameters
     * @return the template text
     */
    String getTemplateText(String fieldName, LinkedList<PsiParameter> parameters);
}
