package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.dialect.oracle;

import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.JdbcTypeUtils;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.operator.suffix.SuffixOperator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.dialect.mysql.MysqlInsertBatch;
import com.intellij.database.model.DasObject;
import com.intellij.database.model.DasTable;
import com.intellij.database.model.DasTableKey;
import com.intellij.database.model.ObjectKind;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.psi.DbElement;
import com.intellij.database.util.DasUtil;
import com.intellij.database.util.DbUtil;
import com.intellij.psi.PsiParameter;
import com.intellij.util.containers.JBIterable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * oracle的批量插入
 */
public class OracleInsertBatch extends MysqlInsertBatch {


    private DasTable dasTable;
    private String tableName;

    public OracleInsertBatch(DasTable dasTable, String tableName) {
        this.dasTable = dasTable;
        this.tableName = tableName;

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
// TODO oracle 批量插入选择合适的序列

            Optional<String> sequenceName = findSequenceName();

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
                .map(field -> {
                    String fieldStr = JdbcTypeUtils.wrapperField(itemName + "." + field.getFieldName(), field.getFieldType());
                    if (sequenceName.isPresent() && dasTable != null) {
                        DasTableKey primaryKey = DasUtil.getPrimaryKey(dasTable);
                        // 当前字段是主键, 使用自定义函数替换主键
                        if (primaryKey != null && primaryKey.getColumnsRef().size() == 1) {
                            String pkFieldName = primaryKey.getColumnsRef().iterate().next();
                            if (pkFieldName.equals(field.getColumnName())) {
                                fieldStr = "GET_SEQ_NO('" + sequenceName.get() + "')";
                            }
                        }
                    }
                    return fieldStr;
                })
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

    private Optional<String> findSequenceName() {
        String foundSequenceName = null;
        if (dasTable != null) {
            DasObject schema = dasTable.getDasParent();
            PriorityQueue<String> sequenceQueue = new PriorityQueue<>(Comparator.comparing(String::length, Comparator.reverseOrder()));

            JBIterable<? extends DasObject> sequences = schema.getDasChildren(ObjectKind.SEQUENCE);
            for (DasObject sequence : sequences) {
                String sequenceName = sequence.getName();
                if (sequenceName.contains(tableName)) {
                    sequenceQueue.add(sequenceName);
                }
            }
            if (sequenceQueue.size() > 0) {
                foundSequenceName = sequenceQueue.peek();
            }

        }
        return Optional.ofNullable(foundSequenceName);
    }
}
