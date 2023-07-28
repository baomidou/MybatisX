package com.baomidou.plugin.idea.mybatisx.generate.setting;

import com.baomidou.plugin.idea.mybatisx.generate.dto.ConfigSetting;
import com.baomidou.plugin.idea.mybatisx.generate.dto.TemplateContext;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@State(name = "TemplatesSettings", storages = {@Storage("mybatisx/templates.xml")})
public class TemplatesSettings implements PersistentStateComponent<TemplatesSettings> {

    private TemplateContext templateConfigs;

    @NotNull
    public static TemplatesSettings getInstance(Project project) {
        TemplatesSettings service = ServiceManager.getService(project, TemplatesSettings.class);
        // 配置的默认值
        if (service.templateConfigs == null) {
            // 默认配置
            TemplateContext templateContext = new TemplateContext();
            templateContext.setTemplateSettingMap(new HashMap<>());
            templateContext.setProjectPath(project.getBasePath());
            service.templateConfigs = templateContext;
        }
        return service;
    }

    @Override
    public @Nullable TemplatesSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull TemplatesSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public void setTemplateConfigs(TemplateContext templateConfigs) {
        this.templateConfigs = templateConfigs;
    }

    /**
     * 默认的配置更改是无效的
     *
     * @return
     */
    public Map<String, ConfigSetting> getTemplateSettingMap() {
        final Map<String, ConfigSetting> templateSettingMap = new LinkedHashMap<>();
        // 保存全局配置
        Map<String, ConfigSetting> setTemplateSettingMap = DefaultSettingsConfig.defaultSettings();
        templateSettingMap.putAll(setTemplateSettingMap);
        // 加载自定义配置
        final Map<String, ConfigSetting> settingMap = templateConfigs.getTemplateSettingMap();
        templateSettingMap.putAll(settingMap);
        return templateSettingMap;
    }
}
