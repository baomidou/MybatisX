package com.baomidou.plugin.idea.mybatisx.smartjpa.operate;


import com.baomidou.plugin.idea.mybatisx.generate.AbstractStatementGenerator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.AreaSequence;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CompositeAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomAreaAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomFieldAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomJoinAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.factory.ConditionAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.factory.ResultAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.factory.SortAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxReturnDescriptor;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.MybatisXmlGenerator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager.StatementBlock;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.SyntaxAppenderWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SelectOperator extends BaseOperatorManager {


    public SelectOperator(List<TxField> mappingField) {

        this.setOperatorNameList(AbstractStatementGenerator.SELECT_GENERATOR.getPatterns());
        this.init(mappingField);
    }

    public void init(final List<TxField> mappingField) {
        SortAppenderFactory sortAppenderFactory = new SortAppenderFactory(mappingField);
        for (String areaName : this.getOperatorNameList()) {
            StatementBlock statementBlock = new StatementBlock();

            ConditionAppenderFactory conditionAppenderFactory = new ConditionAppenderFactory(areaName, mappingField);
            statementBlock.setConditionAppenderFactory(conditionAppenderFactory);

            // 结果集区域
            ResultAppenderFactory resultAppenderFactory = new SelectResultAppenderFactory(areaName);
            this.initResultAppender(resultAppenderFactory, mappingField, areaName,conditionAppenderFactory);

            statementBlock.setResultAppenderFactory(resultAppenderFactory);
            //条件区域
            statementBlock.setSortAppenderFactory(sortAppenderFactory);
            statementBlock.setTagName(areaName);
            this.registerStatementBlock(statementBlock);
        }

    }


    /**
     * 查询特定的结果追加工厂
     */
    private class SelectResultAppenderFactory extends ResultAppenderFactory {

        public SelectResultAppenderFactory(String areaPrefix) {
            super(areaPrefix);
        }

        /**
         * 查询的结果不需要添加到参数中
         *
         * @param entityClass
         * @param jpaStringList
         * @return
         */
        @Override
        public List<TxParameter> getMxParameter(PsiClass entityClass, LinkedList<SyntaxAppender> jpaStringList) {

            // 移除select 标签
            List<TxParameter> txParameter = super.getMxParameter(entityClass, jpaStringList);
            return Collections.emptyList();
        }


        @Override
        public String getTemplateText(String tableName,
                                      PsiClass entityClass,
                                      LinkedList<PsiParameter> parameters,
                                      LinkedList<SyntaxAppenderWrapper> collector, MybatisXmlGenerator mybatisXmlGenerator) {
            if (collector.isEmpty()) {
                return "select <include refid=\"Base_Column_List\"/> from " + tableName;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("select").append(" ");
            for (SyntaxAppenderWrapper syntaxAppender : collector) {
                // 列名 或者 逗号
                String columnName = syntaxAppender.getAppender()
                    .getTemplateText(tableName, entityClass, parameters, syntaxAppender.getCollector(), mybatisXmlGenerator);
                stringBuilder.append(columnName);
            }
            stringBuilder.append("\n").append("from").append(" ").append(tableName);
            return stringBuilder.toString();
        }


    }


    private void initResultAppender(final ResultAppenderFactory selectFactory,
                                    final List<TxField> mappingField,
                                    final String areaName,
                                    ConditionAppenderFactory conditionAppenderFactory) {
        for (TxField field : mappingField) {
            // field
            // and + field
            CompositeAppender andAppender = new SelectCompositeAppender(
                new CustomJoinAppender("And", ",", AreaSequence.RESULT),
                new SelectFieldAppender(field));
            selectFactory.registerAppender(andAppender);

            // select + field
            CompositeAppender areaAppender =
                new SelectCompositeAppender(
                    new SelectCustomAreaAppender(areaName, ResultAppenderFactory.RESULT, selectFactory),
                    new SelectFieldAppender(field)
                );
            selectFactory.registerAppender(areaAppender);


            // 区域条件 : select + By + field
            CompositeAppender areaByAppender = new CompositeAppender(
                new SelectCustomAreaAppender(areaName, ResultAppenderFactory.RESULT, selectFactory),
                CustomAreaAppender.createCustomAreaAppender("By", "By", AreaSequence.AREA, AreaSequence.CONDITION, conditionAppenderFactory),
                new CustomFieldAppender(field, AreaSequence.CONDITION)
            );
            selectFactory.registerAppender(areaByAppender);

            // select + All + By + field
            CompositeAppender selectAllAppender =
                new CompositeAppender(
                    new SelectCustomAreaAppender(areaName, ResultAppenderFactory.RESULT,
                        selectFactory),
                    new SelectAllFieldAppender("All"),
                    CustomAreaAppender.createCustomAreaAppender("By", "By", AreaSequence.AREA, AreaSequence.CONDITION, conditionAppenderFactory),
                    new CustomFieldAppender(field, AreaSequence.CONDITION)
                );
            selectFactory.registerAppender(selectAllAppender);
        }


    }

    private class SelectCustomAreaAppender extends CustomAreaAppender {


        public SelectCustomAreaAppender(final String area, final String areaType, final SyntaxAppenderFactory syntaxAppenderFactory) {
            super(area, areaType, AreaSequence.AREA, AreaSequence.RESULT, syntaxAppenderFactory);
        }


    }

    private class SelectAllFieldAppender extends CustomFieldAppender {

        public SelectAllFieldAppender(String tipName) {
            super(tipName);
            super.setAreaSequence(AreaSequence.RESULT);
        }

        @Override
        public List<TxParameter> getMxParameter(LinkedList<SyntaxAppender> jpaStringList, PsiClass entityClass) {
            // 把All 移除
            jpaStringList.poll();
            return Collections.emptyList();
        }
    }


    private class SelectCompositeAppender extends CompositeAppender {

        public SelectCompositeAppender(final SyntaxAppender... appenders) {
            super(appenders);
        }

        @Override
        public String getTemplateText(String tableName, PsiClass entityClass, LinkedList<PsiParameter> parameters, LinkedList<SyntaxAppenderWrapper> collector, MybatisXmlGenerator mybatisXmlGenerator) {
            return appenderList
                .stream()
                .map(x -> x.getTemplateText(tableName, entityClass, parameters, collector, mybatisXmlGenerator))
                .collect(Collectors.joining(","));
        }
    }

    // 查询类型的结果集区域,  字段拼接部分, 只需要字段名称就可以了
    private static class SelectFieldAppender extends CustomFieldAppender {


        private SelectFieldAppender(TxField txField) {
            super(txField, AreaSequence.RESULT);
        }


        @Override
        public String getTemplateText(String tableName, PsiClass entityClass, LinkedList<PsiParameter> parameters, LinkedList<SyntaxAppenderWrapper> collector, MybatisXmlGenerator mybatisXmlGenerator) {
            return columnName;
        }
    }

    @Override
    public TxReturnDescriptor getReturnWrapper(String text, PsiClass entityClass, LinkedList<SyntaxAppender> linkedList) {
        return TxReturnDescriptor.createByPsiClass(entityClass);
    }

    @Override
    public void generateMapperXml(String id, LinkedList<SyntaxAppender> jpaList,
                                  PsiClass entityClass,
                                  PsiMethod psiMethod, String tableName,
                                  MybatisXmlGenerator mybatisXmlGenerator) {
        String mapperXml = super.generateXml(id, jpaList, entityClass, psiMethod, tableName, mybatisXmlGenerator);
        mybatisXmlGenerator.generateSelect(id, mapperXml);
    }

    @Override
    public String getTagName() {
        return "select";
    }
}
