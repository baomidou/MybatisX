package com.baomidou.plugin.idea.mybatisx.generate.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * 末班上下文配置
 */
@Setter
@Getter
public class TemplateContext {
    /**
     * 项目路径
     */
    private String projectPath;
    /**
     * 模块名称
     */
    private String moduleName;
    /**
     * 注解类型
     */
    private String annotationType;
    /**
     * 模板名称
     */
    private String templateName;
    /**
     * 扩展的自定义模板
     */
    private Map<String, ConfigSetting> templateSettingMap = new HashMap<>();

    private GenerateConfig generateConfig;

}
