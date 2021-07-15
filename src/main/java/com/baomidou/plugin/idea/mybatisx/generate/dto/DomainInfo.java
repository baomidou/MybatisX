package com.baomidou.plugin.idea.mybatisx.generate.dto;

import java.io.Serializable;

public class DomainInfo implements Serializable {
    private String encoding;
    private String basePackage;
    private String relativePackage;
    private String fileName;
    private String basePath;
    private String modulePath;

    public String getModulePath() {
        return modulePath;
    }

    public void setModulePath(String modulePath) {
        this.modulePath = modulePath;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getRelativePackage() {
        return relativePackage;
    }

    public void setRelativePackage(String relativePackage) {
        this.relativePackage = relativePackage;
    }

    public DomainInfo copyFromFileName(String extraDomainName) {
        DomainInfo domainInfo = new DomainInfo();
        domainInfo.setModulePath(modulePath);
        domainInfo.setBasePath(basePath);
        domainInfo.setEncoding(encoding);
        domainInfo.setBasePackage(basePackage);
        domainInfo.setFileName(extraDomainName);
        domainInfo.setRelativePackage(relativePackage);
        return domainInfo;
    }
}

