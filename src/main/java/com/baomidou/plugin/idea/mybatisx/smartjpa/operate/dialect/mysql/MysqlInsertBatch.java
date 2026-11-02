package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.dialect.mysql;

import org.jetbrains.annotations.NotNull;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.AreaSequence;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomAreaAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.JdbcTypeUtils;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.operator.suffix.SuffixOperator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.factory.ResultAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest.ConditionFieldWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxReturnDescriptor;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.dialect.CustomStatement;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.dialect.oracle.InsertCustomSuffixAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager.StatementBlock;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.SyntaxAppenderWrapper;
import com.baomidou.plugin.idea.mybatisx.util.MybatisXCollectors;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * The type Mysql insert batch.
 */
public class MysqlInsertBatch implements CustomStatement {

    /**
     * The Statement block.
     */
    StatementBlock statementBlock;
    /**
     * The Operator name.
     */
    String operatorName;


    /**
     * Instantiates a new Mysql insert batch.
     */
    public MysqlInsertBatch() {

    }

    /**
     * Init insert batch.
     *
     * @param areaName     the area name
     * @param mappingField the mapping field
     */
    public void initInsertBatch(String areaName, List<TxField> mappingField) {
        String newAreaName = getNewAreaName(areaName);
        // insertBatch
        ResultAppenderFactory appenderFactory = getResultAppenderFactory(mappingField, newAreaName);
        // insert + Batch
        final SyntaxAppender batchAppender = CustomAreaAppender.createCustomAreaAppender(newAreaName, ResultAppenderFactory.RESULT, AreaSequence.AREA, AreaSequence.RESULT, appenderFactory);
        appenderFactory.registerAppender(batchAppender);

        StatementBlock statementBlock = new StatementBlock();
        statementBlock.setResultAppenderFactory(appenderFactory);
        statementBlock.setTagName(newAreaName);
        statementBlock.setReturnWrapper(TxReturnDescriptor.createByOrigin(null, "int"));
        this.statementBlock = statementBlock;

        this.operatorName = newAreaName;
    }

    /**
     * Gets result appender factory.
     *
     * @param mappingField the mapping field
     * @param newAreaName  the new area name
     * @return the result appender factory
     */
    protected ResultAppenderFactory getResultAppenderFactory(List<TxField> mappingField, String newAreaName) {

        return new InsertBatchResultAppenderFactory(newAreaName) {
            @Override
            public String getTemplateText(String tableName, PsiClass entityClass, LinkedList<TxParameter> parameters, LinkedList<SyntaxAppenderWrapper> collector, ConditionFieldWrapper conditionFieldWrapper) {
                // 定制参数
                SyntaxAppender suffixOperator = InsertCustomSuffixAppender.createInsertBySuffixOperator(batchName(), getSuffixOperator(mappingField), AreaSequence.RESULT);
                LinkedList<SyntaxAppenderWrapper> syntaxAppenderWrappers = new LinkedList<>();
                syntaxAppenderWrappers.add(new SyntaxAppenderWrapper(suffixOperator));
                return super.getTemplateText(tableName, entityClass, parameters, syntaxAppenderWrappers, conditionFieldWrapper);
            }
        };
    }

    /**
     * Batch name string.
     *
     * @return the string
     */
    @NotNull
    protected String batchName() {
        return "Batch";
    }

    /**
     * Gets new area name.
     *
     * @param areaName the area name
     * @return the new area name
     */
    @NotNull
    protected String getNewAreaName(String areaName) {
        return areaName + batchName();
    }

    /**
     * Gets suffix operator.
     *
     * @param mappingField the mapping field
     * @return the suffix operator
     */
    @NotNull
    protected SuffixOperator getSuffixOperator(List<TxField> mappingField) {
        return new InsertBatchSuffixOperator(mappingField);
    }

    @Override
    public StatementBlock getStatementBlock() {
        return statementBlock;
    }

    @Override
    public String operatorName() {
        return operatorName;
    }


    private static class InsertBatchResultAppenderFactory extends ResultAppenderFactory {

        /**
         * Instantiates a new Insert batch result appender factory.
         *
         * @param areaPrefix the area prefix
         */
        public InsertBatchResultAppenderFactory(String areaPrefix) {
            super(areaPrefix);
        }

        @Override
        public String getTemplateText(String tableName, PsiClass entityClass, LinkedList<TxParameter> parameters, LinkedList<SyntaxAppenderWrapper> collector, ConditionFieldWrapper conditionFieldWrapper) {
            StringBuilder mapperXml = new StringBuilder("insert into " + tableName);
            for (SyntaxAppenderWrapper syntaxAppenderWrapper : collector) {
                String templateText = syntaxAppenderWrapper.getAppender().getTemplateText(tableName, entityClass, parameters, collector, conditionFieldWrapper);
                mapperXml.append(templateText);
            }
            return mapperXml.toString();
        }

        @Override
        public List<TxParameter> getMxParameter(PsiClass entityClass, LinkedList<SyntaxAppenderWrapper> jpaStringList) {
            // 遍历定义的类型
            String defineName = Collection.class.getSimpleName() + "<" + entityClass.getName() + ">";
            // 变量名称
            String variableName = StringUtils.lowerCaseFirstChar(entityClass.getName()) + "Collection";

            List<String> importClass = new ArrayList<>();
            importClass.add("java.util.Collection");
            importClass.add(entityClass.getQualifiedName());
            TxParameter parameter = TxParameter.createByOrigin(variableName, defineName, Collection.class.getName(), true, importClass);
            return Collections.singletonList(parameter);
        }
    }


    /**
     * 批量插入
     */
    private record InsertBatchSuffixOperator(List<TxField> mappingField) implements SuffixOperator {

        /**
         * Instantiates a new Insert batch suffix operator.
         *
         * @param mappingField the mapping field
         */
        private InsertBatchSuffixOperator {
        }

        @Override
        public String getTemplateText(String fieldName, @NotNull LinkedList<TxParameter> parameters, ConditionFieldWrapper conditionFieldWrapper) {
            // 1. 参数校验
            if (parameters.isEmpty()) {
                throw new IllegalArgumentException("Parameters list cannot be null or empty");
            }

            final TxParameter collection = parameters.poll();
            final String collectionName = collection.getName();
            // 2. 构建 INSERT 语句
            String columns = buildColumnClause(conditionFieldWrapper);
            String valuesPlaceholder = buildValuesPlaceholder(collectionName, conditionFieldWrapper);

            return "(%s)\nvalues\n%s"
                .formatted(columns, valuesPlaceholder);
        }


        /**
         * 构建 VALUES 占位符（包含 foreach 循环）
         */
        private String buildValuesPlaceholder(String collectionName, ConditionFieldWrapper wrapper) {
            String itemName = "item";
            String fieldPlaceholders = buildFieldPlaceholders(itemName, wrapper);

            return "<foreach collection=\"%s\" item=\"%s\" separator=\",\">\n(%s)\n</foreach>"
                .formatted(collectionName, itemName, fieldPlaceholders);
        }

        /**
         * 构建字段值占位符
         */
        private String buildFieldPlaceholders(String itemName, ConditionFieldWrapper conditionFieldWrapper) {
            return mappingField.stream().map(field -> {
                String fieldPath = itemName + "." + field.getFieldName();
                String fieldValue = JdbcTypeUtils.wrapperField(fieldPath, field.getFieldType());
                return conditionFieldWrapper.wrapDefaultDateIfNecessary(field.getColumnName(), fieldValue);
            }).collect(MybatisXCollectors.joining(",", conditionFieldWrapper.getNewline()));
        }

        /**
         * 构建列名子句
         */
        private String buildColumnClause(ConditionFieldWrapper wrapper) {
            return mappingField.stream()
                .map(TxField::getColumnName)
                .collect(MybatisXCollectors.joining(",", wrapper.getNewline()));
        }
    }
}
