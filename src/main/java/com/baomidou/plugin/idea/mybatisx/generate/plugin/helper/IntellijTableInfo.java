package com.baomidou.plugin.idea.mybatisx.generate.plugin.helper;


import lombok.Getter;

import java.util.List;

@Getter
public class IntellijTableInfo {
    private String tableName;
    private String databaseType;
    private String tableRemark;
    private String tableType;
    private List<IntellijColumnInfo> columnInfos;
    private List<IntellijColumnInfo> primaryKeyColumns;

    public IntellijTableInfo() {
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    public void setColumnInfos(List<IntellijColumnInfo> columnInfos) {
        this.columnInfos = columnInfos;
    }

    public void setPrimaryKeyColumns(List<IntellijColumnInfo> primaryKeyColumns) {
        this.primaryKeyColumns = primaryKeyColumns;
    }

    public void setTableRemark(String tableRemark) {
        this.tableRemark = tableRemark;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }
}
