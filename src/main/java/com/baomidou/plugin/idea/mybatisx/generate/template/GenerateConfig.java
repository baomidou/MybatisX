package com.baomidou.plugin.idea.mybatisx.generate.template;

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

    private String packageName;
    private String domainObjectName;
    /**
     * 模块的源码相对路径
     */
    private String basePath;

    private String modulePath;

    /**
     * 需要生成 tostring,hashcode,equals
     */
    private boolean needToStringHashcodeEquals;
    /**
     * 需要生成实体类注释
     */
    private boolean needsComment;
    /**
     * 需要生成mapper注解
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
     * @Repository
     */
//    private boolean repositoryAnnotation;


    private boolean useLombokPlugin;
    private boolean useActualColumns;
    private boolean jsr310Support;
    private List<String> extraTemplateNames;


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

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
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

}
