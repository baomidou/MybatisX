package com.baomidou.plugin.idea.mybatisx.smartjpa.common;


import com.baomidou.plugin.idea.mybatisx.smartjpa.completion.parameter.MxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.TreeWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiParameter;

import java.util.*;

/**
 * 符号追加器工厂
 */
public interface SyntaxAppenderFactory {
    List<SyntaxAppender> getSyntaxAppenderList();

    default List<String> getCompletionContent(final List<SyntaxAppender> splitList) {
        final List<String> result = new ArrayList<>();
        for (final SyntaxAppender syntaxAppender : this.getSyntaxAppenderList()) {
            this.mappingAppend(syntaxAppender, splitList).ifPresent(result::add);
        }
        return result;
    }

    String getFactoryTemplateText(LinkedList<SyntaxAppender> jpaStringList,
                                  PsiClass entityClass,
                                  LinkedList<PsiParameter> parameters, String tableName);

    Optional<String> mappingAppend(SyntaxAppender syntaxAppender, List<SyntaxAppender> splitList);

    /**
     * 动态提示文本
     *
     * @return
     */
    String getTipText();


    List<MxParameter> getMxParameter(PsiClass entityClass, LinkedList<SyntaxAppender> jpaStringList);

    default String getTemplateText(String tableName,
                                   PsiClass entityClass,
                                   LinkedList<PsiParameter> parameters,
                                   LinkedList<TreeWrapper<SyntaxAppender>> collector) {
        return "";
    }

    void findPriority(PriorityQueue<SyntaxAppender> priorityQueue, LinkedList<SyntaxAppender> syntaxAppenderList, String splitStr);
}
