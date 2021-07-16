package com.baomidou.plugin.idea.mybatisx.generate.dto;

import java.util.List;

public class GenerateConfig {
    /**
     * 忽略表的前缀
     */
    private String ignoreTablePrefix;
    /**
     * 忽略表的后缀
     */
    private String ignoreTableSuffix;

    /**
     * 界面恢复
     */
    private String moduleName;

    private String annotationType;

    /**
     * 基础包名
     */
    private String basePackage;
    /**
     * 相对包路径
     */
    private String relativePackage;
    /**
     * 编码方式, 默认: UTF-8
     */
    private String encoding;
    /**
     * 模块的源码相对路径
     */
    private String basePath;
    /**
     * 模块路径
     */
    private String modulePath;

    /**
     * 需要生成 toString,hashcode,equals
     */
    private boolean needToStringHashcodeEquals;
    /**
     * 需要生成实体类注释
     */
    private boolean needsComment;
    /**
     * 实体类需要继承的父类
     */
    private String superClass;
    /**
     * 需要移除的字段前缀
     */
    private String ignoreFieldPrefix;
    /**
     * 需要移除的字段后缀
     */
    private String ignoreFieldSuffix;

    /**
     * 需要生成repository注解
     *
     * @Repository
     */
//    private boolean repositoryAnnotation;

    private boolean useLombokPlugin;
    private boolean useActualColumns;
    private boolean jsr310Support;
    private boolean useActualColumnAnnotationInject;
    /**
     * 模板组名称
     */
    private String templatesName;
    /**
     * 额外的类名后缀
     */
    private String extraClassSuffix;
    /**
     * 已选择的模板名称
     */
    private List<ModuleInfoGo> moduleUIInfoList;
    /**
     * 要生成的表信息列表
     */

    private transient List<TableUIInfo> tableUIInfoList;

    public List<TableUIInfo> getTableUIInfoList() {
        return tableUIInfoList;
    }

    public void setTableUIInfoList(List<TableUIInfo> tableUIInfoList) {
        this.tableUIInfoList = tableUIInfoList;
    }

    public String getExtraClassSuffix() {
        return extraClassSuffix;
    }

    public void setExtraClassSuffix(String extraClassSuffix) {
        this.extraClassSuffix = extraClassSuffix;
    }

    /**
     * 需要生成mapper注解
     *
     * @Mapper
     */
//    private boolean needMapperAnnotation;


    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }


    public String getTemplatesName() {
        return templatesName;
    }

    public void setTemplatesName(String templatesName) {
        this.templatesName = templatesName;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public boolean isNeedsComment() {
        return needsComment;
    }

    public void setNeedsComment(boolean needsComment) {
        this.needsComment = needsComment;
    }

    public String getAnnotationType() {
        return annotationType;
    }

    public void setAnnotationType(String annotationType) {
        this.annotationType = annotationType;
    }

    public boolean isUseActualColumns() {
        return useActualColumns;
    }

    public void setUseActualColumns(boolean useActualColumns) {
        this.useActualColumns = useActualColumns;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public String getRelativePackage() {
        return relativePackage;
    }

    public void setRelativePackage(String relativePackage) {
        this.relativePackage = relativePackage;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getModulePath() {
        return modulePath;
    }

    public void setModulePath(String modulePath) {
        this.modulePath = modulePath;
    }

    public List<ModuleInfoGo> getModuleUIInfoList() {
        return moduleUIInfoList;
    }

    public void setModuleUIInfoList(List<ModuleInfoGo> moduleUIInfoList) {
        this.moduleUIInfoList = moduleUIInfoList;
    }

    public boolean isNeedToStringHashcodeEquals() {
        return needToStringHashcodeEquals;
    }

    public void setNeedToStringHashcodeEquals(boolean needToStringHashcodeEquals) {
        this.needToStringHashcodeEquals = needToStringHashcodeEquals;
    }


    public boolean isUseLombokPlugin() {
        return useLombokPlugin;
    }

    public void setUseLombokPlugin(boolean useLombokPlugin) {
        this.useLombokPlugin = useLombokPlugin;
    }

    public boolean isJsr310Support() {
        return jsr310Support;
    }

    public void setJsr310Support(boolean jsr310Support) {
        this.jsr310Support = jsr310Support;
    }


    public String getSuperClass() {
        return superClass;
    }

    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }

    public boolean isUseActualColumnAnnotationInject() {
        return useActualColumnAnnotationInject;
    }

    public void setUseActualColumnAnnotationInject(boolean useActualColumnAnnotationInject) {
        this.useActualColumnAnnotationInject = useActualColumnAnnotationInject;
    }

    public String getIgnoreTablePrefix() {
        return ignoreTablePrefix;
    }

    public void setIgnoreTablePrefix(String ignoreTablePrefix) {
        this.ignoreTablePrefix = ignoreTablePrefix;
    }

    public String getIgnoreTableSuffix() {
        return ignoreTableSuffix;
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
            ", useActualColumnAnnotationInject=" + useActualColumnAnnotationInject +
            ", templatesName='" + templatesName + '\'' +
            ", extraTemplateNames=" + moduleUIInfoList +
            '}';
    }

    public String getIgnoreFieldPrefix() {
        return ignoreFieldPrefix;
    }

    public void setIgnoreFieldPrefix(String ignoreFieldPrefix) {
        this.ignoreFieldPrefix = ignoreFieldPrefix;
    }

    public String getIgnoreFieldSuffix() {
        return ignoreFieldSuffix;
    }

    public void setIgnoreFieldSuffix(String ignoreFieldSuffix) {
        this.ignoreFieldSuffix = ignoreFieldSuffix;
    }
}
