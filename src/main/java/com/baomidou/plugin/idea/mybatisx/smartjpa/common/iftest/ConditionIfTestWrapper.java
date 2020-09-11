package com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest;

import java.util.Set;

/**
 * @author ls9527
 */
public class ConditionIfTestWrapper implements ConditionFieldWrapper {
    private Set<String> wrapperFields;
    private String allFieldsStr;
    private String resultMap;
    private boolean resultType;
    private String resultTypeClass;

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

    @Override
    public String getAllFields() {
        return allFieldsStr;
    }

    @Override
    public String getResultMap() {
        return resultType ? null : resultMap;
    }

    @Override
    public String getResultType() {
        return resultType ? resultTypeClass : null;
    }


    private String getNullWrapper(String fieldName) {
        return fieldName + "!= null";
    }

    public void setAllFields(String allFieldsStr) {
        this.allFieldsStr = allFieldsStr;
    }

    public void setResultMap(String resultMap) {
        this.resultMap = resultMap;
    }

    public void setResultType(boolean resultType) {
        this.resultType = resultType;
    }

    public void setResultTypeClass(String resultTypeClass) {
        this.resultTypeClass = resultTypeClass;
    }
}
