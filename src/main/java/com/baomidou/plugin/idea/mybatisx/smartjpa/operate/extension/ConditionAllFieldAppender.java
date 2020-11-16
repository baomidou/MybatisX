package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.extension;

import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.AreaSequence;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomFieldAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomJoinAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomSuffixAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest.ConditionFieldWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.SyntaxAppenderWrapper;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * 例如: updateFieldAByAllFields
 * 支持条件区域直接写 AllFields 方法生成代码，然后选择其中一部分
 */
public class ConditionAllFieldAppender extends CustomFieldAppender {
    private List<TxField> mappingField;

    public ConditionAllFieldAppender(TxField field, AreaSequence areaSequence, List<TxField> mappingField) {
        super(field, areaSequence);
        this.mappingField = mappingField;
    }

    @Override
    public String getTemplateText(String tableName,
                                  PsiClass entityClass,
                                  LinkedList<TxParameter> parameters,
                                  LinkedList<SyntaxAppenderWrapper> collector,
                                  ConditionFieldWrapper conditionFieldWrapper) {
        StringJoiner stringJoiner = new StringJoiner("\n");
        // equals 后缀追加器
        CustomSuffixAppender equalsAppender = CustomSuffixAppender.createByParamJoin("Equals", "=", AreaSequence.CONDITION);
        for (TxField txField : mappingField) {
            LinkedList<TxParameter> currentFieldParameters = new LinkedList<>();
            currentFieldParameters.add(TxParameter.createByOrigin(txField.getFieldName(),
                txField.getTipName(),
                txField.getFieldType(),
                false
                , Collections.singletonList(txField.getFieldType())));

            LinkedList<SyntaxAppenderWrapper> linkedList = new LinkedList<>();
            // 连接符追加器
            linkedList.add(new SyntaxAppenderWrapper(new CustomJoinAppender("And", "AND", AreaSequence.CONDITION)));
            // 字段追加器
            linkedList.add(new SyntaxAppenderWrapper(new CustomFieldAppender(txField, AreaSequence.CONDITION)));
            String templateText = equalsAppender.getTemplateText(tableName, entityClass, currentFieldParameters, linkedList, conditionFieldWrapper);
            stringJoiner.add(templateText);
        }
        String text = stringJoiner.toString();
        if(text.startsWith("AND")){
            text = text.substring(3);
        }
        return text;
    }


    /**
     * 前面必须是区域， 后续必须是区域或者空
     * @param secondAppender
     * @param areaSequence
     * @return
     */
    @Override
    public boolean checkAfter(SyntaxAppender secondAppender, AreaSequence areaSequence) {
        boolean hasAreaCheck = secondAppender == null || secondAppender.getAreaSequence() == AreaSequence.AREA;
        boolean fieldAreaCheck = getAreaSequence() == areaSequence;
        return hasAreaCheck && fieldAreaCheck;
    }
}
