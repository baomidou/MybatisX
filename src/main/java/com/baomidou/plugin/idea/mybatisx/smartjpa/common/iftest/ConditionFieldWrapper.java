package com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest;

public interface ConditionFieldWrapper {
    String wrapperConditionText(String fieldName, String templateText);

    String wrapperWhere(String content);
}
