package com.baomidou.plugin.idea.mybatisx.smartjpa.operate;






import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.factory.ConditionAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.factory.ResultAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.completion.parameter.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.completion.res.ReturnWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager.StatementBlock;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.TreeWrapper;
import com.intellij.psi.PsiClass;
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
                                      LinkedList<TreeWrapper<SyntaxAppender>> collector) {
            return "delete from " + tableName;
        }
    }

    @Override
    public ReturnWrapper getReturnWrapper(String text, PsiClass entityClass, LinkedList<SyntaxAppender> linkedList) {
        return ReturnWrapper.createByOrigin(null, "int");
    }

    @Override
    public String getTagName() {
        return "delete";
    }
}
