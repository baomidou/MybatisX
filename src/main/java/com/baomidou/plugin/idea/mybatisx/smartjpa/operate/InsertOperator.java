package com.baomidou.plugin.idea.mybatisx.smartjpa.operate;


import com.baomidou.plugin.idea.mybatisx.generate.AbstractStatementGenerator;
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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class InsertOperator extends BaseOperatorManager {


    public InsertOperator(final List<TxField> mappingField) {
        Set<String> patterns = AbstractStatementGenerator.INSERT_GENERATOR.getPatterns();
        this.init(mappingField, patterns);
    }


    public void init(final List<TxField> mappingField, Set<String> patterns) {
        for (final String areaName : patterns) {
            // insertSelective
            this.initInsertSelectiveAppender(areaName, mappingField);
            // insertOne
            this.initInsertAllAppender(areaName, mappingField);
            // insertBatch
            this.initInsertBatch(areaName, mappingField);
        }
    }

    private void initInsertBatch(String areaName, List<TxField> mappingField) {
        String newAreaName = areaName + "Batch";
        // insertBatch
        ResultAppenderFactory appenderFactory = new InsertBatchResultAppenderFactory(newAreaName) {
            @Override
            public String getTemplateText(String tableName, PsiClass entityClass, LinkedList<PsiParameter> parameters, LinkedList<SyntaxAppenderWrapper> collector, MybatisXmlGenerator mybatisXmlGenerator) {
                // 定制参数
                SyntaxAppender suffixOperator = InsertCustomSuffixAppender.createInsertBySuffixOperator("Batch",
                    new InsertBatchSuffixOperator(mappingField),
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
        this.registerStatementBlock(statementBlock);

        this.addOperatorName(newAreaName);
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
            StringBuilder mapperXml = new StringBuilder();
            mapperXml.append("insert into " + tableName).append("\n");
            for (SyntaxAppenderWrapper syntaxAppenderWrapper : collector) {
                String templateText = syntaxAppenderWrapper.getAppender().getTemplateText(tableName, entityClass, parameters, collector, mybatisXmlGenerator);
                mapperXml.append(templateText);
            }
            return mapperXml.toString();
        }

        @Override
        public List<TxParameter> getMxParameter(PsiClass entityClass, LinkedList<SyntaxAppender> jpaStringList) {
            String defineName = entityClass.getName();
            String variableName = StringUtils.lowerCaseFirstChar(defineName);
            TxParameter parameter = TxParameter.createByOrigin(variableName, defineName, entityClass.getQualifiedName(), false);
            return Collections.singletonList(parameter);
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


    private void initInsertSelectiveAppender(String areaName, List<TxField> mappingField) {
        String newAreaName = areaName + "Selective";
        final ResultAppenderFactory insertResultAppenderFactory = new InsertResultAppenderFactory(newAreaName) {
            @Override
            public String getTemplateText(String tableName,
                                          PsiClass entityClass,
                                          LinkedList<PsiParameter> parameters,
                                          LinkedList<SyntaxAppenderWrapper> collector, MybatisXmlGenerator mybatisXmlGenerator) {
                // 定制参数
                SyntaxAppender selective = InsertCustomSuffixAppender.createInsertBySuffixOperator("Selective",
                    new InsertSelectiveSuffixOperator(mappingField),
                    AreaSequence.RESULT);
                LinkedList<SyntaxAppenderWrapper> syntaxAppenderWrappers = new LinkedList<>();
                syntaxAppenderWrappers.add(new SyntaxAppenderWrapper(selective));
                return super.getTemplateText(tableName, entityClass, parameters, syntaxAppenderWrappers, mybatisXmlGenerator);
            }
        };

        // insert + Selective
        final SyntaxAppender selectiveAppender =
            CustomAreaAppender.createCustomAreaAppender(newAreaName, ResultAppenderFactory.RESULT, AreaSequence.AREA, AreaSequence.RESULT, insertResultAppenderFactory);
        insertResultAppenderFactory.registerAppender(selectiveAppender);

        StatementBlock statementBlock = new StatementBlock();
        statementBlock.setResultAppenderFactory(insertResultAppenderFactory);
        statementBlock.setTagName(newAreaName);
        statementBlock.setReturnWrapper(TxReturnDescriptor.createByOrigin(null, "int"));
        this.registerStatementBlock(statementBlock);

        this.addOperatorName(newAreaName);
    }


    private void initInsertAllAppender(final String areaName, List<TxField> mappingField) {
        String newAreaName = areaName + "All";
        final ResultAppenderFactory insertResultAppenderFactory = new InsertResultAppenderFactory(newAreaName) {
            @Override
            public String getTemplateText(String tableName,
                                          PsiClass entityClass,
                                          LinkedList<PsiParameter> parameters,
                                          LinkedList<SyntaxAppenderWrapper> collector, MybatisXmlGenerator mybatisXmlGenerator) {
                // 定制参数
                SyntaxAppender insertAll = InsertCustomSuffixAppender.createInsertBySuffixOperator("All",
                    new InsertAllSuffixOperator(mappingField),
                    AreaSequence.RESULT);
                LinkedList<SyntaxAppenderWrapper> syntaxAppenderWrappers = new LinkedList<>();
                syntaxAppenderWrappers.add(new SyntaxAppenderWrapper(insertAll));
                return super.getTemplateText(tableName, entityClass, parameters, syntaxAppenderWrappers, mybatisXmlGenerator);
            }


        };
        // insert + All
        final SyntaxAppender allAppender =
            CustomAreaAppender.createCustomAreaAppender(newAreaName, ResultAppenderFactory.RESULT,
                AreaSequence.AREA, AreaSequence.RESULT, insertResultAppenderFactory);
        insertResultAppenderFactory.registerAppender(allAppender);

        StatementBlock statementBlock = new StatementBlock();
        statementBlock.setResultAppenderFactory(insertResultAppenderFactory);
        statementBlock.setTagName(newAreaName);
        statementBlock.setReturnWrapper(TxReturnDescriptor.createByOrigin(null, "int"));
        this.registerStatementBlock(statementBlock);

        this.addOperatorName(newAreaName);
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

    private class InsertAllSuffixOperator implements SuffixOperator {

        private List<TxField> mappingField;

        public InsertAllSuffixOperator(List<TxField> mappingField) {
            this.mappingField = mappingField;
        }

        @Override
        public String getTemplateText(String fieldName, LinkedList<PsiParameter> parameters) {
            PsiParameter parameter = parameters.poll();
            StringBuilder stringBuilder = new StringBuilder();
            // 追加列名
            final String columns = mappingField.stream()
                .map(TxField::getColumnName)
                .collect(Collectors.joining(",\n"));
            stringBuilder.append("(").append(columns).append(")").append("\n");
            // values 连接符
            stringBuilder.append("values").append("\n");
            final String fields = mappingField.stream()
                .map(field -> JdbcTypeUtils.wrapperField(field.getFieldName(), field.getFieldType()))
                .collect(Collectors.joining(",\n"));
            stringBuilder.append("(\n");
            stringBuilder.append(fields).append("\n");
            stringBuilder.append(")").append("\n");

            return stringBuilder.toString();
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
                .collect(Collectors.joining("\n"));

            stringBuilder.append(trimFieldStart()).append(columns).append(trimEnd()).append("\n");
            // values 连接符
            stringBuilder.append("values").append("\n");
            final String fields = mappingField.stream()
                .map(field -> selective(field.getFieldName(), JdbcTypeUtils.wrapperField(field.getFieldName(), field.getFieldType())))
                .collect(Collectors.joining("\n"));

            stringBuilder.append(trimFieldStart());
            stringBuilder.append(fields).append("\n");
            stringBuilder.append(trimEnd());

            return stringBuilder.toString();
        }

        private String trimEnd() {
            return "</trim>";
        }

        private String trimFieldStart() {
            return "<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">";
        }

        private String selective(String paramName, String origin) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<if test=\"").append(paramName).append("!= null").append("\">");
            stringBuilder.append(origin);
            stringBuilder.append(",");
            stringBuilder.append("</if>");
            return stringBuilder.toString();
        }


    }

    private static final Logger logger = LoggerFactory.getLogger(InsertOperator.class);
}
