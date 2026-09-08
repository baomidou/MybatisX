package com.baomidou.plugin.idea.mybatisx.generate.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
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

}
