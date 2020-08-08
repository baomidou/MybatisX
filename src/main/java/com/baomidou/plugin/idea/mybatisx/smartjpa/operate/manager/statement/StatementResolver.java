package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager.statement;





import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.completion.parameter.MxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.model.AppendTypeEnum;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 声明解析器
 */
public class StatementResolver {
    /**
     * 增,删,改,查
     */
    private String type;

    /**
     * select:
     * select + result + condition + sort
     * update:
     * update + condition
     * insert
     * not support
     * delete:
     * delete + condition
     */
    private List<Statement> result = new ArrayList<>();


    public void setSytaxAppenderList(LinkedList<SyntaxAppender> jpaStringList) {
        Statement currentStatement = null;
        for (SyntaxAppender syntaxAppender : jpaStringList) {
            AppendTypeEnum type = syntaxAppender.getType();
            if(type.equals(AppendTypeEnum.AREA)){

            }
        }
    }

    /**
     * 获得参数列表
     * @return
     */
    public List<MxParameter> getParameters() {
        List<MxParameter> parameters = new ArrayList<>();
        for (Statement statement : result) {
            parameters.addAll(statement.getParameters());
        }
        return parameters;
    }
}
