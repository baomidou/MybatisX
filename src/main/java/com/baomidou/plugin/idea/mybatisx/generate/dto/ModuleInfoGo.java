package com.baomidou.plugin.idea.mybatisx.generate.dto;

import lombok.Getter;

import java.io.Serializable;

/**
 * @author :ls9527
 * @date : 2021/6/30
 */
@Getter
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
     * 模板文件文件名
     */
    private String configFileName;
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

    private Boolean enable = false;

    private Boolean needJdbcType;

    public void setFileNameWithSuffix(String fileNameWithSuffix) {
        this.fileNameWithSuffix = fileNameWithSuffix;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public void setModulePath(String modulePath) {
        this.modulePath = modulePath;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public void setConfigFileName(String configFileName) {
        this.configFileName = configFileName;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public void setNeedJdbcType(Boolean needJdbcType) {
        this.needJdbcType = needJdbcType;
    }
}
