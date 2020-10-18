package com.baomidou.plugin.idea.mybatisx.smartjpa.common.factory;






import com.baomidou.plugin.idea.mybatisx.smartjpa.common.BaseAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.AreaSequence;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomFieldAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest.ConditionFieldWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.model.AppendTypeEnum;
import com.intellij.psi.PsiClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 结果集追加
 */
public class ResultAppenderFactory extends BaseAppenderFactory {
    /**
     * The constant RESULT.
     */
// 区域类型
    public static final String RESULT = "Result";
    /**
     * 区域前缀
     */
    private final String areaPrefix;

    private final List<SyntaxAppender> syntaxAppenderList = new ArrayList<>();

    /**
     * Instantiates a new Result appender factory.
     *
     * @param areaPrefix the area prefix
     */
    public ResultAppenderFactory(final String areaPrefix) {
        this.areaPrefix = areaPrefix;
    }

    /**
     * Register appender.
     *
     * @param syntaxAppender the syntax appender
     */
    public void registerAppender(final SyntaxAppender syntaxAppender) {
        this.syntaxAppenderList.add(syntaxAppender);
    }


    @Override
    public List<TxParameter> getMxParameter(PsiClass entityClass, LinkedList<SyntaxAppender> jpaStringList) {
        SyntaxAppender peek = jpaStringList.poll();
        if (peek == null) {
            return Collections.emptyList();
        }

        LinkedList<TxParameter> txParameters = new LinkedList<>();

        while ((peek = jpaStringList.peek()) != null && peek.getType() != AppendTypeEnum.AREA) {
            txParameters.addAll(peek.getMxParameter(jpaStringList, entityClass));
        }
        return txParameters;
    }

    @Override
    public List<SyntaxAppender> getSyntaxAppenderList() {
        return this.syntaxAppenderList;
    }


    @Override
    public String getTipText() {
        return this.areaPrefix;
    }


    @Override
    protected AreaSequence getAreaSequence() {
        return AreaSequence.RESULT;
    }



    public static class WrapDateCustomFieldAppender extends CustomFieldAppender {


        /**
         * Instantiates a new Custom field appender.
         *
         * @param field        the field
         * @param areaSequence the area sequence
         */
        public WrapDateCustomFieldAppender(TxField field, AreaSequence areaSequence) {
            super(field, areaSequence);
        }


        @Override
        protected String wrapFieldValueInTemplateText(String columnName, ConditionFieldWrapper conditionFieldWrapper, String fieldValue) {
            return  conditionFieldWrapper.wrapDefaultDateIfNecessary(columnName, fieldValue);
        }
    }
}
