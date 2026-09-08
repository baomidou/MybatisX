package com.baomidou.plugin.idea.mybatisx.generate.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class DomainInfo implements Serializable {
    private String encoding;
    private String basePackage;
    private String relativePackage;
    private String fileName;
    private String basePath;
    private String modulePath;

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
