package com.baomidou.mybatis3.domain;

public class DomainDupKeyParent extends DomainDupKeyRoot{
    Integer version;

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
