package com.baomidou.plugin.idea.mybatisx.smartjpa.operate;


import com.baomidou.plugin.idea.mybatisx.generate.AbstractStatementGenerator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.AreaSequence;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CompositeAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomAreaAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomSuffixAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.FieldWrapperUtils;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.operator.suffix.SuffixOperator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.factory.ResultAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxReturnDescriptor;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.MybatisXmlGenerator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager.StatementBlock;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.StringUtils;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.SyntaxAppenderWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class InsertOperator extends BaseOperatorManager {


    public InsertOperator(final List<TxField> mappingField) {
        this.setOperatorNameList(AbstractStatementGenerator.INSERT_GENERATOR.getPatterns());
        this.init(mappingField);
    }


    public void init(final List<TxField> mappingField) {
        for (final String areaName : this.getOperatorNameList()) {
            // insertOne,insertSelective
            final ResultAppenderFactory insertResultAppenderFactory = new InsertResultAppenderFactory(areaName);
            this.initOneResultAppender(insertResultAppenderFactory, areaName, mappingField);
            StatementBlock statementBlock = new StatementBlock();
            statementBlock.setResultAppenderFactory(insertResultAppenderFactory);
            statementBlock.setTagName(areaName);
            this.registerStatementBlock(statementBlock);


            // insertBatch
            ResultAppenderFactory batch = new InsertBatchResultAppenderFactory(areaName);
            this.initBatchResultAppender(batch, areaName, mappingField);
            StatementBlock statementBlockBatch = new StatementBlock();
            statementBlockBatch.setResultAppenderFactory(batch);
            statementBlockBatch.setTagName(areaName);
            this.registerStatementBlock(statementBlockBatch);
        }
    }

    private class InsertResultAppenderFactory extends ResultAppenderFactory {

        public InsertResultAppenderFactory(String areaPrefix) {
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
            String name = entityClass.getName();
            String s = StringUtils.lowerCaseFirstChar(name);
            TxParameter parameter = TxParameter.createByOrigin(name, entityClass.getQualifiedName(), s);
            return Arrays.asList(parameter);
        }
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


    private void initOneResultAppender(final ResultAppenderFactory insertFactory, final String areaName, List<TxField> mappingField) {

        // insert + Selective
        final CompositeAppender selectiveAppender =
            new CompositeAppender(
                CustomAreaAppender.createCustomAreaAppender(areaName, ResultAppenderFactory.RESULT, AreaSequence.AREA, AreaSequence.RESULT, insertFactory),
                InsertCustomSuffixAppender.createInsertBySuffixOperator("Selective", new InsertSelectiveSuffixOperator(mappingField), AreaSequence.RESULT)
            );
        insertFactory.registerAppender(selectiveAppender);
        // insert + All
        final CompositeAppender allAppender =
            new CompositeAppender(
                CustomAreaAppender.createCustomAreaAppender(areaName, ResultAppenderFactory.RESULT, AreaSequence.AREA, AreaSequence.RESULT, insertFactory),
                InsertCustomSuffixAppender.createInsertBySuffixOperator("All", new InsertAllSuffixOperator(mappingField), AreaSequence.RESULT)
            );
        insertFactory.registerAppender(allAppender);

    }


    private void initBatchResultAppender(final ResultAppenderFactory insertFactory, final String areaName, List<TxField> mappingField) {

        // insert + Batch
        final CompositeAppender batchAppender =
            new CompositeAppender(
                CustomAreaAppender.createCustomAreaAppender(areaName, ResultAppenderFactory.RESULT, AreaSequence.AREA, AreaSequence.RESULT, insertFactory),
                InsertCustomSuffixAppender.createInsertBySuffixOperator("Batch", new InsertBatchSuffixOperator(mappingField), AreaSequence.RESULT)
            );
        insertFactory.registerAppender(batchAppender);

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

    @Override
    public TxReturnDescriptor getReturnWrapper(String text, PsiClass entityClass, LinkedList<SyntaxAppender> linkedList) {
        return TxReturnDescriptor.createByOrigin(null, "int");
    }


    @Override
    public String getTagName() {
        return "insert";
    }

    @Override
    public void generateMapperXml(String id, LinkedList<SyntaxAppender> jpaList, PsiClass entityClass, PsiMethod psiMethod, String tableName, MybatisXmlGenerator mybatisXmlGenerator) {
        String mapperXml = super.generateXml(jpaList, entityClass, psiMethod, tableName, mybatisXmlGenerator);
        mybatisXmlGenerator.generateInsert(id, mapperXml);
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
