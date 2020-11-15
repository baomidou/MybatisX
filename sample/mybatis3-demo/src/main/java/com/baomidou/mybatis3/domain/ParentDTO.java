package com.baomidou.mybatis3.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Accessors(chain = true)
@Data
public class ParentDTO implements Serializable {
    /**
     * 用于测试忽略静态字段
     */
    private static final long serialVersionUID = 1L;

    private Date createTime;
}
