package com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest;

/**
 * The interface Condition field wrapper.
 */
public interface ConditionFieldWrapper {
    /**
     * Wrapper condition text string.
     *
     * @param fieldName    the field name
     * @param templateText the template text
     * @return the string
     */
    String wrapperConditionText(String fieldName, String templateText);

    /**
     * Wrapper where string.
     *
     * @param content the content
     * @return the string
     */
    String wrapperWhere(String content);

    /**
     * Gets all fields.
     *
     * @return the all fields
     */
    String getAllFields();

    /**
     * Gets result map.
     *
     * @return the result map
     */
    String getResultMap();

    /**
     * Gets result type.
     *
     * @return the result type
     */
    String getResultType();

}
