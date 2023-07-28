package com.baomidou.plugin.idea.mybatisx.generate.dto;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomTemplateRoot implements Serializable {

    /**
     * 模板路径
     */
    @Getter
    private String templateBasePath;

    private ModuleInfoGo moduleUIInfo;

    @Getter
    private DomainInfo domainInfo;


    @Getter
    private List<TemplateSettingDTO> templateSettingDTOList;

    private List<ModuleInfoGo> moduleInfoList = new ArrayList<>();

    public void setModuleInfoList(List<ModuleInfoGo> moduleInfoList) {
        this.moduleInfoList = moduleInfoList;
    }

    public Map<? extends String, ?> toMap() {
        return moduleInfoList.stream().collect(Collectors.toMap(ModuleInfoGo::getConfigName, v -> v, (a, b) -> a));
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


    public void setTemplateSettingDTOList(List<TemplateSettingDTO> templateSettingDTOList) {
        this.templateSettingDTOList = templateSettingDTOList;
    }

    public void setTemplateBasePath(String templateBasePath) {
        this.templateBasePath = templateBasePath;
    }
}
