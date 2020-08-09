package com.baomidou.plugin.idea.mybatisx.smartjpa.operate;


import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.*;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.factory.ConditionAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.factory.ResultAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.factory.SortAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.completion.parameter.MxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.completion.parameter.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.completion.res.ReturnWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager.StatementBlock;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.TreeWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiParameter;

import java.util.*;
import java.util.stream.Collectors;

public class SelectOperator extends BaseOperatorManager {


    public SelectOperator(List<TxField> mappingField) {

        this.setOperatorNameList("select,query,get,find");
        this.init(mappingField);
    }

    public void init(final List<TxField> mappingField) {
        SortAppenderFactory sortAppenderFactory = new SortAppenderFactory(mappingField);
        for (String areaName : this.getOperatorNameList()) {
            StatementBlock statementBlock = new StatementBlock();

            // 结果集区域
            ResultAppenderFactory resultAppenderFactory = new SelectResultAppenderFactory(areaName);
            this.initResultAppender(resultAppenderFactory, mappingField, areaName);

            statementBlock.setResultAppenderFactory(resultAppenderFactory);
            //条件区域
            statementBlock.setConditionAppenderFactory(new ConditionAppenderFactory(areaName, mappingField));
            statementBlock.setSortAppenderFactory(sortAppenderFactory);
            statementBlock.setTagName(getTagName());
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
        public List<MxParameter> getMxParameter(PsiClass entityClass, LinkedList<SyntaxAppender> jpaStringList) {

            // 移除select 标签
            List<MxParameter> mxParameter = super.getMxParameter(entityClass, jpaStringList);
            return Collections.emptyList();
        }


        @Override
        public String getTemplateText(String tableName,
                                      PsiClass entityClass,
                                      LinkedList<PsiParameter> parameters,
                                      LinkedList<TreeWrapper<SyntaxAppender>> collector) {
            if (collector.isEmpty()) {
                return "select <include refid=\"Base_Column_List\"/> from " + tableName;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("select").append(" ");
            for (TreeWrapper<SyntaxAppender> syntaxAppender : collector) {
                // 列名 或者 逗号
                String columnName = syntaxAppender.getAppender().getTemplateText(tableName, entityClass, parameters, syntaxAppender.getCollector());
                stringBuilder.append(columnName);
            }
            stringBuilder.append("\n").append("from").append(" ").append(tableName);
            return stringBuilder.toString();
        }


    }


    private void initResultAppender(final ResultAppenderFactory selectFactory, final List<TxField> mappingField, final String areaName) {
        for (TxField field : mappingField) {
            // field
            // and + field
            CompositeAppender andAppender = new SelectCompositeAppender(
                new CustomJoinAppender("And", ",", AreaSequence.RESULT),
                new SelectFieldAppender(field));
            selectFactory.registerAppender(andAppender);

            // selectFactory + field
            CompositeAppender areaAppender =
                new SelectCompositeAppender(
                    new SelectCustomAreaAppender(areaName, ResultAppenderFactory.RESULT, selectFactory),
                    new SelectFieldAppender(field)
                );
            selectFactory.registerAppender(areaAppender);

        }

        // selectFactory + All
        CompositeAppender areaAppender =
            new CompositeAppender(
                new SelectCustomAreaAppender(areaName, ResultAppenderFactory.RESULT,
                    selectFactory),
                new SelectAllFieldAppender("All")
            );
        selectFactory.registerAppender(areaAppender);
    }

    private class SelectCustomAreaAppender extends CustomAreaAppender {


        public SelectCustomAreaAppender(final String area, final String areaType, final SyntaxAppenderFactory syntaxAppenderFactory) {
            super(area, areaType, AreaSequence.AREA, AreaSequence.RESULT, syntaxAppenderFactory);
        }


    }

    private class SelectAllFieldAppender extends CustomFieldAppender {

        public SelectAllFieldAppender(String tipName) {
            super(tipName);
        }

        @Override
        public List<MxParameter> getMxParameter(LinkedList<SyntaxAppender> jpaStringList, PsiClass entityClass) {
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
        public String getTemplateText(String tableName, PsiClass entityClass, LinkedList<PsiParameter> parameters, LinkedList<TreeWrapper<SyntaxAppender>> collector) {
            return appenderList
                .stream()
                .map(x -> x.getTemplateText(tableName, entityClass, parameters, collector))
                .collect(Collectors.joining(","));
        }
    }

    // 查询类型的结果集区域,  字段拼接部分, 只需要字段名称就可以了
    private static class SelectFieldAppender extends CustomFieldAppender {


        private SelectFieldAppender(TxField txField) {
            super(txField, AreaSequence.RESULT);
        }


        @Override
        public String getTemplateText(String tableName, PsiClass entityClass, LinkedList<PsiParameter> parameters, LinkedList<TreeWrapper<SyntaxAppender>> collector) {
            return columnName;
        }
    }

    @Override
    public ReturnWrapper getReturnWrapper(String text, PsiClass entityClass, LinkedList<SyntaxAppender> linkedList) {
        return ReturnWrapper.createByPsiClass(entityClass);
    }


    @Override
    public String getTagName() {
        return "select";
    }
}
