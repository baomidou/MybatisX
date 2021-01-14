package com.baomidou.plugin.idea.mybatisx.smartjpa.component;


/**
 * The type Tx field.
 */
public class TxField {
    /**
     * 提示名称
     */
    private String tipName;
    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 表的列名
     */
    private String columnName;
    /**
     * 字段类型
     */
    private String fieldType;
    /**
     * 字段jdbc类型
     */
    private String jdbcType;
    /**
     * Gets field type.
     *
     * @return the field type
     */
    public String getFieldType() {
        return this.fieldType;
    }

    /**
     * Sets field type.
     *
     * @param fieldType the field type
     */
    public void setFieldType(final String fieldType) {
        this.fieldType = fieldType;
    }

    /**
     * Gets tip name.
     *
     * @return the tip name
     */
    public String getTipName() {
        return this.tipName;
    }

    /**
     * Sets tip name.
     *
     * @param tipName the tip name
     */
    public void setTipName(final String tipName) {
        this.tipName = tipName;
    }

    /**
     * Gets field name.
     *
     * @return the field name
     */
    public String getFieldName() {
        return this.fieldName;
    }

    /**
     * Sets field name.
     *
     * @param fieldName the field name
     */
    public void setFieldName(final String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Gets column name.
     *
     * @return the column name
     */
    public String getColumnName() {
        return this.columnName;
    }

    /**
     * Sets column name.
     *
     * @param columnName the column name
     */
    public void setColumnName(final String columnName) {
        this.columnName = columnName;
    }

    public String getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }
}
