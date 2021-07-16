package com.baomidou.plugin.idea.mybatisx.generate.dto;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomTemplateRoot implements Serializable {

    private ModuleInfoGo moduleUIInfo;

    private DomainInfo domainInfo;

    private String templateText;

    public String getTemplateText() {
        return templateText;
    }

    public void setTemplateText(String templateText) {
        this.templateText = templateText;
    }

    private List<ModuleInfoGo> moduleInfoList = new ArrayList<>();

    public void setModuleInfoList(List<ModuleInfoGo> moduleInfoList) {
        this.moduleInfoList = moduleInfoList;
    }

    public Map<? extends String, ?> toMap() {
        return moduleInfoList.stream().collect(Collectors.toMap(ModuleInfoGo::getConfigName, v -> v, (a, b) -> a));
    }

    public DomainInfo getDomainInfo() {
        return domainInfo;
    }

    public void setDomainInfo(DomainInfo domainInfo) {
        this.domainInfo = domainInfo;
    }

    @NotNull
    public ModuleInfoGo getModuleUIInfo() {
        return moduleUIInfo;
    }

    public void setModuleUIInfo(ModuleInfoGo moduleUIInfo) {
        this.moduleUIInfo = moduleUIInfo;
    }
}
