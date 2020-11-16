package com.baomidou.plugin.idea.mybatisx.smartjpa.operate;


import com.baomidou.plugin.idea.mybatisx.generate.AbstractStatementGenerator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.AreaSequence;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CompositeAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomAreaAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomFieldAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomJoinAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.factory.ConditionAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.factory.ResultAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest.ConditionFieldWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxReturnDescriptor;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.extension.AllTxFields;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.extension.ConditionAllFieldAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.Generator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager.StatementBlock;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.SyntaxAppenderWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Update operator.
 */
public class UpdateOperator extends BaseOperatorManager {


    /**
     * Instantiates a new Update operator.
     *
     * @param mappingField the mapping field
     * @param entityClass
     */
    public UpdateOperator(final List<TxField> mappingField, PsiClass entityClass) {
        this.setOperatorNameList(AbstractStatementGenerator.UPDATE_GENERATOR.getPatterns());
        this.init(mappingField,entityClass);
    }

    /**
     * Init.
     *
     * @param mappingField the mapping field
     * @param entityClass
     */
    public void init(final List<TxField> mappingField, PsiClass entityClass) {
        TxReturnDescriptor anInt = TxReturnDescriptor.createByOrigin(null, "int");

        for (final String areaName : this.getOperatorNameList()) {
            final ResultAppenderFactory updateFactory = new UpdateResultAppenderFactory(areaName);
            this.initResultAppender(updateFactory, mappingField, areaName);

            StatementBlock statementBlock = new StatementBlock();
            statementBlock.setTagName(areaName);
            statementBlock.setResultAppenderFactory(updateFactory);
            statementBlock.setConditionAppenderFactory(new ConditionAppenderFactory(areaName, mappingField){
                @Override
                public List<SyntaxAppender> initAppender(List<TxField> mappingField, String resultAreaName) {
                    List<SyntaxAppender> syntaxAppenders = super.initAppender(mappingField, resultAreaName);
                    // 区域 :  By + allFields

                    AllTxFields field = new AllTxFields(mappingField, entityClass);
                    ConditionAllFieldAppender customAllFieldAppender = new ConditionAllFieldAppender(field, AreaSequence.CONDITION, mappingField);
                    final CompositeAppender areaFieldsAppender = new CompositeAppender(
                        CustomAreaAppender.createCustomAreaAppender(this.getTipText(),
                            getTipText(),
                            AreaSequence.AREA,
                            AreaSequence.CONDITION,
                            this),
                        customAllFieldAppender);
                    syntaxAppenders.add(customAllFieldAppender);

                    syntaxAppenders.add(areaFieldsAppender);
                    return syntaxAppenders;
                }
            });
            statementBlock.setReturnWrapper(anInt);
            this.registerStatementBlock(statementBlock);
        }

    }

    private class UpdateResultAppenderFactory extends ResultAppenderFactory {

        /**
         * Instantiates a new Update result appender factory.
         *
         * @param areaPrefix the area prefix
         */
        public UpdateResultAppenderFactory(String areaPrefix) {
            super(areaPrefix);
        }

        @Override
        public String getTemplateText(String tableName,
                                      PsiClass entityClass,
                                      LinkedList<TxParameter> parameters,
                                      LinkedList<SyntaxAppenderWrapper> collector, ConditionFieldWrapper conditionFieldWrapper) {
            String operatorXml = "update " + tableName + "\n set ";

            return operatorXml + collector.stream().map(syntaxAppenderWrapper -> {
                return syntaxAppenderWrapper.getAppender().getTemplateText(tableName, entityClass, parameters, collector, conditionFieldWrapper);
            }).collect(Collectors.joining());
        }

        @Override
        public List<TxParameter> getMxParameter(PsiClass entityClass, LinkedList<SyntaxAppenderWrapper> jpaStringList) {
            return new SyntaxAppenderWrapper(null, jpaStringList).getMxParameter(entityClass);
        }
    }


    private void initResultAppender(final ResultAppenderFactory updateFactory, final List<TxField> mappingField, final String areaName) {
        for (final TxField field : mappingField) {
            // field
            // and + field
            final CompositeAppender andAppender = new CompositeAppender(
                new CustomJoinAppender("And", ",\n", AreaSequence.RESULT),
                new ResultAppenderFactory.WrapDateCustomFieldAppender(field, AreaSequence.RESULT));
            updateFactory.registerAppender(andAppender);

            // update + field
            final CompositeAppender areaAppender =
                new CompositeAppender(
                    CustomAreaAppender.createCustomAreaAppender(areaName,
                        ResultAppenderFactory.RESULT,
                        AreaSequence.AREA,
                        AreaSequence.RESULT,
                        updateFactory),
                    new CustomFieldAppender(field, AreaSequence.RESULT)
                );
            updateFactory.registerAppender(areaAppender);

        }
    }


    @Override
    public String getTagName() {
        return "update";
    }

    @Override
    public void generateMapperXml(String id, LinkedList<SyntaxAppender> jpaList, PsiClass entityClass, PsiMethod psiMethod, String tableName, Generator mybatisXmlGenerator, ConditionFieldWrapper conditionFieldWrapper) {
        String mapperXml = super.generateXml(jpaList, entityClass, psiMethod, tableName, conditionFieldWrapper);
        mybatisXmlGenerator.generateUpdate(id, mapperXml);
    }
}
