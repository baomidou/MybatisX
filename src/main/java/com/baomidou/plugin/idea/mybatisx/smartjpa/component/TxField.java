package com.baomidou.plugin.idea.mybatisx.smartjpa.component;

import org.jetbrains.annotations.NotNull;

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

    private String fieldType;

    public String getFieldType() {
        return this.fieldType;
    }

    public void setFieldType(final String fieldType) {
        this.fieldType = fieldType;
    }

    public String getTipName() {
        return this.tipName;
    }

    public void setTipName(final String tipName) {
        this.tipName = tipName;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(final String fieldName) {
        this.fieldName = fieldName;
    }

    @NotNull
    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(final String columnName) {
        this.columnName = columnName;
    }
}
