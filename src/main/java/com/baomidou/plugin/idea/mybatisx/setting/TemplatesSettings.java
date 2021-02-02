package com.baomidou.plugin.idea.mybatisx.setting;

import com.baomidou.plugin.idea.mybatisx.setting.template.TemplateContext;
import com.baomidou.plugin.idea.mybatisx.setting.template.TemplateSettingDTO;
import com.baomidou.plugin.idea.mybatisx.util.IOUtils;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.ReflectionUtil;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@State(name = "TemplatesSettings", storages = {@Storage("mybatisx/templates.xml")})
public class TemplatesSettings implements PersistentStateComponent<TemplatesSettings> {

    public static final String DEFAULT_TEMPLATE_NAME = "mybatis-plus3";
    private TemplateContext templateConfigs;

    @NotNull
    public static TemplatesSettings getInstance(Project project) {
        TemplatesSettings service = ServiceManager.getService(project, TemplatesSettings.class);
        // 配置的默认值
        if (service.templateConfigs == null) {
            Map<String, List<TemplateSettingDTO>> setTemplateSettingMap = new HashMap<>();
            List<TemplateSettingDTO> templateSettingDTOS = defaultSettings();
            setTemplateSettingMap.put(DEFAULT_TEMPLATE_NAME, templateSettingDTOS);

            TemplateContext templateContext = new TemplateContext();
            templateContext.setTemplateSettingMap(setTemplateSettingMap);
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

    private static List<TemplateSettingDTO> defaultSettings() {
        List<TemplateSettingDTO> list = new ArrayList<>();
        try {
            TemplateSettingDTO service = new TemplateSettingDTO();
            service.setConfigName("service");
            service.setFileName("${domain}Service");
            service.setSuffix(".java");
            service.setPackageName("generator");
            service.setEncoding("UTF-8");
            service.setBasePath("src/main/java");
            try (InputStream resourceAsStream = ReflectionUtil.getGrandCallerClass().getClassLoader().getResourceAsStream("generate/template/service.ftl")) {
                String templateText = IOUtils.toString(resourceAsStream, "UTF-8");
                service.setTemplateText(templateText);
                list.add(service);
            }

            TemplateSettingDTO serviceImpl = new TemplateSettingDTO();
            serviceImpl.setConfigName("serviceImpl");
            serviceImpl.setFileName("${domain}ServiceImpl");
            serviceImpl.setSuffix(".java");
            serviceImpl.setPackageName("generator");
            serviceImpl.setEncoding("UTF-8");
            serviceImpl.setBasePath("src/main/java");
            try (InputStream resourceAsStream = ReflectionUtil.getGrandCallerClass().getClassLoader().getResourceAsStream("generate/template/service-impl.ftl")) {
                String templateText = IOUtils.toString(resourceAsStream, "UTF-8");
                serviceImpl.setTemplateText(templateText);
                list.add(serviceImpl);
            }

            TemplateSettingDTO mapperInterface = new TemplateSettingDTO();
            mapperInterface.setConfigName("mapperInterface");
            mapperInterface.setFileName("${domain}Mapper");
            mapperInterface.setSuffix(".java");
            mapperInterface.setPackageName("generator");
            mapperInterface.setEncoding("UTF-8");
            mapperInterface.setBasePath("src/main/java");
            try (InputStream resourceAsStream = ReflectionUtil.getGrandCallerClass().getClassLoader().getResourceAsStream("generate/template/mapper.ftl")) {
                String templateText = IOUtils.toString(resourceAsStream, "UTF-8");
                mapperInterface.setTemplateText(templateText);
                list.add(mapperInterface);
            }

            TemplateSettingDTO mapperXml = new TemplateSettingDTO();
            mapperXml.setConfigName("mapperXml");
            mapperXml.setFileName("${domain}Mapper");
            mapperXml.setSuffix(".xml");
            mapperXml.setPackageName("generator");
            mapperXml.setEncoding("UTF-8");
            mapperXml.setBasePath("src/main/resources");
            try (InputStream resourceAsStream = ReflectionUtil.getGrandCallerClass().getClassLoader().getResourceAsStream("generate/template/mapper-xml.ftl")) {
                String templateText = IOUtils.toString(resourceAsStream, "UTF-8");
                mapperXml.setTemplateText(templateText);
                list.add(mapperXml);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
