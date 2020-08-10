package com.baomidou.plugin.idea.mybatisx.smartjpa.common;


import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.AreaSequence;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CompositeAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomAreaAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.command.AppendTypeCommand;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.MybatisXmlGenerator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.SyntaxAppenderWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiParameter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class BaseAppenderFactory implements SyntaxAppenderFactory {


    @Override
    public String getFactoryTemplateText(LinkedList<SyntaxAppender> jpaStringList,
                                         PsiClass entityClass,
                                         LinkedList<PsiParameter> parameters, String tableName, MybatisXmlGenerator mybatisXmlGenerator) {
        if (jpaStringList.isEmpty()) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        // 按照区域维度转换成一棵树
        CompositeAppender compositeAppender = new CompositeAppender();
        SyntaxAppenderWrapper rootSyntaxWrapper = new SyntaxAppenderWrapper(null);
        compositeAppender.toTree(jpaStringList, rootSyntaxWrapper);

        // 遍历区域, 生成字符串
        for (SyntaxAppenderWrapper syntaxAppenderWrapper : rootSyntaxWrapper.getCollector()) {
            LinkedList<SyntaxAppenderWrapper> collector = syntaxAppenderWrapper
                .getCollector();
            String templateText = syntaxAppenderWrapper.getAppender().getTemplateText(tableName,
                entityClass,
                parameters,
                collector,
                mybatisXmlGenerator);
            stringBuilder.append(templateText).append("\n");
        }

        return stringBuilder.toString();
    }


    /**
     * 在字段后面追加一波符号追加器
     * <p>
     * 例如 and + field
     * 实际上是 and +field + equals
     * 默认追加 equals
     *
     * @param syntaxAppender
     * @param current
     */
    protected void appendDefault(SyntaxAppender syntaxAppender, LinkedList<SyntaxAppender> current) {

    }

    private SyntaxAppender getLastAppendType(List<SyntaxAppender> splitList) {
        if (splitList.size() == 0) {
            return SyntaxAppender.EMPTY;
        }
        return splitList.get(splitList.size() - 1);
    }

    /**
     * @param syntaxAppender
     * @param splitList      之前所有有效的 符号追加列表
     * @return
     */
    @Override
    public Optional<String> mappingAppend(SyntaxAppender syntaxAppender, List<SyntaxAppender> splitList) {

        List<SyntaxAppender> appendTypes = new ArrayList<>();
        List<AppendTypeCommand> list = syntaxAppender.getCommand(this.getTipText(), splitList);

        for (AppendTypeCommand appendTypeCommand : list) {
            Optional<SyntaxAppender> syntaxAppenderOptional = appendTypeCommand.execute();
            if (syntaxAppenderOptional.isPresent()) {
                appendTypes.add(syntaxAppenderOptional.get());
            }
        }


        // 无效的就返回 null
        if (!isValid(splitList, appendTypes)) {
            return Optional.empty();
        }
        String resultStr = appendTypes.stream().map(x -> x.getText()).collect(Collectors.joining());
        return Optional.of(resultStr);
    }

    /**
     * @param splitList   前面已经存在数据
     * @param appendTypes 当前可选的数据
     * @return
     */
    protected boolean isValid(List<SyntaxAppender> splitList, List<SyntaxAppender> appendTypes) {
        if (appendTypes.size() == 0) {
            return false;
        }
        SyntaxAppender lastAppender = getLastAppendType(splitList);
        // 检查所有标签的区域不能超过2次
        Set<String> syntaxAppenders = new HashSet<>();
        // 检查之前的标签
        if (!checkSameArea(splitList, syntaxAppenders)) {
            return false;
        }
        // 检查当前标签
        if (!checkSameArea(appendTypes, syntaxAppenders)) {
            return false;
        }
        AreaSequence areaSequence = getAreaSequence();
        // 检查当前标签允许的方式
        for (SyntaxAppender currentAppender : appendTypes) {
            if (!lastAppender.checkAfter(currentAppender, areaSequence)) {
                return false;
            }
            lastAppender = currentAppender;
            areaSequence = currentAppender.getAreaSequence();
        }
        return true;
    }

    private boolean checkSameArea(List<SyntaxAppender> splitList, Set<String> syntaxAppenders) {
        for (SyntaxAppender syntaxAppender : splitList) {
            if (!syntaxAppender.checkDuplicate(syntaxAppenders)) {
                return false;
            }
        }
        return true;
    }


    /**
     * @param priorityQueue
     * @param existsSyntaxAppenderList 已经存在的前缀
     * @param splitStr
     */
    @Override
    public void findPriority(final PriorityQueue<SyntaxAppender> priorityQueue, LinkedList<SyntaxAppender> existsSyntaxAppenderList, final String splitStr) {
        SyntaxAppender lastSyntaxAppender = existsSyntaxAppenderList.peekLast();

        // 把自己内部的符号加入候选
        for (final SyntaxAppender syntaxAppender : getSyntaxAppenderList()) {
            if (lastSyntaxAppender == null
                || lastSyntaxAppender.checkAfter(syntaxAppender, getAreaSequence())) {
                syntaxAppender.findPriority(priorityQueue, splitStr);
            }
        }
        // 把自己加入候选
        final String factorySyntaxPrefix = getTipText();
        if (StringUtils.isNotBlank(factorySyntaxPrefix) && splitStr.startsWith(factorySyntaxPrefix)) {
            CustomAreaAppender customAreaAppender = CustomAreaAppender.createCustomAreaAppender(factorySyntaxPrefix,
                getTipText(),
                AreaSequence.AREA,
                getAreaSequence(),
                this);
            priorityQueue.add(customAreaAppender);
        }
    }

    protected abstract AreaSequence getAreaSequence();


}
