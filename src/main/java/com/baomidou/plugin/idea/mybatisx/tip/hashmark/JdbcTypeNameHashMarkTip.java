package com.baomidou.plugin.idea.mybatisx.tip.hashmark;

import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.intellij.codeInsight.completion.CompletionResultSet;

public class JdbcTypeNameHashMarkTip implements HashMarkTip {
    @Override
    public String getName() {
        return "jdbcTypeName";
    }

    /**
     * 自定义结构体类型,无需支持
     *
     * @param completionResultSet
     * @param mapper
     */
    @Override
    public void tipValue(CompletionResultSet completionResultSet, Mapper mapper) {

    }
}
