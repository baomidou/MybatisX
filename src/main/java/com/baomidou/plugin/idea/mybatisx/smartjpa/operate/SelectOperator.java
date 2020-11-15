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
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest.ConditionFieldWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxReturnDescriptor;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.Generator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager.StatementBlock;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.SyntaxAppenderWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The type Select operator.
 */
public class SelectOperator extends BaseOperatorManager {


    /**
     * Instantiates a new Select operator.
     *
     * @param mappingField the mapping field
     * @param entityClass  the entity class
     */
    public SelectOperator(List<TxField> mappingField, PsiClass entityClass) {
        Set<String> patterns = getPatterns();
        this.init(mappingField, entityClass, patterns);
    }

    protected Set<String> getPatterns() {
        Set<String> patterns = AbstractStatementGenerator.SELECT_GENERATOR.getPatterns();
        HashSet<String> strings = new HashSet<>(patterns);
        // 1.4.x 支持countBy 语法, 由于历史原因， 查询必须排除count
        strings.remove("count");
        return strings;
    }

    /**
     * Init.
     *
     * @param mappingField the mapping field
     * @param entityClass  the entity class
     * @param patterns     the patterns
     */
    public void init(final List<TxField> mappingField, PsiClass entityClass, Set<String> patterns) {
        SortAppenderFactory sortAppenderFactory = new SortAppenderFactory(mappingField);
        for (String areaName : patterns) {
            // 返回自定义字段
            initCustomFieldBlock(areaName, mappingField, sortAppenderFactory, entityClass);
            // 返回集合
            initSelectAllBlock(areaName, mappingField, sortAppenderFactory, entityClass);
            // 返回对象
            initSelectOneBlock(areaName, mappingField, sortAppenderFactory, entityClass);

        }

    }

    /**
     * 初始化 selectOne+By+field区域
     *
     * @param areaName
     * @param mappingField
     * @param sortAppenderFactory
     * @param entityClass
     */
    private void initSelectOneBlock(String areaName,
                                    List<TxField> mappingField,
                                    SortAppenderFactory sortAppenderFactory,
                                    PsiClass entityClass) {
        String newAreaName = areaName + "One";
        StatementBlock statementBlock = new StatementBlock();

        ConditionAppenderFactory conditionAppenderFactory = new ConditionAppenderFactory(newAreaName, mappingField);
        statementBlock.setConditionAppenderFactory(conditionAppenderFactory);

        // 结果集区域
        ResultAppenderFactory resultAppenderFactory = new SelectResultAppenderFactory(newAreaName) {
            @Override
            public String getTemplateText(String tableName,
                                          PsiClass entityClass,
                                          LinkedList<TxParameter> parameters,
                                          LinkedList<SyntaxAppenderWrapper> collector,
                                          ConditionFieldWrapper conditionFieldWrapper) {
                // 把查询区的参数清空
                super.getTemplateText(tableName, entityClass, parameters, collector, conditionFieldWrapper);
                // 无论如何都是返回这样的查询
                String allFields = conditionFieldWrapper.getAllFields();
                return "select " + allFields + " from " + tableName;
            }
        };

        for (TxField txField : mappingField) {
            // select + One + By + field
            CompositeAppender selectAllAppender =
                new CompositeAppender(
                    new SelectCustomAreaAppender(newAreaName, ResultAppenderFactory.RESULT,
                        resultAppenderFactory),
                    CustomAreaAppender.createCustomAreaAppender("By", "By", AreaSequence.AREA, AreaSequence.CONDITION, conditionAppenderFactory),
                    new CustomFieldAppender(txField, AreaSequence.CONDITION)
                );
            resultAppenderFactory.registerAppender(selectAllAppender);
        }

        statementBlock.setResultAppenderFactory(resultAppenderFactory);
        //条件区域
        statementBlock.setSortAppenderFactory(sortAppenderFactory);
        statementBlock.setTagName(newAreaName);
        statementBlock.setReturnWrapper(TxReturnDescriptor.createByPsiClass(entityClass));
        this.registerStatementBlock(statementBlock);

        this.addOperatorName(newAreaName);
    }

    private void initSelectAllBlock(String areaName, List<TxField> mappingField, SortAppenderFactory sortAppenderFactory, PsiClass entityClass) {
        String newAreaName = areaName + "All";
        StatementBlock statementBlock = new StatementBlock();

        ConditionAppenderFactory conditionAppenderFactory = new ConditionAppenderFactory(newAreaName, mappingField);
        statementBlock.setConditionAppenderFactory(conditionAppenderFactory);

        // 结果集区域
        ResultAppenderFactory resultAppenderFactory = new SelectResultAppenderFactory(newAreaName) {
            @Override
            public String getTemplateText(String tableName,
                                          PsiClass entityClass,
                                          LinkedList<TxParameter> parameters,
                                          LinkedList<SyntaxAppenderWrapper> collector, ConditionFieldWrapper conditionFieldWrapper) {
                // 把查询区的参数清空
                super.getTemplateText(tableName, entityClass, parameters, collector, conditionFieldWrapper);
                // 无论如何都是返回这样的查询
                String allFields = conditionFieldWrapper.getAllFields();
                return "select " + allFields + " from " + tableName;
            }
        };

        for (TxField txField : mappingField) {
            // select + One + By + field
            CompositeAppender selectAllAppender =
                new CompositeAppender(
                    new SelectCustomAreaAppender(newAreaName, ResultAppenderFactory.RESULT,
                        resultAppenderFactory),
                    CustomAreaAppender.createCustomAreaAppender("By", "By", AreaSequence.AREA, AreaSequence.CONDITION, conditionAppenderFactory),
                    new CustomFieldAppender(txField, AreaSequence.CONDITION)
                );
            resultAppenderFactory.registerAppender(selectAllAppender);
        }

        statementBlock.setResultAppenderFactory(resultAppenderFactory);
        //条件区域
        statementBlock.setSortAppenderFactory(sortAppenderFactory);
        statementBlock.setTagName(newAreaName);
        statementBlock.setReturnWrapper(TxReturnDescriptor.createCollectionByPsiClass(entityClass));
        this.registerStatementBlock(statementBlock);

        this.addOperatorName(newAreaName);
    }

    private void initCustomFieldBlock(String areaName, List<TxField> mappingField, SortAppenderFactory sortAppenderFactory, PsiClass entityClass) {
        StatementBlock statementBlock = new StatementBlock();
        ConditionAppenderFactory conditionAppenderFactory = new ConditionAppenderFactory(areaName, mappingField);
        statementBlock.setConditionAppenderFactory(conditionAppenderFactory);

        // 结果集区域

        ResultAppenderFactory resultAppenderFactory =
            this.initCustomFieldResultAppender(mappingField, areaName, conditionAppenderFactory);

        statementBlock.setResultAppenderFactory(resultAppenderFactory);

        //条件区域
        statementBlock.setSortAppenderFactory(sortAppenderFactory);
        statementBlock.setTagName(areaName);
        statementBlock.setReturnWrapper(TxReturnDescriptor.createCollectionByPsiClass(entityClass));
        this.registerStatementBlock(statementBlock);

        this.addOperatorName(areaName);
    }


    /**
     * 查询特定的结果追加工厂
     */
    private class SelectResultAppenderFactory extends ResultAppenderFactory {

        /**
         * Instantiates a new Select result appender factory.
         *
         * @param areaPrefix the area prefix
         */
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
        public List<TxParameter> getMxParameter(PsiClass entityClass, LinkedList<SyntaxAppenderWrapper> jpaStringList) {

            // 移除select 标签
            List<TxParameter> txParameter = super.getMxParameter(entityClass, jpaStringList);
            return Collections.emptyList();
        }


        @Override
        public String getTemplateText(String tableName,
                                      PsiClass entityClass,
                                      LinkedList<TxParameter> parameters,
                                      LinkedList<SyntaxAppenderWrapper> collector, ConditionFieldWrapper conditionFieldWrapper) {
            if (collector.isEmpty()) {
                String allFields = conditionFieldWrapper.getAllFields();
                return "select " + allFields + " from " + tableName;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("select").append(" ");
            for (SyntaxAppenderWrapper syntaxAppender : collector) {
                // 列名 或者 逗号
                String columnName = syntaxAppender.getAppender()
                    .getTemplateText(tableName, entityClass, parameters, syntaxAppender.getCollector(), conditionFieldWrapper);
                stringBuilder.append(columnName);
            }
            stringBuilder.append("\n").append("from").append(" ").append(tableName);
            return stringBuilder.toString();
        }


    }


    protected ResultAppenderFactory initCustomFieldResultAppender(final List<TxField> mappingField,
                                               final String areaName,
                                               ConditionAppenderFactory conditionAppenderFactory) {
        ResultAppenderFactory selectFactory = new SelectResultAppenderFactory(areaName);
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


        }
return selectFactory;

    }

    private class SelectCustomAreaAppender extends CustomAreaAppender {


        /**
         * Instantiates a new Select custom area appender.
         *
         * @param area                  the area
         * @param areaType              the area type
         * @param syntaxAppenderFactory the syntax appender factory
         */
        public SelectCustomAreaAppender(final String area, final String areaType, final SyntaxAppenderFactory syntaxAppenderFactory) {
            super(area, areaType, AreaSequence.AREA, AreaSequence.RESULT, syntaxAppenderFactory);
        }

        /**
         * select 标签的字段一定不会生成参数
         * @param syntaxAppenderWrappers the jpa string list
         * @param entityClass   the entity class
         * @return
         */
        @Override
        public List<TxParameter> getMxParameter(LinkedList<SyntaxAppenderWrapper> syntaxAppenderWrappers, PsiClass entityClass) {
            return Collections.emptyList();
        }
    }


    private class SelectCompositeAppender extends CompositeAppender {

        /**
         * Instantiates a new Select composite appender.
         *
         * @param appenders the appenders
         */
        public SelectCompositeAppender(final SyntaxAppender... appenders) {
            super(appenders);
        }

        @Override
        public String getTemplateText(String tableName, PsiClass entityClass, LinkedList<TxParameter> parameters, LinkedList<SyntaxAppenderWrapper> collector, ConditionFieldWrapper conditionFieldWrapper) {
            return appenderList
                .stream()
                .map(x -> x.getTemplateText(tableName, entityClass, parameters, collector, conditionFieldWrapper))
                .collect(Collectors.joining(","));
        }
    }

    // 查询类型的结果集区域,  字段拼接部分, 只需要字段名称就可以了
    private static class SelectFieldAppender extends CustomFieldAppender {

        private SelectFieldAppender(TxField txField) {
            super(txField, AreaSequence.RESULT);
        }


        @Override
        public String getTemplateText(String tableName, PsiClass entityClass, LinkedList<TxParameter> parameters, LinkedList<SyntaxAppenderWrapper> collector, ConditionFieldWrapper conditionFieldWrapper) {
            return columnName;
        }
    }

    @Override
    public void generateMapperXml(String id, LinkedList<SyntaxAppender> jpaList,
                                  PsiClass entityClass,
                                  PsiMethod psiMethod, String tableName,
                                  Generator mybatisXmlGenerator, ConditionFieldWrapper conditionFieldWrapper) {
        String mapperXml = super.generateXml(jpaList, entityClass, psiMethod, tableName, conditionFieldWrapper);

        mybatisXmlGenerator.generateSelect(id, mapperXml,conditionFieldWrapper.getResultMap(), conditionFieldWrapper.getResultType());
    }

    @Override
    public String getTagName() {
        return "select";
    }
}
