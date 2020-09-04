package com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender;


import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.command.AppendTypeCommand;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.command.FieldAppendTypeCommand;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.MybatisXmlGenerator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.model.AppendTypeEnum;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.FieldUtil;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.SyntaxAppenderWrapper;
import com.intellij.lang.jvm.JvmModifier;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiParameter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class CustomFieldAppender implements SyntaxAppender {

    protected String tipName;

    protected String fieldName;

    protected String columnName;
    private AreaSequence areaSequence;

    @Override
    public AreaSequence getAreaSequence() {
        return areaSequence;
    }

    public CustomFieldAppender(String tipName) {
        this.tipName = tipName;
    }

    public void setAreaSequence(AreaSequence areaSequence) {
        this.areaSequence = areaSequence;
    }


    public CustomFieldAppender(TxField field, AreaSequence areaSequence) {
        this.fieldName = field.getFieldName();
        this.tipName = field.getTipName();
        this.columnName = field.getColumnName();
        this.areaSequence = areaSequence;
    }

    protected CustomFieldAppender(CustomFieldAppender customFieldAppender, AreaSequence areaSequence) {
        this.tipName = customFieldAppender.tipName;
        this.fieldName = customFieldAppender.fieldName;
        this.columnName = customFieldAppender.columnName;
        this.areaSequence = areaSequence;
    }


    public String getFieldName() {
        return this.fieldName;
    }

    @Override
    public String getText() {
        return this.tipName;
    }

    @Override
    public AppendTypeEnum getType() {
        return AppendTypeEnum.FIELD;
    }

    @Override
    public List<AppendTypeCommand> getCommand(String areaPrefix, List<SyntaxAppender> splitList) {
        return Arrays.asList(new FieldAppendTypeCommand(this));
    }

    @Override
    public boolean getCandidateAppender(LinkedList<SyntaxAppender> result) {
        SyntaxAppender lastAppender = result.peekLast();
        if (result.isEmpty() || lastAppender != null
            && lastAppender.getType() == AppendTypeEnum.JOIN) {
            result.add(this);
            return true;
        }
        return false;
    }

    private static final Logger logger = LoggerFactory.getLogger(CustomFieldAppender.class);

    @Override
    public String getTemplateText(String tableName,
                                  PsiClass entityClass,
                                  LinkedList<PsiParameter> parameters, LinkedList<SyntaxAppenderWrapper> collector, MybatisXmlGenerator mybatisXmlGenerator) {
        PsiParameter parameter = parameters.poll();
        if (parameter == null) {
            logger.info("字段参数为空, 什么也不做, fieldName: {}", fieldName);
            return "";
        }
        return columnName + " = " + JdbcTypeUtils.wrapperField(parameter.getName(), parameter.getType().getCanonicalText());
    }


    @Override
    public List<TxParameter> getMxParameter(LinkedList<SyntaxAppender> jpaStringList, PsiClass entityClass) {
        Map<String, PsiField> fieldMap = FieldUtil.getStringPsiFieldMap(entityClass);

        // 移除字段符号
        final SyntaxAppender peek = jpaStringList.poll();
        String text = peek.getText();
        //old:   text.substring(0, 1).toLowerCase() + text.substring(1)
        text = StringUtils.lowerCaseFirstChar(text);
        PsiField psiField = fieldMap.get(text);
        if (psiField == null) {
            logger.info("查找映射字段失败, text: {}", text);
            return Collections.emptyList();
        }
        return Collections.singletonList(TxParameter.createByPsiField(psiField));
    }




    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
            .append("tipName", tipName)
            .append("fieldName", fieldName)
            .append("columnName", columnName)
            .append("areaSequence", areaSequence)
            .toString();
    }

    /**
     * 啥也做不了,  只能把自己加到树里面
     *
     * @param jpaStringList
     * @param syntaxAppenderWrapper
     */
    @Override
    public void toTree(LinkedList<SyntaxAppender> jpaStringList, SyntaxAppenderWrapper syntaxAppenderWrapper) {
        syntaxAppenderWrapper.getCollector().add(new SyntaxAppenderWrapper(this));
    }

    @Override
    public boolean checkAfter(SyntaxAppender secondAppender, AreaSequence areaSequence) {
        boolean hasAreaCheck = secondAppender.getAreaSequence() == AreaSequence.AREA;
        boolean typeCheck = getType().checkAfter(secondAppender.getType());
        boolean fieldAreaCheck = getAreaSequence() == areaSequence;
        return hasAreaCheck || (typeCheck && fieldAreaCheck);
    }
}
