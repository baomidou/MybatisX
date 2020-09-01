package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.dialect.oracle;

import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.JdbcTypeUtils;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.operator.suffix.SuffixOperator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.dialect.mysql.MysqlInsertBatch;
import com.intellij.psi.PsiParameter;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * oracle的批量插入
 */
public class OracleInsertBatch extends MysqlInsertBatch {


    public OracleInsertBatch(String areaName, List<TxField> mappingField) {
        super(areaName, mappingField);
    }

    @NotNull
    protected SuffixOperator getSuffixOperator(List<TxField> mappingField) {
        return new InsertBatchSuffixOperator(mappingField);
    }

    /**
     * 批量插入
     */
    private class InsertBatchSuffixOperator implements SuffixOperator {

        private List<TxField> mappingField;

        public InsertBatchSuffixOperator(List<TxField> mappingField) {
            this.mappingField = mappingField;
        }

        @Override
        public String getTemplateText(String fieldName, LinkedList<PsiParameter> parameters) {
            StringBuilder stringBuilder = new StringBuilder();
            String itemName = "item";
            // 追加列名
            final String columns = mappingField.stream()
                .map(field -> field.getColumnName())
                .collect(Collectors.joining(",\n"));
            stringBuilder.append("(").append(columns).append(")").append("\n");
            // values 连接符
            stringBuilder.append("(").append("\n");
            final PsiParameter collection = parameters.poll();
            final String collectionName = collection.getName();
            final String fields = mappingField.stream()
                .map(field -> JdbcTypeUtils.wrapperField(itemName + "." + field.getFieldName(), field.getFieldType()))
                .collect(Collectors.joining(",\n"));

            stringBuilder.append("<foreach collection=\"").append(collectionName).append("\"");
            stringBuilder.append(" item=\"" + itemName + "\"");
            stringBuilder.append(" separator=\"union all\">").append("\n");
            stringBuilder.append("select").append("\n");
            stringBuilder.append(fields).append("\n").append("from dual").append("\n");
            stringBuilder.append("</foreach>").append("\n");
            stringBuilder.append(")");
            return stringBuilder.toString();
        }

    }
}
