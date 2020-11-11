package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.appender;

import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.AreaSequence;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomFieldAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest.ConditionFieldWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.SyntaxAppenderWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiParameter;

import java.util.LinkedList;

/**
 * @author ls9527
 */
// 查询类型的结果集区域,  字段拼接部分, 只需要字段名称就可以了
public class SelectFieldAppender extends CustomFieldAppender {

    public SelectFieldAppender(TxField txField) {
        super(txField, AreaSequence.RESULT);
    }


    @Override
    public String getTemplateText(String tableName, PsiClass
        entityClass, LinkedList<PsiParameter> parameters, LinkedList<SyntaxAppenderWrapper> collector, ConditionFieldWrapper
                                      conditionFieldWrapper) {
        return columnName;
    }
}
