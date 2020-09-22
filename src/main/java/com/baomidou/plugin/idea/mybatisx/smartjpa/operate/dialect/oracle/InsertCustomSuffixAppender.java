package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.dialect.oracle;

import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.AreaSequence;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomSuffixAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.operator.suffix.SuffixOperator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.SyntaxAppenderWrapper;

import java.util.LinkedList;

/**
 * 插入后缀处理器
 */
public class InsertCustomSuffixAppender extends CustomSuffixAppender {

    /**
     * Instantiates a new Insert custom suffix appender.
     *
     * @param tipName        the tip name
     * @param suffixOperator the suffix operator
     * @param areaSequence   the area sequence
     */
    public InsertCustomSuffixAppender(String tipName, SuffixOperator suffixOperator, AreaSequence areaSequence) {
        super(tipName, suffixOperator, areaSequence);
    }

    /**
     * Create insert by suffix operator syntax appender.
     *
     * @param all            the all
     * @param suffixOperator the suffix operator
     * @param areaSequence   the area sequence
     * @return the syntax appender
     */
    public static SyntaxAppender createInsertBySuffixOperator(String all, SuffixOperator suffixOperator, AreaSequence areaSequence) {
        return new InsertCustomSuffixAppender(all, suffixOperator, areaSequence);
    }

    @Override
    public void toTree(LinkedList<SyntaxAppender> jpaStringList, SyntaxAppenderWrapper syntaxAppenderWrapper) {
        syntaxAppenderWrapper.addWrapper(new SyntaxAppenderWrapper(InsertCustomSuffixAppender.this));
    }

}
