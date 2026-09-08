package com.baomidou.plugin.idea.mybatisx.generate.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author :ls9527
 * @date : 2021/6/30
 */
@Setter
@Getter
public class TableUIInfo implements Serializable {

    @Serial
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

}
