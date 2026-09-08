package com.baomidou.plugin.idea.mybatisx.generate.dto;

import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.intellij.openapi.ui.Messages;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
public class GenerateConfig {

    /**
     * 忽略表的前缀
     */
    @Getter
    private String ignoreTablePrefix;
    /**
     * 忽略表的后缀
     */
    @Getter
    private String ignoreTableSuffix;

    //    private boolean needMapperAnnotation;
    /**
     * 界面恢复
     * -- GETTER --
     * 需要生成mapper注解
     *
     */
    @Getter
    private String moduleName;

    @Getter
    private String annotationType;

    /**
     * 基础包名
     */
    @Getter
    private String basePackage;
    /**
     * 相对包路径
     */
    @Getter
    private String relativePackage;
    /**
     * 编码方式, 默认: UTF-8
     */
    @Getter
    private String encoding;
    /**
     * 模块的源码相对路径
     */
    @Getter
    private String basePath;
    /**
     * 模块路径
     */
    @Getter
    private String modulePath;

    /**
     * 需要生成 toString,hashcode,equals
     */
    @Getter
    private boolean needToStringHashcodeEquals;
    /**
     * 需要生成实体类注释
     */
    @Getter
    private boolean needsComment;
    /**
     * 实体类需要继承的父类
     */
    @Getter
    private String superClass;
    /**
     * 需要移除的字段前缀
     */
    @Getter
    private String ignoreFieldPrefix;
    /**
     * 需要移除的字段后缀
     */
    @Getter
    private String ignoreFieldSuffix;

    /**
     * 需要生成repository注解
     *
     * @Repository
     */
//    private boolean repositoryAnnotation;

    @Getter
    private boolean useLombokPlugin;
    @Getter
    private boolean useActualColumns;
    @Getter
    private boolean jsr310Support;
    /**
     * 是否生成实体类
     */
    private Boolean needsModel;
    @Getter
    private boolean needSerializable;

    @Getter
    private boolean needJdbcType;

    @Getter
    private boolean useActualColumnAnnotationInject;
    /**
     * 模板组名称
     */
    @Getter
    private String templatesName;
    /**
     * 额外的类名后缀
     */
    @Getter
    private String extraClassSuffix;
    /**
     * 已选择的模板名称
     */
    @Getter
    private List<ModuleInfoGo> moduleUIInfoList;
    /**
     * 要生成的表信息列表
     */

    @Getter
    private transient List<TableUIInfo> tableUIInfoList;

    /**
     * 类名生成策略
     * CAMEL: 根据表名生成驼峰命名
     * SAME: 使用表明
     */
    @Getter
    private String classNameStrategy;


    public Boolean isNeedsModel() {
        return needsModel;
    }


    @Override
    public String toString() {
        return "GenerateConfig{" +
            "moduleName='" + moduleName + '\'' +
            ", annotationType='" + annotationType + '\'' +
            ", basePackage='" + basePackage + '\'' +
            ", relativePackage='" + relativePackage + '\'' +
            ", encoding='" + encoding + '\'' +
            ", basePath='" + basePath + '\'' +
            ", modulePath='" + modulePath + '\'' +
            ", needToStringHashcodeEquals=" + needToStringHashcodeEquals +
            ", needsComment=" + needsComment +
            ", rootClass='" + superClass + '\'' +
            ", removedPrefix='" + ignoreFieldPrefix + '\'' +
            ", removedSuffix='" + ignoreFieldSuffix + '\'' +
            ", useLombokPlugin=" + useLombokPlugin +
            ", useActualColumns=" + useActualColumns +
            ", jsr310Support=" + jsr310Support +
            ", needSerializable=" + needSerializable +
            ", needJdbcType=" + needJdbcType +
            ", useActualColumnAnnotationInject=" + useActualColumnAnnotationInject +
            ", templatesName='" + templatesName + '\'' +
            ", extraTemplateNames=" + moduleUIInfoList +
            '}';
    }

    public boolean checkGenerate() {
        if (StringUtils.isEmpty(moduleName)) {
            Messages.showErrorDialog("moduleName must not be empty", "Generate Info");
            return false;
        }
        if (StringUtils.isEmpty(templatesName)) {
            Messages.showErrorDialog("templatesName must not be empty", "Generate Info");
            return false;
        }
        return true;
    }

}
