package com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender;


import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.operator.suffix.FixedSuffixOperator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.operator.suffix.ParamAroundSuffixOperator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.operator.suffix.ParamBeforeSuffixOperator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.operator.suffix.SuffixOperator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.command.AppendTypeCommand;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.command.FieldSuffixAppendTypeService;
import com.baomidou.plugin.idea.mybatisx.smartjpa.completion.parameter.MxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.model.AppendTypeEnum;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.TreeWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiParameter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.*;

public class CustomSuffixAppender implements SyntaxAppender {

    private final String tipName;
    private SuffixOperator suffixOperator;
    private MxParameterChanger mxParameterFinder;


    private CustomSuffixAppender(String tipName) {
        this.tipName = tipName;
    }


    public static CustomSuffixAppender createByParamJoin(String tipName, String compareOperator, AreaSequence areaSequence) {
        CustomSuffixAppender customSuffixAppender = new CustomSuffixAppender(tipName);
        customSuffixAppender.suffixOperator = new ParamBeforeSuffixOperator(compareOperator);
        customSuffixAppender.areaSequence = areaSequence;
        return customSuffixAppender;
    }

    public static CustomSuffixAppender createByParamAround(String tipName, String prefix, String suffix, AreaSequence areaSequence) {
        CustomSuffixAppender customSuffixAppender = new CustomSuffixAppender(tipName);
        customSuffixAppender.suffixOperator = new ParamAroundSuffixOperator(prefix, suffix);
        customSuffixAppender.areaSequence = areaSequence;
        return customSuffixAppender;
    }

    public static CustomSuffixAppender createBySuffixOperator(String tipName, SuffixOperator suffixOperator, AreaSequence areaSequence) {
        CustomSuffixAppender customSuffixAppender = new CustomSuffixAppender(tipName);
        customSuffixAppender.suffixOperator = suffixOperator;
        customSuffixAppender.areaSequence = areaSequence;
        return customSuffixAppender;
    }


    public static CustomSuffixAppender createByParameterChanger(String tipName, MxParameterChanger mxParameterFinder, AreaSequence areaSequence) {
        CustomSuffixAppender customSuffixAppender = new CustomSuffixAppender(tipName);
        customSuffixAppender.mxParameterFinder = mxParameterFinder;
        customSuffixAppender.suffixOperator = mxParameterFinder;
        customSuffixAppender.areaSequence = areaSequence;
        return customSuffixAppender;
    }

    @Override
    public AreaSequence getAreaSequence() {
        return areaSequence;
    }

    /**
     * 根据固定后缀
     *
     * @param tipName
     * @param suffix
     * @param areaSequence
     * @return
     */
    public static SyntaxAppender createByFixed(String tipName, String suffix, AreaSequence areaSequence) {
        CustomSuffixAppender customSuffixAppender = new CustomSuffixAppender(tipName);
        customSuffixAppender.suffixOperator = new FixedSuffixOperator(suffix);
        customSuffixAppender.areaSequence = areaSequence;
        return customSuffixAppender;
    }

    AreaSequence areaSequence;


    @Override
    public String getText() {
        return this.tipName;
    }

    @Override
    public AppendTypeEnum getType() {
        return AppendTypeEnum.SUFFIX;
    }

    @Override
    public List<AppendTypeCommand> getCommand(String areaPrefix, List<SyntaxAppender> splitList) {
        return Arrays.asList(new FieldSuffixAppendTypeService(this));
    }

    @Override
    public List<MxParameter> getParameter(MxParameter mxParameter) {
        if (mxParameterFinder == null) {
            return Arrays.asList(mxParameter);
        }
        return mxParameterFinder.getParameter(mxParameter);
    }

    /**
     * 后缀后面一定不能添加任何东西了
     *
     * @param result
     * @return
     */
    @Override
    public boolean getCandidateAppender(LinkedList<SyntaxAppender> result) {
        SyntaxAppender lastAppender = result.peekLast();
        if (!result.isEmpty() &&
            lastAppender != null) {
            // && lastAppender.getType() == AppendTypeEnum.FIELD  在insert 的时候, 后缀不需要字段
            result.add(this);
            return true;
        }
        return false;
    }


    @Override
    public String getTemplateText(String tableName,
                                  PsiClass entityClass,
                                  LinkedList<PsiParameter> parameters, LinkedList<TreeWrapper<SyntaxAppender>> collector) {
        TreeWrapper<SyntaxAppender> treeWrapper = collector.get(0);
        CustomFieldAppender field = (CustomFieldAppender) treeWrapper.getAppender();
        String templateText = suffixOperator.getTemplateText(field.getFieldName(), parameters);
        return templateText;
    }

    /**
     * 对于前一个追加器是字段类型的,  就把字段弹出来, 加到自己里面
     *
     * @param jpaStringList
     * @param treeHelp
     * @param treeWrapper
     */
    @Override
    public void toTree(LinkedList<SyntaxAppender> jpaStringList, Stack<SyntaxAppender> treeHelp, TreeWrapper<SyntaxAppender> treeWrapper) {
        LinkedList<TreeWrapper<SyntaxAppender>> collector = new LinkedList<>();

        final SyntaxAppender peek = treeHelp.peek();
        if (peek.getType() == AppendTypeEnum.FIELD) {
            final SyntaxAppender pop = treeHelp.pop();

            TreeWrapper<SyntaxAppender> lastField = treeWrapper.getCollector().pop();
            collector.add(lastField);
            treeWrapper.addWrapper(new TreeWrapper<>(this,collector));
        }
        treeHelp.push(this);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("tipName", tipName)
            .append("suffixOperator", suffixOperator)
            .append("mxParameterFinder", mxParameterFinder)
            .append("areaSequence", areaSequence)
            .toString();
    }

    @Override
    public boolean checkAfter(SyntaxAppender secondAppender, AreaSequence areaSequence) {
        boolean hasAreaCheck = secondAppender.getAreaSequence() == AreaSequence.AREA;
        boolean typeCheck = getType().checkAfter(secondAppender.getType());
        boolean sequenceCheck = getAreaSequence().getSequence() == secondAppender.getAreaSequence().getSequence();
        return hasAreaCheck || (typeCheck && sequenceCheck);
    }
}
