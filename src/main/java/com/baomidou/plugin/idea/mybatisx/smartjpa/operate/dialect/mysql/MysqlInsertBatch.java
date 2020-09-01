package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.dialect.mysql;

import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.AreaSequence;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomAreaAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomSuffixAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.JdbcTypeUtils;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.operator.suffix.SuffixOperator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.factory.ResultAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxReturnDescriptor;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.InsertOperator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.dialect.CustomStatement;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.MybatisXmlGenerator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager.StatementBlock;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.StringUtils;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.SyntaxAppenderWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiParameter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class MysqlInsertBatch implements CustomStatement {

    public MysqlInsertBatch(String areaName, List<TxField> mappingField) {
        initInsertBatch(areaName, mappingField);
    }

    private void initInsertBatch(String areaName, List<TxField> mappingField) {
        String newAreaName = areaName + "Batch";
        // insertBatch
        ResultAppenderFactory appenderFactory = new InsertBatchResultAppenderFactory(newAreaName) {
            @Override
            public String getTemplateText(String tableName, PsiClass entityClass, LinkedList<PsiParameter> parameters, LinkedList<SyntaxAppenderWrapper> collector, MybatisXmlGenerator mybatisXmlGenerator) {
                // 定制参数
                SyntaxAppender suffixOperator = InsertCustomSuffixAppender.createInsertBySuffixOperator("Batch",
                    getSuffixOperator(mappingField),
                    AreaSequence.RESULT);
                LinkedList<SyntaxAppenderWrapper> syntaxAppenderWrappers = new LinkedList<>();
                syntaxAppenderWrappers.add(new SyntaxAppenderWrapper(suffixOperator));
                return super.getTemplateText(tableName, entityClass, parameters, syntaxAppenderWrappers, mybatisXmlGenerator);
            }
        };
        // insert + Batch
        final SyntaxAppender batchAppender =
            CustomAreaAppender.createCustomAreaAppender(newAreaName, ResultAppenderFactory.RESULT, AreaSequence.AREA, AreaSequence.RESULT, appenderFactory);
        appenderFactory.registerAppender(batchAppender);

        StatementBlock statementBlock = new StatementBlock();
        statementBlock.setResultAppenderFactory(appenderFactory);
        statementBlock.setTagName(newAreaName);
        statementBlock.setReturnWrapper(TxReturnDescriptor.createByOrigin(null, "int"));
        this.statementBlock = statementBlock;

        this.operatorName = newAreaName;
    }

    @NotNull
    protected SuffixOperator getSuffixOperator(List<TxField> mappingField) {
        return new InsertBatchSuffixOperator(mappingField);
    }

    StatementBlock statementBlock;

    String operatorName;
    @Override
    public StatementBlock getStatementBlock() {
        return statementBlock;
    }

    @Override
    public String operatorName() {
        return operatorName;
    }


    private class InsertBatchResultAppenderFactory extends ResultAppenderFactory {

        public InsertBatchResultAppenderFactory(String areaPrefix) {
            super(areaPrefix);
        }

        @Override
        public String getTemplateText(String tableName,
                                      PsiClass entityClass,
                                      LinkedList<PsiParameter> parameters,
                                      LinkedList<SyntaxAppenderWrapper> collector,
                                      MybatisXmlGenerator mybatisXmlGenerator) {
            StringBuilder mapperXml = new StringBuilder("insert into " + tableName);
            for (SyntaxAppenderWrapper syntaxAppenderWrapper : collector) {
                String templateText = syntaxAppenderWrapper.getAppender().getTemplateText(tableName, entityClass, parameters, collector, mybatisXmlGenerator);
                mapperXml.append(templateText);
            }
            return mapperXml.toString();
        }

        @Override
        public List<TxParameter> getMxParameter(PsiClass entityClass, LinkedList<SyntaxAppender> jpaStringList) {
            // 遍历定义的类型
            String defineName = Collection.class.getSimpleName() + "<" + entityClass.getName() + ">";
            // 变量名称
            String variableName = StringUtils.lowerCaseFirstChar(entityClass.getName()) + "Collection";

            TxParameter parameter = TxParameter.createByOrigin(variableName, defineName, Collection.class.getName());
            return Arrays.asList(parameter);
        }
    }

    static class InsertCustomSuffixAppender extends CustomSuffixAppender {

        public InsertCustomSuffixAppender(String tipName, SuffixOperator suffixOperator, AreaSequence areaSequence) {
            super(tipName, suffixOperator, areaSequence);
        }

        public static SyntaxAppender createInsertBySuffixOperator(String all, SuffixOperator suffixOperator, AreaSequence areaSequence) {
            return new InsertCustomSuffixAppender(all, suffixOperator, areaSequence);
        }

        @Override
        public void toTree(LinkedList<SyntaxAppender> jpaStringList, SyntaxAppenderWrapper syntaxAppenderWrapper) {
            syntaxAppenderWrapper.addWrapper(new SyntaxAppenderWrapper(this));
        }

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
            stringBuilder.append("values").append("\n");
            final PsiParameter collection = parameters.poll();
            final String collectionName = collection.getName();
            final String fields = mappingField.stream()
                .map(field -> JdbcTypeUtils.wrapperField(itemName + "." + field.getFieldName(), field.getFieldType()))
                .collect(Collectors.joining(",\n"));

            stringBuilder.append("<foreach collection=\"").append(collectionName).append("\"");
            stringBuilder.append(" item=\"" + itemName + "\"");
            stringBuilder.append(" separator=\",\">").append("\n");
            stringBuilder.append("(").append(fields).append(")").append("\n");
            stringBuilder.append("</foreach>");

            return stringBuilder.toString();
        }

    }
}
