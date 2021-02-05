package com.baomidou.plugin.idea.mybatisx.generate.dto;

import java.io.Serializable;

public class TemplateSettingDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 配置名称
     */
    private String configName;
    /**
     * 配置文件名称
     */
    private String configFile;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 后缀
     */
    private String suffix;
    /**
     * 包名
     */
    private String packageName;
    /**
     * 编码
     */
    private String encoding;
    /**
     * 模板内容
     */
    private String templateText;
    /**
     * 相对模块的资源文件路径
     */
    private String basePath;

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getTemplateText() {
        return templateText;
    }

    public void setTemplateText(String templateText) {
        this.templateText = templateText;
    }


}
