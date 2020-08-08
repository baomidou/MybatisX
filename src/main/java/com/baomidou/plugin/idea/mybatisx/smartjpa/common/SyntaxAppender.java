package com.baomidou.plugin.idea.mybatisx.smartjpa.common;





import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.AreaSequence;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.command.AppendTypeCommand;
import com.baomidou.plugin.idea.mybatisx.smartjpa.completion.parameter.MxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.model.AppendTypeEnum;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.TreeWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiParameter;

import java.util.*;

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
        public String getTemplateText(String tableName, PsiClass entityClass, LinkedList<PsiParameter> parameters, LinkedList<TreeWrapper<SyntaxAppender>> collector) {
            return "";
        }


        @Override
        public void toTree(LinkedList<SyntaxAppender> jpaStringList, Stack<SyntaxAppender> treeHelp, TreeWrapper<SyntaxAppender> treeWrapper) {
        }

        @Override
        public AreaSequence getAreaSequence() {
            return AreaSequence.AREA;
        }
    };

    /**
     * 文本
     *
     * @return
     */
    String getText();

    /**
     * 追加的类型
     *
     * @return
     */
    AppendTypeEnum getType();


    default boolean checkAfter(final SyntaxAppender secondAppender, AreaSequence areaSequence) {
        boolean hasAreaCheck = secondAppender.getAreaSequence() == AreaSequence.AREA;
        boolean typeCheck = getType().checkAfter(secondAppender.getType());
        return hasAreaCheck || typeCheck ;
    }

    /**
     * 获得要执行的命令
     *
     * @param areaPrefix
     * @param splitList
     * @return
     */
    default List<AppendTypeCommand> getCommand(final String areaPrefix,
                                               final List<SyntaxAppender> splitList) {
        return Collections.emptyList();
    }

    default Optional<SyntaxAppender> pollLast(LinkedList<SyntaxAppender> splitList) {
        return Optional.empty();
    }

    default boolean checkDuplicate(Set<String> syntaxAppenders) {
        return true;
    }

    default void findPriority(PriorityQueue<SyntaxAppender> priorityQueue, String splitStr) {
        // 后缀, 组合
        final String syntaxText = getText();
        if (syntaxText.length() > 0 && splitStr.startsWith(syntaxText)) {
            priorityQueue.add(this);
        }
    }

    default List<MxParameter> getParameter(MxParameter mxParameter) {
        return Arrays.asList(mxParameter);
    }


    default boolean getCandidateAppender(LinkedList<SyntaxAppender> result) {
        return true;
    }

    String getTemplateText(String tableName,
                           PsiClass entityClass,
                           LinkedList<PsiParameter> parameters,
                           LinkedList<TreeWrapper<SyntaxAppender>> collector);

    default List<MxParameter> getMxParameter(LinkedList<SyntaxAppender> jpaStringList, PsiClass entityClass) {
        jpaStringList.poll();
        return Collections.emptyList();
    }

    /**
     * 转成树
     * @param jpaStringList
     * @param treeHelp
     * @param treeWrapper
     */
    void toTree(LinkedList<SyntaxAppender> jpaStringList, Stack<SyntaxAppender> treeHelp, TreeWrapper<SyntaxAppender> treeWrapper);


    default AreaSequence getAreaSequence() {
        return AreaSequence.UN_KNOWN;
    }

}
