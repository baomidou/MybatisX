package com.baomidou.plugin.idea.mybatisx.model;

/**
 * 界面配置
 */
public class Config {

	/**
	 * 配置名称
	 */
	private String name;

	/**
	 * 表名
	 */
	private String tableName;

	/**
	 * 主键
	 */
	private String primaryKey;

	/**
	 * 实体名
	 */
	private String modelName;

	/**
	 * dao名称
	 */
	private String daoName;

	/**
	 * dao后缀
	 */
	private String daoPostfix;

	/**
	 * 工程目录
	 */
	private String projectFolder;

	private String modelPackage;
	private String modelTargetFolder;
	private String modelMvnPath;

	private String daoPackage;
	private String daoTargetFolder;
	private String daoMvnPath;

	private String xmlPackage;
	private String xmlMvnPath;

	/**
	 * 是否分页
	 */
	private boolean offsetLimit;

	/**
	 * 是否生成实体注释（来自表）
	 */
	private boolean comment;

	/**
	 * 是否覆盖原xml
	 */
	private boolean overrideXML;

	/**
	 * 是否覆盖原java
	 */
	private boolean overrideJava;

	/**
	 * 是否生成toString/hashCode/equals方法
	 */
	private boolean needToStringHashcodeEquals;

	/**
	 * 是否使用Schema前缀
	 */
	private boolean useSchemaPrefix;

	/**
	 * 是否select 增加ForUpdate
	 */
	private boolean needForUpdate;

	/**
	 * 是否DAO使用 @Repository 注解
	 */
	private boolean annotationDAO;

	/**
	 * 是否DAO方法抽出到公共父接口
	 */
	private boolean useDAOExtendStyle;

	/**
	 * 是否JSR310: Date and Time API
	 */
	private boolean jsr310Support;

	/**
	 * 是否生成JPA注解
	 */
	private boolean annotation;

	/**
	 * 是否使用实际的列名
	 */
	private boolean useActualColumnNames;

	/**
	 * 是否启用as别名查询
	 */
	private boolean useTableNameAlias;

	/**
	 * 是否使用Example
	 */
	private boolean useExample;


	private String encoding;
	private String connectorJarPath;
	private boolean useLombokPlugin;

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

    public boolean isUseSchemaPrefix() {
        return useSchemaPrefix;
    }

    public void setUseSchemaPrefix(boolean useSchemaPrefix) {
        this.useSchemaPrefix = useSchemaPrefix;
    }

	public boolean isUseExample() {
		return useExample;
	}

	public void setUseExample(boolean useExample) {
		this.useExample = useExample;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getConnectorJarPath() {
		return connectorJarPath;
	}

	public void setConnectorJarPath(String connectorJarPath) {
		this.connectorJarPath = connectorJarPath;
	}

	public String getProjectFolder() {
		return projectFolder;
	}

	public void setProjectFolder(String projectFolder) {
		this.projectFolder = projectFolder;
	}

	public String getModelPackage() {
		return modelPackage;
	}

	public void setModelPackage(String modelPackage) {
		this.modelPackage = modelPackage;
	}

	public String getModelTargetFolder() {
		return modelTargetFolder;
	}

	public void setModelTargetFolder(String modelTargetFolder) {
		this.modelTargetFolder = modelTargetFolder;
	}

	public String getDaoPackage() {
		return daoPackage;
	}

	public void setDaoPackage(String daoPackage) {
		this.daoPackage = daoPackage;
	}

	public String getDaoTargetFolder() {
		return daoTargetFolder;
	}

	public void setDaoTargetFolder(String daoTargetFolder) {
		this.daoTargetFolder = daoTargetFolder;
	}

	public String getXmlPackage() {
		return xmlPackage;
	}

	public void setXmlPackage(String xmlPackage) {
		this.xmlPackage = xmlPackage;
	}

	public boolean isOffsetLimit() {
		return offsetLimit;
	}

	public void setOffsetLimit(boolean offsetLimit) {
		this.offsetLimit = offsetLimit;
	}

	public boolean isComment() {
		return comment;
	}

	public void setComment(boolean comment) {
		this.comment = comment;
	}

    public boolean isNeedToStringHashcodeEquals() {
        return needToStringHashcodeEquals;
    }

    public void setNeedToStringHashcodeEquals(boolean needToStringHashcodeEquals) {
        this.needToStringHashcodeEquals = needToStringHashcodeEquals;
    }

	public boolean isNeedForUpdate() {
		return needForUpdate;
	}

	public void setNeedForUpdate(boolean needForUpdate) {
		this.needForUpdate = needForUpdate;
	}

	public boolean isAnnotationDAO() {
		return annotationDAO;
	}

	public void setAnnotationDAO(boolean annotationDAO) {
		this.annotationDAO = annotationDAO;
	}

	public boolean isAnnotation() {
		return annotation;
	}

	public void setAnnotation(boolean annotation) {
		this.annotation = annotation;
	}

	public boolean isUseActualColumnNames() {
		return useActualColumnNames;
	}

	public void setUseActualColumnNames(boolean useActualColumnNames) {
		this.useActualColumnNames = useActualColumnNames;
	}

	public String getDaoName() {
		return daoName;
	}

	public void setDaoName(String daoName) {
		this.daoName = daoName;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

	public boolean getUseTableNameAlias() {
		return useTableNameAlias;
	}

	public void setUseTableNameAlias(boolean useTableNameAlias) {
		this.useTableNameAlias = useTableNameAlias;
	}

	public boolean isUseTableNameAlias() {
		return useTableNameAlias;
	}

	public boolean isOverrideXML() {
		return overrideXML;
	}

	public void setOverrideXML(boolean overrideXML) {
		this.overrideXML = overrideXML;
	}

	public void setUseDAOExtendStyle(boolean useDAOExtendStyle) {
		this.useDAOExtendStyle = useDAOExtendStyle;
	}

	public boolean isUseDAOExtendStyle() {
		return useDAOExtendStyle;
	}

	public String getModelMvnPath() {
		return modelMvnPath;
	}

	public void setModelMvnPath(String modelMvnPath) {
		this.modelMvnPath = modelMvnPath;
	}

	public String getDaoMvnPath() {
		return daoMvnPath;
	}

	public void setDaoMvnPath(String daoMvnPath) {
		this.daoMvnPath = daoMvnPath;
	}

	public String getXmlMvnPath() {
		return xmlMvnPath;
	}

	public void setXmlMvnPath(String xmlMvnPath) {
		this.xmlMvnPath = xmlMvnPath;
	}

	public String getDaoPostfix() {
		return daoPostfix;
	}

	public void setDaoPostfix(String daoPostfix) {
		this.daoPostfix = daoPostfix;
	}


	public boolean isOverrideJava() {
		return overrideJava;
	}

	public void setOverrideJava(boolean overrideJava) {
		this.overrideJava = overrideJava;
	}
}
