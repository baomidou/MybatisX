package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.dialect.mysql;

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
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Mysql update selective
 * 根据主键更新其他的列
 */
public class MysqlUpdateSelective implements CustomStatement {

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
    public MysqlUpdateSelective() {

    }

    /**
     * Init insert batch.
     *
     * @param areaName     the area name
     * @param mappingField the mapping field
     */
    public void initUpdateSelective(String areaName, List<TxField> mappingField) {
        String newAreaName = getNewAreaName(areaName);
        // insertBatch
        ResultAppenderFactory appenderFactory = getResultAppenderFactory(mappingField, newAreaName);
        // insert + Batch
        final SyntaxAppender batchAppender =
            CustomAreaAppender.createCustomAreaAppender(newAreaName,
                ResultAppenderFactory.RESULT,
                AreaSequence.AREA,
                AreaSequence.RESULT,
                appenderFactory);
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
        return new UpdateSelectiveResultAppenderFactory(newAreaName) {
            @Override
            public String getTemplateText(String tableName,
                                          PsiClass entityClass,
                                          LinkedList<TxParameter> parameters,
                                          LinkedList<SyntaxAppenderWrapper> collector,
                                          ConditionFieldWrapper conditionFieldWrapper) {
                // 定制参数
                SyntaxAppender suffixOperator = InsertCustomSuffixAppender.createInsertBySuffixOperator(batchName(),
                    getSuffixOperator(mappingField),
                    AreaSequence.RESULT);
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
        return "Selective";
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
        return new UpdateSelectiveSuffixOperator(mappingField);
    }

    @Override
    public StatementBlock getStatementBlock() {
        return statementBlock;
    }

    @Override
    public String operatorName() {
        return operatorName;
    }


    private class UpdateSelectiveResultAppenderFactory extends ResultAppenderFactory {

        /**
         * Instantiates a new Insert batch result appender factory.
         *
         * @param areaPrefix the area prefix
         */
        public UpdateSelectiveResultAppenderFactory(String areaPrefix) {
            super(areaPrefix);
        }

        @Override
        public String getTemplateText(String tableName,
                                      PsiClass entityClass,
                                      LinkedList<TxParameter> parameters,
                                      LinkedList<SyntaxAppenderWrapper> collector, ConditionFieldWrapper conditionFieldWrapper) {
            StringBuilder mapperXml = new StringBuilder("update " + tableName + "\n");
            for (SyntaxAppenderWrapper syntaxAppenderWrapper : collector) {
                String templateText = syntaxAppenderWrapper.getAppender().getTemplateText(tableName, entityClass, parameters, collector, conditionFieldWrapper);
                mapperXml.append(templateText);
            }
            return mapperXml.toString();
        }

        @Override
        public List<TxParameter> getMxParameter(PsiClass entityClass, LinkedList<SyntaxAppenderWrapper> jpaStringList) {
            // 遍历定义的类型
            String defineName = entityClass.getName();
            // 变量名称
            String variableName = StringUtils.lowerCaseFirstChar(entityClass.getName());

            List<String> importClass = new ArrayList<>();
            importClass.add(entityClass.getQualifiedName());
            TxParameter parameter = TxParameter.createByOrigin(variableName, defineName, entityClass.getName(), false, importClass);
            return Collections.singletonList(parameter);
        }
    }


    /**
     * 批量插入
     */
    private class UpdateSelectiveSuffixOperator implements SuffixOperator {

        private List<TxField> mappingField;

        /**
         * Instantiates a new Insert batch suffix operator.
         *
         * @param mappingField the mapping field
         */
        public UpdateSelectiveSuffixOperator(List<TxField> mappingField) {
            this.mappingField = mappingField;
        }

        @Override
        public String getTemplateText(String fieldName, LinkedList<TxParameter> parameters, ConditionFieldWrapper conditionFieldWrapper) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<set>").append("\n");
            final TxParameter parameter = parameters.poll();
            final String selectiveItems = mappingField.stream()
                // 不是主键的就加到条件列表
                .filter(field -> !field.getPrimaryKey())
                .distinct()
                .map(field -> {
                    String fieldValue = JdbcTypeUtils.wrapperField(field.getFieldName(), field.getFieldType());
                    fieldValue = conditionFieldWrapper.wrapDefaultDateIfNecessary(field.getColumnName(), fieldValue);
                    return "<if test=\"" + field.getFieldName() + " != null\">" + field.getFieldName() + "=" + fieldValue + ",</if>";
                })
                .collect(Collectors.joining("\n"));
            stringBuilder.append(selectiveItems);
            stringBuilder.append("</set>").append("\n");
            // 条件
            final String conditionText = mappingField.stream().filter(TxField::getPrimaryKey).map(field -> {
                String fieldValue = JdbcTypeUtils.wrapperField(field.getFieldName(), field.getFieldType());
                fieldValue = conditionFieldWrapper.wrapDefaultDateIfNecessary(field.getColumnName(), fieldValue);
                return field.getFieldName() + " = " + fieldValue;
            }).collect(Collectors.joining("\n"));
            stringBuilder.append("where ").append(conditionText);

            return stringBuilder.toString();
        }

    }
}
