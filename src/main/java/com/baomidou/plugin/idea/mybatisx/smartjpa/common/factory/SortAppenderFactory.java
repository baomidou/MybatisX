package com.baomidou.plugin.idea.mybatisx.smartjpa.common.factory;


import com.baomidou.plugin.idea.mybatisx.smartjpa.common.BaseAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.AreaSequence;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CompositeAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomAreaAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomFieldAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomJoinAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomSuffixAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest.ConditionFieldWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.model.AppendTypeEnum;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.SyntaxAppenderWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiParameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 排序区
 */
public class SortAppenderFactory extends BaseAppenderFactory {
    private final List<TxField> mappingField;

    /**
     * Instantiates a new Sort appender factory.
     *
     * @param mappingField the mapping field
     */
    public SortAppenderFactory(final List<TxField> mappingField) {
        this.mappingField = mappingField;
    }

    @Override
    public List<SyntaxAppender> getSyntaxAppenderList() {
        final List<SyntaxAppender> syntaxAppenderArrayList = new ArrayList<>();
        // order by field : desc
        syntaxAppenderArrayList.add(CustomSuffixAppender.createByFixed("Desc", "desc", AreaSequence.SORT));
        for (final TxField field : this.mappingField) {
            // order by: field
            final SyntaxAppender appender = new CompositeAppender(
                    CustomAreaAppender.createCustomAreaAppender(this.getTipText(), getTipText(), AreaSequence.AREA, AreaSequence.SORT, this),
                    new SortCustomFieldAppender(field, AreaSequence.SORT));
            syntaxAppenderArrayList.add(appender);
            // order by: and field
            final CompositeAppender andAppender = new CompositeAppender(new CustomJoinAppender("And", ",", AreaSequence.SORT),
                    new SortCustomFieldAppender(field, AreaSequence.SORT));
            syntaxAppenderArrayList.add(andAppender);
        }
        return syntaxAppenderArrayList;
    }

    @Override
    public String getTipText() {
        return "OrderBy";
    }

    @Override
    public List<TxParameter> getMxParameter(PsiClass entityClass, LinkedList<SyntaxAppender> jpaStringList) {
        return Collections.emptyList();
    }


    private class SortCustomFieldAppender extends CustomFieldAppender{

        /**
         * Instantiates a new Sort custom field appender.
         *
         * @param field the field
         * @param sort  the sort
         */
        public SortCustomFieldAppender(TxField field, AreaSequence sort) {
            super(field, sort);
        }

        @Override
        public String getTemplateText(String tableName,
                                      PsiClass entityClass,
                                      LinkedList<PsiParameter> parameters,
                                      LinkedList<SyntaxAppenderWrapper> collector, ConditionFieldWrapper conditionFieldWrapper) {
            return getFieldName();
        }
    }

    @Override
    public String getTemplateText(String tableName, PsiClass entityClass, LinkedList<PsiParameter> parameters, LinkedList<SyntaxAppenderWrapper> collector, ConditionFieldWrapper conditionFieldWrapper) {
        StringBuilder stringBuilder = new StringBuilder();
        for (SyntaxAppenderWrapper syntaxAppender : collector) {
            String templateText = syntaxAppender.getAppender().getTemplateText(tableName, entityClass, parameters, syntaxAppender.getCollector(), conditionFieldWrapper);
            stringBuilder.append(templateText).append(" ");
        }
        return "order by " + stringBuilder.toString();
    }


    @Override
    public void appendDefault(SyntaxAppender syntaxAppender, LinkedList<SyntaxAppender> current) {
        if (syntaxAppender.getType() == AppendTypeEnum.FIELD) {
            current.addLast(CustomSuffixAppender.createByFixed("Asc", "asc", AreaSequence.SORT));
        }
    }


    @Override
    protected AreaSequence getAreaSequence() {
        return AreaSequence.SORT;
    }
}
