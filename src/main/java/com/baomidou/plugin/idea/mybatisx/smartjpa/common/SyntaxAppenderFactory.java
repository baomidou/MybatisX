package com.baomidou.plugin.idea.mybatisx.smartjpa.common;


import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.MybatisXmlGenerator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.SyntaxAppenderWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiParameter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;

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
                                  LinkedList<PsiParameter> parameters, String tableName, MybatisXmlGenerator mybatisXmlGenerator);

    Optional<String> mappingAppend(SyntaxAppender syntaxAppender, List<SyntaxAppender> splitList);

    /**
     * 动态提示文本
     *
     * @return
     */
    String getTipText();


    List<TxParameter> getMxParameter(PsiClass entityClass, LinkedList<SyntaxAppender> jpaStringList);

    default String getTemplateText(String tableName,
                                   PsiClass entityClass,
                                   LinkedList<PsiParameter> parameters,
                                   LinkedList<SyntaxAppenderWrapper> collector, MybatisXmlGenerator mybatisXmlGenerator) {
        return "";
    }

    void findPriority(PriorityQueue<SyntaxAppender> priorityQueue, LinkedList<SyntaxAppender> syntaxAppenderList, String splitStr);
}
