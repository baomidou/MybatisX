package com.baomidou.plugin.idea.mybatisx.smartjpa.component;


import lombok.Getter;

/**
 * The type Tx field.
 */
@Getter
public class TxField {
    /**
     * 是不是主键
     */
    private Boolean primaryKey = false;
    /**
     * 定义字段的类全路径名称
     */
    private String className;
    /**
     * 提示名称
     * -- GETTER --
     *  Gets tip name.
     *
     * @return the tip name

     */
    private String tipName;
    /**
     * 字段名称
     * -- GETTER --
     *  Gets field name.
     *
     * @return the field name

     */
    private String fieldName;

    /**
     * 表的列名
     * -- GETTER --
     *  Gets column name.
     *
     * @return the column name

     */
    private String columnName;
    /**
     * 字段类型
     * -- GETTER --
     *  Gets field type.
     *
     * @return the field type

     */
    private String fieldType;
    /**
     * 字段jdbc类型
     */
    private String jdbcType;

    /**
     * Sets field type.
     *
     * @param fieldType the field type
     */
    public void setFieldType(final String fieldType) {
        this.fieldType = fieldType;
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
     * Sets field name.
     *
     * @param fieldName the field name
     */
    public void setFieldName(final String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Sets column name.
     *
     * @param columnName the column name
     */
    public void setColumnName(final String columnName) {
        this.columnName = columnName;
    }

    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setPrimaryKey(Boolean primaryKey) {
        this.primaryKey = primaryKey;
    }
}
