package com.baomidou.mybatis3.domain;

public class BlogAgeContentDTO {
    private String content;
    private Integer age;

    public void setContent(String content) {
        this.content = content;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getContent() {
        return content;
    }

    public Integer getAge() {
        return age;
    }
}
