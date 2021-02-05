package com.baomidou.plugin.idea.mybatisx.generate.dto;

import java.util.List;

public class GenerateConfig {
    /**
     * 界面恢复
     */
    private String moduleName;

    private String annotationType;

    /***
     *
     */
    private String targetProject;
    private String tableName;

    private String basePackage;
    private String relativePackage;
    private String domainObjectName;
    private String encoding;
    /**
     * 模块的源码相对路径
     */
    private String basePath;

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

    /**
     * 需要生成repository注解
     *
     * @Repository
     */
//    private boolean repositoryAnnotation;


    private boolean useLombokPlugin;
    private boolean useActualColumns;
    private boolean jsr310Support;
    /**
     * 模板组名称
     */
    private String templatesName;
    /**
     * 已选择的模板名称
     */
    private List<String> extraTemplateNames;

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

    public String getTargetProject() {
        return targetProject;
    }

    public void setTargetProject(String targetProject) {
        this.targetProject = targetProject;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
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

    public String getDomainObjectName() {
        return domainObjectName;
    }

    public void setDomainObjectName(String domainObjectName) {
        this.domainObjectName = domainObjectName;
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

    public List<String> getExtraTemplateNames() {
        return extraTemplateNames;
    }

    public void setExtraTemplateNames(List<String> extraTemplateNames) {
        this.extraTemplateNames = extraTemplateNames;
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

    @Override
    public String toString() {
        return "GenerateConfig{" +
            "moduleName='" + moduleName + '\'' +
            ", annotationType='" + annotationType + '\'' +
            ", targetProject='" + targetProject + '\'' +
            ", tableName='" + tableName + '\'' +
            ", basePackage='" + basePackage + '\'' +
            ", relativePackage='" + relativePackage + '\'' +
            ", domainObjectName='" + domainObjectName + '\'' +
            ", encoding='" + encoding + '\'' +
            ", basePath='" + basePath + '\'' +
            ", modulePath='" + modulePath + '\'' +
            ", needToStringHashcodeEquals=" + needToStringHashcodeEquals +
            ", needsComment=" + needsComment +
            ", useLombokPlugin=" + useLombokPlugin +
            ", useActualColumns=" + useActualColumns +
            ", jsr310Support=" + jsr310Support +
            ", templatesName='" + templatesName + '\'' +
            ", extraTemplateNames=" + extraTemplateNames +
            '}';
    }
}
