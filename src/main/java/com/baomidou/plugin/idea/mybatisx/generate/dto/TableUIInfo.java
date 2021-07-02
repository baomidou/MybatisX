package com.baomidou.plugin.idea.mybatisx.generate.dto;

import java.io.Serializable;

/**
 * @author :ls9527
 * @date : 2021/6/30
 */
public class TableUIInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 类名
     */
    private String className;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public TableUIInfo(String tableName, String className) {
        this.tableName = tableName;
        this.className = className;
    }

    public TableUIInfo() {
    }
}
