package com.baomidou.plugin.idea.mybatisx.generate.dto;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 末班上下文配置
 */
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

    public void setGenerateConfig(GenerateConfig generateConfig) {
        this.generateConfig = generateConfig;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public void setAnnotationType(String annotationType) {
        this.annotationType = annotationType;
    }

    public void setTemplateSettingMap(Map<String, ConfigSetting> templateSettingMap) {
        this.templateSettingMap = templateSettingMap;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
}
