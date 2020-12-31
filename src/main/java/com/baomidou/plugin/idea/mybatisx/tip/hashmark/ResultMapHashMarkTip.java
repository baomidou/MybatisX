package com.baomidou.plugin.idea.mybatisx.tip.hashmark;

import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.dom.model.ResultMap;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;

/**
 * 提示 resultMap
 *
 * @author ls9527
 */
public class ResultMapHashMarkTip implements HashMarkTip {

    @Override
    public String getName() {
        return "resultMap";
    }

    /**
     * resultMap 标签太多，只提示当前文件的
     *
     * @param completionResultSet
     * @param mapper
     */
    @Override
    public void tipValue(CompletionResultSet completionResultSet, Mapper mapper) {
        CompletionResultSet caseInsensitiveResultSet = completionResultSet.caseInsensitive();
        for (ResultMap resultMap : mapper.getResultMaps()) {
            caseInsensitiveResultSet.addElement(LookupElementBuilder.create(resultMap.getId()));
        }
    }
}
