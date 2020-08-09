package com.baomidou.plugin.idea.mybatisx.smartjpa.common.factory;






import com.baomidou.plugin.idea.mybatisx.smartjpa.common.BaseAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.AreaSequence;
import com.baomidou.plugin.idea.mybatisx.smartjpa.completion.parameter.MxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.model.AppendTypeEnum;
import com.intellij.psi.PsiClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 结果集追加
 */
public class ResultAppenderFactory extends BaseAppenderFactory {
    // 区域类型
    public static final String RESULT = "Result";
    /**
     * 区域前缀
     */
    private final String areaPrefix;

    private final List<SyntaxAppender> syntaxAppenderList = new ArrayList<>();

    public ResultAppenderFactory(final String areaPrefix) {
        this.areaPrefix = areaPrefix;
    }

    public void registerAppender(final SyntaxAppender syntaxAppender) {
        this.syntaxAppenderList.add(syntaxAppender);
    }


    @Override
    public List<MxParameter> getMxParameter(PsiClass entityClass, LinkedList<SyntaxAppender> jpaStringList) {
        SyntaxAppender peek = jpaStringList.poll();
        if (peek == null) {
            return Collections.emptyList();
        }

        LinkedList<MxParameter> mxParameters = new LinkedList<>();

//        final List<SyntaxAppender> list = toTree(jpaStringList);
//        for (SyntaxAppender syntaxAppender : list) {
//            syntaxAppender.getMxParameter(jpaStringList,entityClass);
//        }
        while ((peek = jpaStringList.peek()) != null && peek.getType() != AppendTypeEnum.AREA) {
            mxParameters.addAll(peek.getMxParameter(jpaStringList, entityClass));
        }
        return mxParameters;
    }

    //    private List<SyntaxAppender> toTree(LinkedList<SyntaxAppender> jpaStringList) {
//        List<SyntaxAppender> list = new ArrayList<>();
//        SyntaxAppender current = null;
//        while ((current = jpaStringList.poll()) != null) {
//            current.toTree(jpaStringList);
//            list.add(current);
//        }
//        return list;
//    }

    private static final Logger logger = LoggerFactory.getLogger(ResultAppenderFactory.class);

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
}
