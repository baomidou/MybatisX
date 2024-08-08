package com.baomidou.plugin.idea.mybatisx.generate.dto;

import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.intellij.openapi.ui.Messages;
import lombok.Getter;

import java.util.List;

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
     *  需要生成mapper注解
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

    public void setTableUIInfoList(List<TableUIInfo> tableUIInfoList) {
        this.tableUIInfoList = tableUIInfoList;
    }

    public void setExtraClassSuffix(String extraClassSuffix) {
        this.extraClassSuffix = extraClassSuffix;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }


    public void setTemplatesName(String templatesName) {
        this.templatesName = templatesName;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setNeedsComment(boolean needsComment) {
        this.needsComment = needsComment;
    }

    public Boolean isNeedsModel() {
        return needsModel;
    }

    public void setNeedsModel(Boolean needsModel) {
        this.needsModel = needsModel;
    }

    public void setNeedSerializable(boolean needSerializable) {
        this.needSerializable = needSerializable;
    }

    public void setNeedJdbcType(boolean needJdbcType) {
        this.needJdbcType = needJdbcType;
    }

    public void setAnnotationType(String annotationType) {
        this.annotationType = annotationType;
    }

    public void setUseActualColumns(boolean useActualColumns) {
        this.useActualColumns = useActualColumns;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public void setRelativePackage(String relativePackage) {
        this.relativePackage = relativePackage;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public void setModulePath(String modulePath) {
        this.modulePath = modulePath;
    }

    public void setModuleUIInfoList(List<ModuleInfoGo> moduleUIInfoList) {
        this.moduleUIInfoList = moduleUIInfoList;
    }

    public void setNeedToStringHashcodeEquals(boolean needToStringHashcodeEquals) {
        this.needToStringHashcodeEquals = needToStringHashcodeEquals;
    }


    public void setUseLombokPlugin(boolean useLombokPlugin) {
        this.useLombokPlugin = useLombokPlugin;
    }

    public void setJsr310Support(boolean jsr310Support) {
        this.jsr310Support = jsr310Support;
    }


    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }

    public void setUseActualColumnAnnotationInject(boolean useActualColumnAnnotationInject) {
        this.useActualColumnAnnotationInject = useActualColumnAnnotationInject;
    }

    public void setIgnoreTablePrefix(String ignoreTablePrefix) {
        this.ignoreTablePrefix = ignoreTablePrefix;
    }

    public void setIgnoreTableSuffix(String ignoreTableSuffix) {
        this.ignoreTableSuffix = ignoreTableSuffix;
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

    public void setIgnoreFieldPrefix(String ignoreFieldPrefix) {
        this.ignoreFieldPrefix = ignoreFieldPrefix;
    }

    public void setIgnoreFieldSuffix(String ignoreFieldSuffix) {
        this.ignoreFieldSuffix = ignoreFieldSuffix;
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

    public void setClassNameStrategy(String classNameStrategy) {
        this.classNameStrategy = classNameStrategy;
    }
}
