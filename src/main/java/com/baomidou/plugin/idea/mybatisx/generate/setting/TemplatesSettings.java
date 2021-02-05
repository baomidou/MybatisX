package com.baomidou.plugin.idea.mybatisx.generate.setting;

import com.baomidou.plugin.idea.mybatisx.generate.dto.TemplateContext;
import com.baomidou.plugin.idea.mybatisx.generate.dto.TemplateSettingDTO;
import com.intellij.ide.extensionResources.ExtensionsRootType;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.util.ReflectionUtil;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public TemplateContext getTemplateConfigs() {
        return templateConfigs;
    }

    public void setTemplateConfigs(TemplateContext templateConfigs) {
        this.templateConfigs = templateConfigs;
    }

    /**
     * 默认的配置更改是无效的
     *
     * @return
     */
    public Map<String, List<TemplateSettingDTO>> getTemplateSettingMap() {
        final Map<String, List<TemplateSettingDTO>> templateSettingMap = new HashMap<>();
        final Map<String, List<TemplateSettingDTO>> settingMap = templateConfigs.getTemplateSettingMap();
        Map<String, List<TemplateSettingDTO>> setTemplateSettingMap = DefaultSettingsConfig.defaultSettings();
        templateSettingMap.putAll(settingMap);
        templateSettingMap.putAll(setTemplateSettingMap);
        return templateSettingMap;
    }
}
