package com.baomidou.plugin.idea.mybatisx.generate.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author :ls9527
 * @date : 2021/6/30
 */
@Setter
@Getter
public class ModuleInfoGo implements Serializable {

    @Serial
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

}
