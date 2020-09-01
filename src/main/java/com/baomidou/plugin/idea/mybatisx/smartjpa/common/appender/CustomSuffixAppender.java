package com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender;


import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.operator.suffix.FixedSuffixOperator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.operator.suffix.ParamAroundSuffixOperator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.operator.suffix.ParamBeforeSuffixOperator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.operator.suffix.SuffixOperator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.command.AppendTypeCommand;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.command.FieldSuffixAppendTypeService;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.MybatisXmlGenerator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.model.AppendTypeEnum;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.SyntaxAppenderWrapper;
import com.intellij.database.datagrid.DataGridUtil;
import com.intellij.database.model.ObjectKind;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.util.DasUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiParameter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CustomSuffixAppender implements SyntaxAppender {

    private final String tipName;
    protected SuffixOperator suffixOperator;
    private MxParameterChanger mxParameterFinder;


    protected CustomSuffixAppender(String tipName) {
        this.tipName = tipName;
    }

    protected CustomSuffixAppender(String tipName, SuffixOperator suffixOperator, AreaSequence areaSequence) {
        this.tipName = tipName;
        this.suffixOperator = suffixOperator;
        this.areaSequence = areaSequence;
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

    private AreaSequence areaSequence;


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
    public List<TxParameter> getParameter(TxParameter txParameter) {
        if (mxParameterFinder == null) {
            return Arrays.asList(txParameter);
        }
        return mxParameterFinder.getParameter(txParameter);
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

    private static final Logger logger = LoggerFactory.getLogger(CustomSuffixAppender.class);

    @Override
    public String getTemplateText(String tableName,
                                  PsiClass entityClass,
                                  LinkedList<PsiParameter> parameters, LinkedList<SyntaxAppenderWrapper> collector, MybatisXmlGenerator mybatisXmlGenerator) {
        if (collector.size() == 0) {
            logger.info("这个后缀没有参数, suffix: {}", this.getText());
        }
        SyntaxAppenderWrapper syntaxAppenderWrapper = collector.get(0);
        SyntaxAppender appender = syntaxAppenderWrapper.getAppender();
        String fieldName = null;
        // 兼容insert 语句生成,后缀没有字段的情况
        if (appender instanceof CustomFieldAppender) {
            CustomFieldAppender field = (CustomFieldAppender) appender;
            fieldName = field.getFieldName();
        }
       return suffixOperator.getTemplateText(fieldName, parameters);
    }

    /**
     * 对于前一个追加器是字段类型的,  就把字段弹出来, 加到自己里面
     *
     * @param jpaStringList
     * @param syntaxAppenderWrapper
     */
    @Override
    public void toTree(LinkedList<SyntaxAppender> jpaStringList, SyntaxAppenderWrapper syntaxAppenderWrapper) {
        LinkedList<SyntaxAppenderWrapper> collector = new LinkedList<>();

        SyntaxAppenderWrapper peek = syntaxAppenderWrapper.getCollector().peekLast();
        if (peek.getAppender().getType() == AppendTypeEnum.FIELD) {

            SyntaxAppenderWrapper lastField = syntaxAppenderWrapper.getCollector().pollLast();
            collector.add(lastField);

        }

        syntaxAppenderWrapper.addWrapper(new SyntaxAppenderWrapper(this, collector));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
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

    public SuffixOperator getSuffixOperator() {
        return suffixOperator;
    }
}
