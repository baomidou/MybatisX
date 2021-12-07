package com.baomidou.mybatis3.domain;

public class BlogSetterDO {
    private Integer id;
    /**
     * 特殊的setter方法
     */
    private String oName;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOName() {
        return oName;
    }

    public void setOName(String oName) {
        this.oName = oName;
    }

    @Override
    public String toString() {
        return "BlogSetterDO{" +
            "id=" + id +
            ", oName='" + oName + '\'' +
            '}';
    }
}
