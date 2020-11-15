package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.extension;

import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.AreaSequence;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CompositeAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomAreaAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomFieldAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomJoinAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomSuffixAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.factory.ConditionAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest.ConditionFieldWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.UpdateOperator;
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
 *
 */
public class UpdateAllFieldsConditionAppenderFactory extends ConditionAppenderFactory implements SyntaxAppenderFactory {

    private PsiClass entityClass;

    /**
     * Instantiates a new Condition appender factory.
     *  @param resultAreaName the result area name
     * @param entityClass
     * @param mappingField   the mapping field
     */
    public UpdateAllFieldsConditionAppenderFactory(String resultAreaName, PsiClass entityClass, List<TxField> mappingField) {
        super(resultAreaName, mappingField);
        this.entityClass = entityClass;
    }

    @Override
    public List<SyntaxAppender> initAppender(List<TxField> mappingField, String resultAreaName) {
        // 区域 :  By + allFields
        final CompositeAppender areaFieldsAppender = new CompositeAppender(
            CustomAreaAppender.createCustomAreaAppender(this.getTipText(),
                getTipText(),
                AreaSequence.AREA,
                AreaSequence.CONDITION,
                this),
            new CustomAllFieldAppender(new AllTxFields(mappingField,entityClass), AreaSequence.CONDITION, mappingField));


        return Collections.singletonList(areaFieldsAppender);
    }

    @Override
    public List<TxParameter> getMxParameter(PsiClass entityClass, LinkedList<SyntaxAppenderWrapper> jpaStringList) {
        TxParameter byOrigin = TxParameter.createByPsiParameter(entityClass);
        return Collections.singletonList(byOrigin);
    }


    /**
     * 自定义 AllFields 追加器
     */
    private class CustomAllFieldAppender extends CustomFieldAppender {
        private List<TxField> mappingField;

        public CustomAllFieldAppender(TxField field, AreaSequence areaSequence, List<TxField> mappingField) {
            super(field, areaSequence);
            this.mappingField = mappingField;
        }

        @Override
        public String getTemplateText(String tableName,
                                      PsiClass entityClass,
                                      LinkedList<TxParameter> parameters,
                                      LinkedList<SyntaxAppenderWrapper> collector,
                                      ConditionFieldWrapper conditionFieldWrapper) {
            StringJoiner stringBuilder = new StringJoiner("\n");
            // equals 后缀追加器
            CustomSuffixAppender equalsAppender = CustomSuffixAppender.createByParamJoin("Equals", "=", AreaSequence.CONDITION);
            for (TxField txField : mappingField) {
                LinkedList<TxParameter> currentFieldParameters = new LinkedList<>();
                currentFieldParameters.add(TxParameter.createByOrigin(txField.getFieldName(),txField.getTipName(),
                    txField.getFieldType(),
                    false
                    , Collections.singletonList(txField.getFieldType())));

                LinkedList<SyntaxAppenderWrapper> linkedList = new LinkedList<>();
                // 连接符追加器
                linkedList.add(new SyntaxAppenderWrapper(new CustomJoinAppender("And", "AND", AreaSequence.CONDITION)));
                // 字段追加器
                linkedList.add(new SyntaxAppenderWrapper(new CustomFieldAppender(txField, AreaSequence.CONDITION)));
                String templateText =  equalsAppender.getTemplateText(tableName,entityClass,currentFieldParameters,linkedList,conditionFieldWrapper);
                stringBuilder.add(templateText);
            }
            return stringBuilder.toString();
        }
    }

    /**
     * 扩展 AllTxField
     */
    private class AllTxFields extends TxField {

        private List<TxField> mappingField;
        private PsiClass entityClass;

        public AllTxFields(List<TxField> mappingField, PsiClass entityClass) {
            super();
            this.mappingField = mappingField;
            this.entityClass = entityClass;
        }

        @Override
        public String getTipName() {
            return "AllFields";
        }

        @Override
        public String getFieldName() {
            return mappingField.stream().map(TxField::getFieldName).collect(Collectors.joining(","));
        }

        @Override
        public @NotNull String getColumnName() {
            return mappingField.stream().map(TxField::getColumnName).collect(Collectors.joining(","));
        }

        @Override
        public String getFieldType() {
            return entityClass.getQualifiedName();
        }
    }

}
