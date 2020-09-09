package com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest;

/**
 * @author ls9527
 */
public class NeverContainsFieldWrapper implements ConditionFieldWrapper {
    @Override
    public String wrapperConditionText(String fieldName, String templateText) {
        return templateText;
    }

    @Override
    public String wrapperWhere(String content) {
        return "where " + content;
    }
}
