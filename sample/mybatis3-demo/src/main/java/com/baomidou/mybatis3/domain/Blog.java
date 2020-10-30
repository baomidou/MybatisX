package com.baomidou.mybatis3.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.apache.ibatis.type.Alias;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@TableName("t_blog")
public class Blog {
    @TableId("id")
   private Long id;
   private String title;
   private String content;
   private BigDecimal money;
   private Integer age;
   @TableField("create_time")
   private Date createTime;
    @TableField("blob_text")
   private byte[] blobText;
    private JpaBlog[] objects;
    private List<Integer> ids;

    public JpaBlog[] getObjects() {
        return objects;
    }

    public Blog setObjects(JpaBlog[] objects) {
        this.objects = objects;
        return this;
    }

    public List<Integer> getIds() {
        return ids;
    }

    public Blog setIds(List<Integer> ids) {
        this.ids = ids;
        return this;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public byte[] getBlobText() {
        return blobText;
    }

    public Blog setBlobText(byte[] blobText) {
        this.blobText = blobText;
        return this;
    }
}
