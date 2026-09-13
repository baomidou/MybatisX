package com.baomidou.plugin.idea.mybatisx.smartjpa.component;


import lombok.Getter;
import lombok.Setter;

/**
 * The type Tx field.
 */
@Setter
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
     * Gets tip name.
     * <p>
     * <p>
     * -- SETTER --
     * Sets tip name.
     *
     * @return the tip name
     * @param tipName the tip name
     */
    private String tipName;
    /**
     * 字段名称
     * -- GETTER --
     * Gets field name.
     * <p>
     * <p>
     * -- SETTER --
     * Sets field name.
     *
     * @return the field name
     * @param fieldName the field name
     */
    private String fieldName;

    /**
     * 表的列名
     * -- GETTER --
     * Gets column name.
     * <p>
     * <p>
     * -- SETTER --
     * Sets column name.
     *
     * @return the column name
     * @param columnName the column name
     */
    private String columnName;
    /**
     * 字段类型
     * -- GETTER --
     * Gets field type.
     * <p>
     * <p>
     * -- SETTER --
     * Sets field type.
     *
     * @return the field type
     * @param fieldType the field type
     */
    private String fieldType;
    /**
     * 字段jdbc类型
     */
    private String jdbcType;

}
