package com.baomidou.plugin.idea.mybatisx.smartjpa.common;


import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.AreaSequence;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.command.AppendTypeCommand;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest.ConditionFieldWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.model.AppendTypeEnum;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.SyntaxAppenderWrapper;
import com.intellij.psi.PsiClass;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * 符号追加器
 */
public interface SyntaxAppender {

    /**
     * 一个字符都没有输入的时候， 这个标签在第一个
     */
    SyntaxAppender EMPTY = new SyntaxAppender() {
        @Override
        public String getText() {
            return "";
        }

        @Override
        public AppendTypeEnum getType() {
            return AppendTypeEnum.EMPTY;
        }

        @Override
        public String getTemplateText(String tableName, PsiClass entityClass, LinkedList<TxParameter> parameters, LinkedList<SyntaxAppenderWrapper> collector, ConditionFieldWrapper conditionFieldWrapper) {
            return "";
        }

        @Override
        public List<TxParameter> getMxParameter(LinkedList<SyntaxAppenderWrapper> syntaxAppenderWrapperLinkedList, PsiClass entityClass) {
            return Collections.emptyList();
        }


        @Override
        public void toTree(LinkedList<SyntaxAppender> jpaStringList, SyntaxAppenderWrapper syntaxAppenderWrapper) {
        }

        @Override
        public AreaSequence getAreaSequence() {
            return AreaSequence.AREA;
        }
    };

    /**
     * 文本
     *
     * @return text
     */
    String getText();

    /**
     * 追加的类型
     *
     * @return type
     */
    AppendTypeEnum getType();


    /**
     * Check after boolean.
     *
     * @param secondAppender the second appender
     * @param areaSequence   the area sequence
     * @return the boolean
     */
    default boolean checkAfter(final SyntaxAppender secondAppender, AreaSequence areaSequence) {
        boolean hasAreaCheck = secondAppender.getAreaSequence() == AreaSequence.AREA;
        boolean typeCheck = getType().checkAfter(secondAppender.getType());
        return hasAreaCheck || typeCheck;
    }

    /**
     * 获得要执行的命令
     *
     * @param areaPrefix the area prefix
     * @param splitList  the split list
     * @return command
     */
    default List<AppendTypeCommand> getCommand(final String areaPrefix,
                                               final List<SyntaxAppender> splitList) {
        return Collections.emptyList();
    }

    /**
     * Poll last optional.
     *
     * @param splitList the split list
     * @return the optional
     */
    default Optional<SyntaxAppender> pollLast(LinkedList<SyntaxAppender> splitList) {
        return Optional.empty();
    }

    /**
     * Check duplicate boolean.
     *
     * @param syntaxAppenders the syntax appenders
     * @return the boolean
     */
    default boolean checkDuplicate(Set<String> syntaxAppenders) {
        return true;
    }

    /**
     * Find priority.
     *
     * @param priorityQueue the priority queue
     * @param splitStr      the split str
     */
    default void findPriority(PriorityQueue<SyntaxAppender> priorityQueue, String splitStr) {
        // 后缀, 组合
        final String syntaxText = getText();
        if (syntaxText.length() > 0 && splitStr.startsWith(syntaxText)) {
            priorityQueue.add(this);
        }
    }


    /**
     * Gets candidate appender.
     *
     * @param result the result
     * @return the candidate appender
     */
    default boolean getCandidateAppender(LinkedList<SyntaxAppender> result) {
        return true;
    }

    /**
     * Gets template text.
     *
     * @param tableName             the table name
     * @param entityClass           the entity class
     * @param parameters            the parameters
     * @param collector             the collector
     * @param conditionFieldWrapper the condition field wrapper
     * @return the template text
     */
    String getTemplateText(String tableName,
                           PsiClass entityClass,
                           LinkedList<TxParameter> parameters,
                           LinkedList<SyntaxAppenderWrapper> collector, ConditionFieldWrapper conditionFieldWrapper);

    /**
     * Gets mx parameter.
     *
     * @param syntaxAppenderWrapperLinkedList the jpa string list
     * @param entityClass                     the entity class
     * @return the mx parameter
     */
    List<TxParameter> getMxParameter(LinkedList<SyntaxAppenderWrapper> syntaxAppenderWrapperLinkedList, PsiClass entityClass);

    /**
     * 转成树
     *
     * @param jpaStringList         the jpa string list
     * @param syntaxAppenderWrapper the syntax appender wrapper
     */
    void toTree(LinkedList<SyntaxAppender> jpaStringList, SyntaxAppenderWrapper syntaxAppenderWrapper);


    /**
     * Gets area sequence.
     *
     * @return the area sequence
     */
    default AreaSequence getAreaSequence() {
        return AreaSequence.UN_KNOWN;
    }

}
