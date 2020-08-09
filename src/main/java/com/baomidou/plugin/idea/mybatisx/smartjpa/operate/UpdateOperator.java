package com.baomidou.plugin.idea.mybatisx.smartjpa.operate;







import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.*;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.factory.ConditionAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.factory.ResultAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.completion.parameter.MxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.completion.parameter.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.completion.res.ReturnWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager.StatementBlock;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.TreeWrapper;
import com.github.hypfvieh.util.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiParameter;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class UpdateOperator extends BaseOperatorManager {


    public UpdateOperator(final List<TxField> mappingField) {
        this.setOperatorNameList("update,modify");
        this.init(mappingField);
    }

    public void init(final List<TxField> mappingField) {
        for (final String areaName : this.getOperatorNameList()) {
            final ResultAppenderFactory updateFactory = new UpdateResultAppenderFactory(areaName);
            this.initResultAppender(updateFactory, mappingField, areaName);

            StatementBlock statementBlock = new StatementBlock();
            statementBlock.setTagName(getTagName());
            statementBlock.setResultAppenderFactory(updateFactory);
            statementBlock.setConditionAppenderFactory(new ConditionAppenderFactory(areaName, mappingField));
            this.registerStatementBlock(statementBlock);
        }

    }

    private class UpdateResultAppenderFactory extends ResultAppenderFactory {

        public UpdateResultAppenderFactory(String areaPrefix) {
            super(areaPrefix);
        }

        @Override
        public String getTemplateText(String tableName, PsiClass entityClass, LinkedList<PsiParameter> parameters, LinkedList<TreeWrapper<SyntaxAppender>> collector) {
            return "update " + tableName + "\n set";
        }
    }


    private void initResultAppender(final ResultAppenderFactory updateFactory, final List<TxField> mappingField, final String areaName) {
        for (final TxField field : mappingField) {
            // field
            // and + field
            final CompositeAppender andAppender = new CompositeAppender(
                    new CustomJoinAppender("And", ",", AreaSequence.RESULT),
                    new CustomFieldAppender(field));
            updateFactory.registerAppender(andAppender);

            // update + field
            final CompositeAppender areaAppender =
                    new CompositeAppender(
                            CustomAreaAppender.createCustomAreaAppender(areaName, ResultAppenderFactory.RESULT, AreaSequence.AREA, AreaSequence.RESULT,updateFactory),
                            new CustomFieldAppender(field)
                    );
            updateFactory.registerAppender(areaAppender);

        }
    }

    @Override
    public ReturnWrapper getReturnWrapper(String text, PsiClass entityClass, LinkedList<SyntaxAppender> linkedList) {
        return ReturnWrapper.createByOrigin(null, "int");
    }

    @Override
    public List<MxParameter> getParameters(PsiClass entityClass, LinkedList<SyntaxAppender> jpaStringList) {
        List<MxParameter> parameters = super.getParameters(entityClass, jpaStringList);
        Set<String> collection = new HashSet<>();
        for (MxParameter parameter : parameters) {
            String name = parameter.getName();
            if (!collection.add(name)) {
                // old:   name.substring(0, 1).toUpperCase() + name.substring(1)
                String newName = "old" + StringUtil.upperCaseFirstChar(name);
                parameter.setName(newName);
            }
        }
        return parameters;
    }


    @Override
    public String getTagName() {
        return "update";
    }
}
