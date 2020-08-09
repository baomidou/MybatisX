package com.baomidou.plugin.idea.mybatisx.smartjpa.operate;






import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.*;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.operator.suffix.SuffixOperator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.factory.ResultAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.completion.parameter.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.completion.res.ReturnWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager.StatementBlock;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.TreeWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class InsertOperator extends BaseOperatorManager {


    public InsertOperator(final List<TxField> mappingField) {
        this.setOperatorNameList("insert");
        this.init(mappingField);
    }


    public void init(final List<TxField> mappingField) {
        for (final String areaName : this.getOperatorNameList()) {
            final ResultAppenderFactory insertResultAppenderFactory = new InsertResultAppenderFactory(areaName);
            this.initResultAppender(insertResultAppenderFactory, areaName, mappingField);
            StatementBlock statementBlock = new StatementBlock();
            statementBlock.setResultAppenderFactory(insertResultAppenderFactory);
            statementBlock.setTagName(getTagName());
            this.registerStatementBlock(statementBlock);

        }
    }

    private class InsertResultAppenderFactory extends ResultAppenderFactory {

        public InsertResultAppenderFactory(String areaPrefix) {
            super(areaPrefix);
        }

        @Override
        public String getTemplateText(String tableName, PsiClass entityClass, LinkedList<PsiParameter> parameters, LinkedList<TreeWrapper<SyntaxAppender>> collector) {
            return "insert into " + tableName;
        }

    }


    private void initResultAppender(final ResultAppenderFactory insertFactory, final String areaName, List<TxField> mappingField) {

        // insert + Batch
        final CompositeAppender batchAppender =
                new CompositeAppender(
                        CustomAreaAppender.createCustomAreaAppender(areaName, ResultAppenderFactory.RESULT, AreaSequence.AREA, AreaSequence.RESULT, insertFactory),
                        CustomSuffixAppender.createBySuffixOperator("Batch", new InsertBatchSuffixOperator(mappingField),AreaSequence.RESULT)
                );
        insertFactory.registerAppender(batchAppender);
        // insert + Selective
        final CompositeAppender selectiveAppender =
                new CompositeAppender(
                        CustomAreaAppender.createCustomAreaAppender(areaName, ResultAppenderFactory.RESULT, AreaSequence.AREA, AreaSequence.RESULT, insertFactory),
                        CustomSuffixAppender.createBySuffixOperator("Selective", new InsertSelectiveSuffixOperator(mappingField),AreaSequence.RESULT)
                );
        insertFactory.registerAppender(selectiveAppender);
        // insert + All
        final CompositeAppender allAppender =
                new CompositeAppender(
                        CustomAreaAppender.createCustomAreaAppender(areaName, ResultAppenderFactory.RESULT, AreaSequence.AREA, AreaSequence.RESULT, insertFactory),
                        CustomSuffixAppender.createBySuffixOperator("All", new InsertAllSuffixOperator(mappingField),AreaSequence.RESULT)
                );
        insertFactory.registerAppender(allAppender);

    }

    @Override
    public ReturnWrapper getReturnWrapper(String text, PsiClass entityClass, LinkedList<SyntaxAppender> linkedList) {
        return ReturnWrapper.createByOrigin(null, "int");
    }



    @Override
    public String getTagName() {
        return "insert";
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
            // 追加列名
            final String columns = mappingField.stream()
                    .map(field -> field.getColumnName())
                    .collect(Collectors.joining(","));
            stringBuilder.append("(").append(columns).append(")").append("\n");
            // values 连接符
            stringBuilder.append("values").append("\n");
            final PsiParameter collection = parameters.poll();
            final String collectionName = collection.getName();
            final String fields = mappingField.stream()
                    .map(field -> FieldWrapperUtils.wrapperField(field.getFieldName(), field.getFieldType()))
                    .collect(Collectors.joining(","));

            stringBuilder.append("<foreach collection=\"").append(collectionName).append("\" item=\"item\" separator=\",\" open=\"(\" close=\")\">").append("\n");
            stringBuilder.append(fields).append("\n");
            stringBuilder.append("</foreach>");

            return stringBuilder.toString();
        }

        @Override
        public boolean needField() {
            return false;
        }
    }

    private class InsertAllSuffixOperator implements SuffixOperator {

        private List<TxField> mappingField;

        public InsertAllSuffixOperator(List<TxField> mappingField) {
            this.mappingField = mappingField;
        }

        @Override
        public String getTemplateText(String fieldName, LinkedList<PsiParameter> parameters) {
            StringBuilder stringBuilder = new StringBuilder();
            // 追加列名
            final String columns = mappingField.stream()
                    .map(field -> field.getColumnName())
                    .collect(Collectors.joining(","));
            stringBuilder.append("(").append(columns).append(")").append("\n");
            // values 连接符
            stringBuilder.append("values").append("\n");
            final PsiParameter collection = parameters.poll();
            final String collectionName = collection.getName();
            final String fields = mappingField.stream()
                    .map(field -> FieldWrapperUtils.wrapperField(field.getFieldName(), field.getFieldType()))
                    .collect(Collectors.joining(","));
            stringBuilder.append("(");
            stringBuilder.append(fields).append("\n");
            stringBuilder.append(")");

            return stringBuilder.toString();
        }

        @Override
        public boolean needField() {
            return false;
        }
    }

    private class InsertSelectiveSuffixOperator implements SuffixOperator {

        private List<TxField> mappingField;

        public InsertSelectiveSuffixOperator(List<TxField> mappingField) {
            this.mappingField = mappingField;
        }

        @Override
        public String getTemplateText(String fieldName, LinkedList<PsiParameter> parameters) {
            StringBuilder stringBuilder = new StringBuilder();
            // 追加列名
            final String columns = mappingField.stream()
                    .map(field -> selective(field.getFieldName(), field.getColumnName()))
                    .collect(Collectors.joining(","));
            stringBuilder.append("(").append(columns).append(")").append("\n");
            // values 连接符
            stringBuilder.append("values").append("\n");
            final PsiParameter collection = parameters.poll();
            final String fields = mappingField.stream()
                    .map(field -> selective(field.getFieldName(), FieldWrapperUtils.wrapperField(field.getFieldName(), field.getFieldType())))
                    .collect(Collectors.joining(","));

            stringBuilder.append("(");
            stringBuilder.append(fields).append("\n");
            stringBuilder.append(")");

            return stringBuilder.toString();
        }

        private String selective(String paramName, String origin) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<if test=\"").append(paramName).append("!= null").append("\">");
            stringBuilder.append(origin);
            stringBuilder.append("</if>");
            return stringBuilder.toString();
        }

        @Override
        public boolean needField() {
            return false;
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(InsertOperator.class);
}
