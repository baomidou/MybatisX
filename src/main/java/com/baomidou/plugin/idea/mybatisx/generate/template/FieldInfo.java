package com.baomidou.plugin.idea.mybatisx.generate.template;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

/**
 * 字段信息
 */
public class FieldInfo {
    private String fieldName;
    private String columnName;
private String shortTypeName;

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

    public static FieldInfo build(IntrospectedColumn introspectedColumn) {
        FieldInfo fieldInfo = new FieldInfo();
        fieldInfo.fieldName = introspectedColumn.getJavaProperty();
        fieldInfo.columnName = introspectedColumn.getActualColumnName();
        fieldInfo.jdbcType = introspectedColumn.getJdbcTypeName();

        FullyQualifiedJavaType fullyQualifiedJavaType = introspectedColumn.getFullyQualifiedJavaType();
        fieldInfo.shortTypeName = fullyQualifiedJavaType.getShortName();
        return fieldInfo;
    }
}
