package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.dialect.oracle;

import androidx.annotation.NonNull;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.JdbcTypeUtils;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.operator.suffix.SuffixOperator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest.ConditionFieldWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.db.adaptor.DasTableAdaptor;
import com.baomidou.plugin.idea.mybatisx.smartjpa.exp.GenerateException;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.dialect.mysql.MysqlInsertBatch;
import com.baomidou.plugin.idea.mybatisx.util.MybatisXCollectors;
import com.intellij.database.model.DasTableKey;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * oracle的批量插入
 */
public class OracleInsertBatchWithUnion extends MysqlInsertBatch {


    private final DasTableAdaptor dasTable;
    private final String tableName;

    /**
     * Instantiates a new Oracle insert batch with union.
     *
     * @param dasTable  the das table
     * @param tableName the table name
     */
    public OracleInsertBatchWithUnion(DasTableAdaptor dasTable, String tableName) {
        this.dasTable = dasTable;
        this.tableName = tableName;

    }

    @Override
    @NotNull
    protected SuffixOperator getSuffixOperator(List<TxField> mappingField) {
        return new InsertBatchSuffixOperator(mappingField);
    }

    @Override
    protected @NotNull
    String batchName() {
        return "BatchWithUnion";
    }


    /**
     * 批量插入
     */
    private class InsertBatchSuffixOperator implements SuffixOperator {
        @NotNull
        private final List<TxField> mappingField;

        /**
         * Instantiates a new Insert batch suffix operator.
         *
         * @param mappingField the mapping field
         */
        public InsertBatchSuffixOperator(@NotNull List<TxField> mappingField) {
            this.mappingField = mappingField;
        }

        @Override
        public String getTemplateText(String fieldName, @NonNull LinkedList<TxParameter> parameters, ConditionFieldWrapper conditionFieldWrapper) {
            if (parameters.isEmpty()) {
                throw new GenerateException("oracle insertBatch 生成失败, parameters为空");
            }
            final TxParameter collection = parameters.poll();
            final String collectionName = collection.getName();
            Optional<String> sequenceName = dasTable.findSequenceName(tableName);

            // build columns
            final String columns = mappingField.stream()
                .map(TxField::getColumnName)
                .collect(MybatisXCollectors.joining(",", conditionFieldWrapper.getNewline()));

            // build field placeholder
            String itemName = "item";
            final String fields = mappingField.stream()
                .map(field -> {
                    String fieldStr = JdbcTypeUtils.wrapperField(itemName + "." + field.getFieldName(), field.getFieldType());
                    // 第一版写死字段变更, 后续重构
                    // 变更主键生成规则为自定义函数
                    if (sequenceName.isPresent()) {
                        DasTableKey primaryKey = dasTable.getPrimaryKey();
                        // 当前字段是主键, 使用自定义函数替换主键
                        if (primaryKey != null && primaryKey.getColumnsRef().size() == 1) {
                            String pkFieldName = primaryKey.getColumnsRef().iterate().next();
                            if (pkFieldName.equals(field.getColumnName())) {
                                fieldStr = "GET_SEQ_NO('" + sequenceName.get() + "')";
                            }
                        }
                    }
                    fieldStr = conditionFieldWrapper.wrapDefaultDateIfNecessary(field.getColumnName(), fieldStr);
                    return fieldStr;
                })
                .collect(MybatisXCollectors.joining(",", conditionFieldWrapper.getNewline()));

            return "(%s)\n(\n<foreach collection=\"%s\" item=\"%s\" separator=\"union all\">\nselect\n%s\nfrom dual\n</foreach>\n)"
                .formatted(columns, collectionName, itemName, fields);
        }

    }
}
