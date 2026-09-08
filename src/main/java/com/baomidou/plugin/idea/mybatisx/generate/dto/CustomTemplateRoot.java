package com.baomidou.plugin.idea.mybatisx.generate.dto;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Setter
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

    public Map<? extends String, ?> toMap() {
        return moduleInfoList.stream().collect(Collectors.toMap(ModuleInfoGo::getConfigName, v -> v, (a, b) -> a));
    }

    @NotNull
    public ModuleInfoGo getModuleUIInfo() {
        return moduleUIInfo;
    }


}
