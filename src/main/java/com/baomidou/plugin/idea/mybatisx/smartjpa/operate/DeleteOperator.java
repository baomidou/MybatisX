package com.baomidou.plugin.idea.mybatisx.smartjpa.operate;






import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.factory.ConditionAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.factory.ResultAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxReturnDescriptor;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.MybatisXmlGenerator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager.StatementBlock;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.SyntaxAppenderWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

import java.util.LinkedList;
import java.util.List;

public class DeleteOperator extends BaseOperatorManager {


    private static final String DELETE = "delete";


    public DeleteOperator(final List<TxField> mappingField) {
        this.init(mappingField);
        super.setOperatorNameList(DELETE);
    }


    public void init(final List<TxField> mappingField) {
        // 没有结果集字段
        final ResultAppenderFactory resultAppenderFactory = new DeleteResultAppenderFactory();
//        resultAppenderFactory.registerAppender(new CustomAreaAppender(DeleteOperator.DELETE, "Result", resultAppenderFactory));
        StatementBlock statementBlock = new StatementBlock();
        statementBlock.setResultAppenderFactory(resultAppenderFactory);
        statementBlock.setTagName(getTagName());
        statementBlock.setConditionAppenderFactory(new ConditionAppenderFactory(DeleteOperator.DELETE, mappingField));
        this.registerStatementBlock(statementBlock);
    }

    private class DeleteResultAppenderFactory extends ResultAppenderFactory {

        public DeleteResultAppenderFactory() {
            super(DeleteOperator.DELETE);
        }

        @Override
        public String getTemplateText(String tableName, PsiClass entityClass,
                                      LinkedList<PsiParameter> parameters,
                                      LinkedList<SyntaxAppenderWrapper> collector,
                                      MybatisXmlGenerator mybatisXmlGenerator) {


            return "delete from " + tableName;
        }
    }

    @Override
    public TxReturnDescriptor getReturnWrapper(String text, PsiClass entityClass, LinkedList<SyntaxAppender> linkedList) {
        return TxReturnDescriptor.createByOrigin(null, "int");
    }

    @Override
    public String getTagName() {
        return "delete";
    }

    @Override
    public void generateMapperXml(String id, LinkedList<SyntaxAppender> jpaList, PsiClass entityClass, PsiMethod psiMethod, String tableName, MybatisXmlGenerator mybatisXmlGenerator) {
        String mapperXml = super.generateXml(id, jpaList, entityClass, psiMethod, tableName, mybatisXmlGenerator);
        mybatisXmlGenerator.generateDelete(id, mapperXml);
    }
}
