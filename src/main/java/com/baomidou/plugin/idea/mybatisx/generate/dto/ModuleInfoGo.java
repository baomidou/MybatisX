package com.baomidou.plugin.idea.mybatisx.generate.dto;

import java.io.Serializable;

/**
 * @author :ls9527
 * @date : 2021/6/30
 */
public class ModuleInfoGo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 配置名称
     */
    private String configName;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 有后缀的文件名
     */
    private String fileNameWithSuffix;
    /**
     * 模块路径
     */
    private String modulePath;

    /**
     * 相对包路径
     */
    private String packageName;
    /**
     * 编码方式, 默认: UTF-8
     */
    private String encoding;
    /**
     * 模块的源码相对路径
     */
    private String basePath;

    public String getFileNameWithSuffix() {
        return fileNameWithSuffix;
    }

    public void setFileNameWithSuffix(String fileNameWithSuffix) {
        this.fileNameWithSuffix = fileNameWithSuffix;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getModulePath() {
        return modulePath;
    }

    public void setModulePath(String modulePath) {
        this.modulePath = modulePath;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }
}
