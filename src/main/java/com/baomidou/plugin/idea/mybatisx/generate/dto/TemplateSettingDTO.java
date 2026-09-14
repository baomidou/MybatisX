package com.baomidou.plugin.idea.mybatisx.generate.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@Setter
@Getter
public class TemplateSettingDTO implements Serializable {
    @Serial
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
     * 相对模块的资源文件路径
     */
    private String basePath;

    /**
     * 是否启用
     */
    private Set<String> existsFileNames;

}
