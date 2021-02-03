package com.baomidou.plugin.idea.mybatisx.generate.dto;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CustomTemplateRoot implements Serializable {

    private String targetProject;

    private DomainInfo domainInfo;

    private List<TemplateSettingDTO> list = new ArrayList<>();

    public void add(TemplateSettingDTO customTemplateConfigDTO) {
        list.add(customTemplateConfigDTO);
    }

    public @NotNull  Optional<TemplateSettingDTO> findByName(@NotNull String currentName) {
        return list.stream().filter(x -> currentName.equalsIgnoreCase(x.getConfigName())).findFirst();
    }

    public Map<? extends String, ?> toMap() {
        return list.stream().collect(Collectors.toMap(TemplateSettingDTO::getConfigName, v -> v, (a, b) -> a));
    }

    public DomainInfo getDomainInfo() {
        return domainInfo;
    }

    public void setDomainInfo(DomainInfo domainInfo) {
        this.domainInfo = domainInfo;
    }

    public String getTargetProject() {
        return targetProject;
    }

    public void setTargetProject(String targetProject) {
        this.targetProject = targetProject;
    }
}
