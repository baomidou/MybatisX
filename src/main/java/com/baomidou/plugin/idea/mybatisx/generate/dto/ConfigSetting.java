package com.baomidou.plugin.idea.mybatisx.generate.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ConfigSetting {

    /**
     * 配置名称
     */
    private String name;
    /**
     * 配置路径
     */
    private String path;
    /**
     * 模板信息
     */
    private List<TemplateSettingDTO> templateSettingDTOList;

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setTemplateSettingDTOList(List<TemplateSettingDTO> templateSettingDTOList) {
        this.templateSettingDTOList = templateSettingDTOList;
    }
}
