package com.baomidou.plugin.idea.mybatisx.generate.plugin.helper;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IntellijColumnInfo {
    private String name;
    /**
     * 数据库原始类型名，例如 int unsigned、number(10,0)。
     */
    private String typeName;
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

}
