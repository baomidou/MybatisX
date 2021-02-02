package com.baomidou.plugin.idea.mybatisx.generate.template;


import com.baomidou.plugin.idea.mybatisx.util.StringUtils;

import java.util.Map;

public final class CustomPluginContextBuilder {
    private String templateName;
    private String fileName;
    private String suffix;
    private String packageName;
    private String encoding;
    private String targetProject;

    private CustomPluginContextBuilder() {
    }

    public static CustomPluginContextBuilder aCustomPluginContext() {
        return new CustomPluginContextBuilder();
    }

    public CustomPluginContextBuilder withTemplateName(String templateName) {
        this.templateName = templateName;
        return this;
    }

    public CustomPluginContextBuilder withFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public CustomPluginContextBuilder withSuffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    public CustomPluginContextBuilder withPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public CustomPluginContextBuilder withEncoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    public CustomPluginContextBuilder withTargetProject(String targetProject) {
        this.targetProject = targetProject;
        return this;
    }

    public CustomPluginContext build() {
        CustomPluginContext customPluginContext = new CustomPluginContext();
        customPluginContext.setTemplateName(templateName);
        customPluginContext.setFileName(fileName);
        customPluginContext.setSuffix(suffix);
        customPluginContext.setPackageName(packageName);
        customPluginContext.setEncoding(encoding);
        customPluginContext.setModulePath(targetProject);
        return customPluginContext;
    }

    public CustomPluginContext buildByProperties(Map<String,String> properties) {
        CustomPluginContext customPluginContext = new CustomPluginContext();
        customPluginContext.setTemplateName(properties.get("templateName"));
        customPluginContext.setFileName(properties.get("fileName"));
        customPluginContext.setSuffix(properties.get("suffix"));
        customPluginContext.setPackageName(properties.get("packageName"));
        customPluginContext.setEncoding(properties.get("encoding"));
        customPluginContext.setModulePath(properties.get("targetProject"));
        customPluginContext.setBasePath(properties.get("basePath"));
        return customPluginContext;
    }
}
