package com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest;

import java.util.Set;

/**
 * @author ls9527
 */
public class ConditionIfTestWrapper implements ConditionFieldWrapper {
    private Set<String> wrapperFields;

    public ConditionIfTestWrapper(Set<String> wrapperFields) {
        this.wrapperFields = wrapperFields;
    }

    @Override
    public String wrapperConditionText(String fieldName, String templateText) {
        if (wrapperFields.contains(fieldName)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\n<if test=\"").append(getNullWrapper(fieldName)).append("\">");
            stringBuilder.append(templateText);
            stringBuilder.append("\n").append("</if>");
            templateText = stringBuilder.toString();
        }
        return templateText;
    }

    @Override
    public String wrapperWhere(String content) {
        return "<where>" + content + "\n</where>";
    }

    private String getNullWrapper(String fieldName) {
        return fieldName + "!= null";
    }
}
