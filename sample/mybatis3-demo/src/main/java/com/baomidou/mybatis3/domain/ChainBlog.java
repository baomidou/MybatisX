package com.baomidou.mybatis3.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.util.Date;


@TableName("t_blog")
public class ChainBlog {
    @TableId("id")
    private Long id;
    private String title;
    private String content;
    private BigDecimal money;
    private Integer age;
    @TableField("create_time")
    private Date createTime;

    public Long getId() {
        return id;
    }

    public ChainBlog setId(Long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public ChainBlog setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContent() {
        return content;
    }

    public ChainBlog setContent(String content) {
        this.content = content;
        return this;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public ChainBlog setMoney(BigDecimal money) {
        this.money = money;
        return this;
    }

    public Integer getAge() {
        return age;
    }

    public ChainBlog setAge(Integer age) {
        this.age = age;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public ChainBlog setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }
}
