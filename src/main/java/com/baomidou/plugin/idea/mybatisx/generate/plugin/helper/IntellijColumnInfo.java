package com.baomidou.plugin.idea.mybatisx.generate.plugin.helper;


import lombok.Getter;

@Getter
public class IntellijColumnInfo {
    private String name;
    private int dataType;
    private boolean generatedColumn;
    private boolean autoIncrement;
    private int size;
    private int decimalDigits;
    private String remarks;
    private String columnDefaultValue;
    private Boolean nullable;
    private short keySeq;

    public IntellijColumnInfo() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public void setGeneratedColumn(boolean generatedColumn) {
        this.generatedColumn = generatedColumn;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setDecimalDigits(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setColumnDefaultValue(String columnDefaultValue) {
        this.columnDefaultValue = columnDefaultValue;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    public void setKeySeq(short keySeq) {
        this.keySeq = keySeq;
    }
}
