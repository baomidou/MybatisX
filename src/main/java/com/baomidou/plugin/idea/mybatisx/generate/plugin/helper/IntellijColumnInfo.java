package com.baomidou.plugin.idea.mybatisx.generate.plugin.helper;


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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDataType() {
        return this.dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public boolean isGeneratedColumn() {
        return this.generatedColumn;
    }

    public void setGeneratedColumn(boolean generatedColumn) {
        this.generatedColumn = generatedColumn;
    }

    public boolean isAutoIncrement() {
        return this.autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getDecimalDigits() {
        return this.decimalDigits;
    }

    public void setDecimalDigits(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    public String getRemarks() {
        return this.remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getColumnDefaultValue() {
        return this.columnDefaultValue;
    }

    public void setColumnDefaultValue(String columnDefaultValue) {
        this.columnDefaultValue = columnDefaultValue;
    }

    public Boolean getNullable() {
        return this.nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    public short getKeySeq() {
        return this.keySeq;
    }

    public void setKeySeq(short keySeq) {
        this.keySeq = keySeq;
    }
}
