package com.baomidou.mybatis3.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;


@TableName("sys_user_info")
public class SysUserInfo {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    public String getId() {
        return id;
    }

    public SysUserInfo setId(String id) {
        this.id = id;
        return this;
    }
}
