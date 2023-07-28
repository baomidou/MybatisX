package com.baomidou.plugin.idea.mybatisx.generate.dto;

import lombok.Getter;

import java.io.Serializable;

/**
 * @author :ls9527
 * @date : 2021/6/30
 */
@Getter
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

    public TableUIInfo(String tableName, String className) {
        this.tableName = tableName;
        this.className = className;
    }

    public TableUIInfo() {
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
