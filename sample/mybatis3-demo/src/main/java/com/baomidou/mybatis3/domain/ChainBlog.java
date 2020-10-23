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

    public ChainBlog setId(Long id) {
        this.id = id;
        return this;
    }

    public ChainBlog setTitle(String title) {
        this.title = title;
        return this;
    }

    public ChainBlog setContent(String content) {
        this.content = content;
        return this;
    }

    public ChainBlog setMoney(BigDecimal money) {
        this.money = money;
        return this;
    }

    public ChainBlog setAge(Integer age) {
        this.age = age;
        return this;
    }

    public ChainBlog setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public Integer getAge() {
        return age;
    }

    public Date getCreateTime() {
        return createTime;
    }
}
