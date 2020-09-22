package com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest;

/**
 * The type Never contains field wrapper.
 *
 * @author ls9527
 */
public class NeverContainsFieldWrapper implements ConditionFieldWrapper {
    @Override
    public String wrapperConditionText(String fieldName, String templateText) {
        return templateText;
    }

    @Override
    public String wrapperWhere(String content) {
        return "where \n" + content;
    }

    /**
     * 默认的 查询所有字段的方式
     * @return
     */
    @Override
    public String getAllFields() {
        return "<include refid=\"Base_Column_List\" />";
    }

    @Override
    public String getResultMap() {
        return "BaseResultMap";
    }

    @Override
    public String getResultType() {
        return null;
    }
}
