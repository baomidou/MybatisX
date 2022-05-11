package com.baomidou.plugin.idea.mybatisx.generate.dto;

import java.util.List;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<TemplateSettingDTO> getTemplateSettingDTOList() {
        return templateSettingDTOList;
    }

    public void setTemplateSettingDTOList(List<TemplateSettingDTO> templateSettingDTOList) {
        this.templateSettingDTOList = templateSettingDTOList;
    }
}
