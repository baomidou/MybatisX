package com.baomidou.plugin.idea.mybatisx.generate.dto;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

/**
 * 字段信息
 */
public class FieldInfo {
    /**
     * 字段名称
     */
    private String fieldName;
    /**
     * 列名称
     */
    private String columnName;
    /**
     * java类型短名称
     */
    private String shortTypeName;
    /**
     * 字段注释
     */
    private String remark;

    public String getJdbcType() {
        return jdbcType;
    }

    private String jdbcType;

    public String getFieldName() {
        return fieldName;
    }

    public String getShortTypeName() {
        return shortTypeName;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getRemark() {
        return remark;
    }

    public static FieldInfo build(IntrospectedColumn introspectedColumn) {
        FieldInfo fieldInfo = new FieldInfo();
        fieldInfo.fieldName = introspectedColumn.getJavaProperty();
        fieldInfo.columnName = introspectedColumn.getActualColumnName();
        fieldInfo.jdbcType = introspectedColumn.getJdbcTypeName();

        FullyQualifiedJavaType fullyQualifiedJavaType = introspectedColumn.getFullyQualifiedJavaType();
        fieldInfo.shortTypeName = fullyQualifiedJavaType.getShortName();
        fieldInfo.remark = introspectedColumn.getRemarks();
        return fieldInfo;
    }
}
