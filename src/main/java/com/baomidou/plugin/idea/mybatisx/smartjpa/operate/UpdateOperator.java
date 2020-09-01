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
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxReturnDescriptor;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.MybatisXmlGenerator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager.StatementBlock;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.StringUtils;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.SyntaxAppenderWrapper;
import com.github.hypfvieh.util.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UpdateOperator extends BaseOperatorManager {


    public UpdateOperator(final List<TxField> mappingField) {
        this.setOperatorNameList(AbstractStatementGenerator.UPDATE_GENERATOR.getPatterns());
        this.init(mappingField);
    }

    public void init(final List<TxField> mappingField) {
        for (final String areaName : this.getOperatorNameList()) {
            final ResultAppenderFactory updateFactory = new UpdateResultAppenderFactory(areaName);
            this.initResultAppender(updateFactory, mappingField, areaName);

            StatementBlock statementBlock = new StatementBlock();
            statementBlock.setTagName(areaName);
            statementBlock.setResultAppenderFactory(updateFactory);
            statementBlock.setConditionAppenderFactory(new ConditionAppenderFactory(areaName, mappingField));
            statementBlock.setReturnWrapper(TxReturnDescriptor.createByOrigin(null,"int"));
            this.registerStatementBlock(statementBlock);
        }

    }

    private class UpdateResultAppenderFactory extends ResultAppenderFactory {

        public UpdateResultAppenderFactory(String areaPrefix) {
            super(areaPrefix);
        }

        @Override
        public String getTemplateText(String tableName,
                                      PsiClass entityClass,
                                      LinkedList<PsiParameter> parameters,
                                      LinkedList<SyntaxAppenderWrapper> collector,
                                      MybatisXmlGenerator mybatisXmlGenerator) {
            String operatorXml = "update " + tableName + "\n set ";

            return operatorXml + collector.stream().map(syntaxAppenderWrapper -> {
                return syntaxAppenderWrapper.getAppender().getTemplateText(tableName, entityClass, parameters, collector, mybatisXmlGenerator);
            }).collect(Collectors.joining());
        }
    }


    private void initResultAppender(final ResultAppenderFactory updateFactory, final List<TxField> mappingField, final String areaName) {
        for (final TxField field : mappingField) {
            // field
            // and + field
            final CompositeAppender andAppender = new CompositeAppender(
                new CustomJoinAppender("And", ",", AreaSequence.RESULT),
                new CustomFieldAppender(field, AreaSequence.RESULT));
            updateFactory.registerAppender(andAppender);

            // update + field
            final CompositeAppender areaAppender =
                new CompositeAppender(
                    CustomAreaAppender.createCustomAreaAppender(areaName, ResultAppenderFactory.RESULT, AreaSequence.AREA, AreaSequence.RESULT, updateFactory),
                    new CustomFieldAppender(field, AreaSequence.RESULT)
                );
            updateFactory.registerAppender(areaAppender);

        }
    }


    @Override
    public List<TxParameter> getParameters(PsiClass entityClass, LinkedList<SyntaxAppender> jpaStringList) {
        List<TxParameter> parameters = super.getParameters(entityClass, jpaStringList);
        Set<String> collection = new HashSet<>();
        for (TxParameter parameter : parameters) {
            String name = parameter.getName();
            if (!collection.add(name)) {
                String newName = "old" + StringUtils.upperCaseFirstChar(name);
                parameter.setName(newName);
            }
        }
        return parameters;
    }


    @Override
    public String getTagName() {
        return "update";
    }

    @Override
    public void generateMapperXml(String id, LinkedList<SyntaxAppender> jpaList, PsiClass entityClass, PsiMethod psiMethod, String tableName, MybatisXmlGenerator mybatisXmlGenerator) {
        String mapperXml = super.generateXml(jpaList, entityClass, psiMethod, tableName, mybatisXmlGenerator);
        mybatisXmlGenerator.generateUpdate(id, mapperXml);
    }
}
