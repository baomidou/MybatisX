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
     * 是否生成Mapper注解
     */
    private boolean needMapperAnnotation;

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

    /**
     * Is use lombok plugin boolean.
     *
     * @return the boolean
     */
    public boolean isUseLombokPlugin() {
		return useLombokPlugin;
	}

    /**
     * Sets use lombok plugin.
     *
     * @param useLombokPlugin the use lombok plugin
     */
    public void setUseLombokPlugin(boolean useLombokPlugin) {
		this.useLombokPlugin = useLombokPlugin;
	}

    /**
     * Is jsr 310 support boolean.
     *
     * @return the boolean
     */
    public boolean isJsr310Support() {
        return jsr310Support;
    }

    /**
     * Sets jsr 310 support.
     *
     * @param jsr310Support the jsr 310 support
     */
    public void setJsr310Support(boolean jsr310Support) {
        this.jsr310Support = jsr310Support;
    }

    /**
     * Is use schema prefix boolean.
     *
     * @return the boolean
     */
    public boolean isUseSchemaPrefix() {
        return useSchemaPrefix;
    }

    /**
     * Sets use schema prefix.
     *
     * @param useSchemaPrefix the use schema prefix
     */
    public void setUseSchemaPrefix(boolean useSchemaPrefix) {
        this.useSchemaPrefix = useSchemaPrefix;
    }

    /**
     * Is use example boolean.
     *
     * @return the boolean
     */
    public boolean isUseExample() {
		return useExample;
	}

    /**
     * Sets use example.
     *
     * @param useExample the use example
     */
    public void setUseExample(boolean useExample) {
		this.useExample = useExample;
	}

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
		return name;
	}

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
		this.name = name;
	}

    /**
     * Gets table name.
     *
     * @return the table name
     */
    public String getTableName() {
		return tableName;
	}

    /**
     * Sets table name.
     *
     * @param tableName the table name
     */
    public void setTableName(String tableName) {
		this.tableName = tableName;
	}

    /**
     * Gets model name.
     *
     * @return the model name
     */
    public String getModelName() {
		return modelName;
	}

    /**
     * Sets model name.
     *
     * @param modelName the model name
     */
    public void setModelName(String modelName) {
		this.modelName = modelName;
	}

    /**
     * Gets connector jar path.
     *
     * @return the connector jar path
     */
    public String getConnectorJarPath() {
		return connectorJarPath;
	}

    /**
     * Sets connector jar path.
     *
     * @param connectorJarPath the connector jar path
     */
    public void setConnectorJarPath(String connectorJarPath) {
		this.connectorJarPath = connectorJarPath;
	}

    /**
     * Gets project folder.
     *
     * @return the project folder
     */
    public String getProjectFolder() {
		return projectFolder;
	}

    /**
     * Sets project folder.
     *
     * @param projectFolder the project folder
     */
    public void setProjectFolder(String projectFolder) {
		this.projectFolder = projectFolder;
	}

    /**
     * Gets model package.
     *
     * @return the model package
     */
    public String getModelPackage() {
		return modelPackage;
	}

    /**
     * Sets model package.
     *
     * @param modelPackage the model package
     */
    public void setModelPackage(String modelPackage) {
		this.modelPackage = modelPackage;
	}

    /**
     * Gets model target folder.
     *
     * @return the model target folder
     */
    public String getModelTargetFolder() {
		return modelTargetFolder;
	}

    /**
     * Sets model target folder.
     *
     * @param modelTargetFolder the model target folder
     */
    public void setModelTargetFolder(String modelTargetFolder) {
		this.modelTargetFolder = modelTargetFolder;
	}

    /**
     * Gets dao package.
     *
     * @return the dao package
     */
    public String getDaoPackage() {
		return daoPackage;
	}

    /**
     * Sets dao package.
     *
     * @param daoPackage the dao package
     */
    public void setDaoPackage(String daoPackage) {
		this.daoPackage = daoPackage;
	}

    /**
     * Gets dao target folder.
     *
     * @return the dao target folder
     */
    public String getDaoTargetFolder() {
		return daoTargetFolder;
	}

    /**
     * Sets dao target folder.
     *
     * @param daoTargetFolder the dao target folder
     */
    public void setDaoTargetFolder(String daoTargetFolder) {
		this.daoTargetFolder = daoTargetFolder;
	}

    /**
     * Gets xml package.
     *
     * @return the xml package
     */
    public String getXmlPackage() {
		return xmlPackage;
	}

    /**
     * Sets xml package.
     *
     * @param xmlPackage the xml package
     */
    public void setXmlPackage(String xmlPackage) {
		this.xmlPackage = xmlPackage;
	}

    /**
     * Is offset limit boolean.
     *
     * @return the boolean
     */
    public boolean isOffsetLimit() {
		return offsetLimit;
	}

    /**
     * Sets offset limit.
     *
     * @param offsetLimit the offset limit
     */
    public void setOffsetLimit(boolean offsetLimit) {
		this.offsetLimit = offsetLimit;
	}

    /**
     * Is comment boolean.
     *
     * @return the boolean
     */
    public boolean isComment() {
		return comment;
	}

    /**
     * Sets comment.
     *
     * @param comment the comment
     */
    public void setComment(boolean comment) {
		this.comment = comment;
	}

    /**
     * Is need to string hashcode equals boolean.
     *
     * @return the boolean
     */
    public boolean isNeedToStringHashcodeEquals() {
        return needToStringHashcodeEquals;
    }

    public boolean isNeedMapperAnnotation() {
        return needMapperAnnotation;
    }

    public void setNeedMapperAnnotation(boolean needMapperAnnotation) {
        this.needMapperAnnotation = needMapperAnnotation;
    }

    /**
     * Sets need to string hashcode equals.
     *
     * @param needToStringHashcodeEquals the need to string hashcode equals
     */
    public void setNeedToStringHashcodeEquals(boolean needToStringHashcodeEquals) {
        this.needToStringHashcodeEquals = needToStringHashcodeEquals;
    }

    /**
     * Is need for update boolean.
     *
     * @return the boolean
     */
    public boolean isNeedForUpdate() {
		return needForUpdate;
	}

    /**
     * Sets need for update.
     *
     * @param needForUpdate the need for update
     */
    public void setNeedForUpdate(boolean needForUpdate) {
		this.needForUpdate = needForUpdate;
	}

    /**
     * Is annotation dao boolean.
     *
     * @return the boolean
     */
    public boolean isAnnotationDAO() {
		return annotationDAO;
	}

    /**
     * Sets annotation dao.
     *
     * @param annotationDAO the annotation dao
     */
    public void setAnnotationDAO(boolean annotationDAO) {
		this.annotationDAO = annotationDAO;
	}

    /**
     * Is annotation boolean.
     *
     * @return the boolean
     */
    public boolean isAnnotation() {
		return annotation;
	}

    /**
     * Sets annotation.
     *
     * @param annotation the annotation
     */
    public void setAnnotation(boolean annotation) {
		this.annotation = annotation;
	}

    /**
     * Is use actual column names boolean.
     *
     * @return the boolean
     */
    public boolean isUseActualColumnNames() {
		return useActualColumnNames;
	}

    /**
     * Sets use actual column names.
     *
     * @param useActualColumnNames the use actual column names
     */
    public void setUseActualColumnNames(boolean useActualColumnNames) {
		this.useActualColumnNames = useActualColumnNames;
	}

    /**
     * Gets dao name.
     *
     * @return the dao name
     */
    public String getDaoName() {
		return daoName;
	}

    /**
     * Sets dao name.
     *
     * @param daoName the dao name
     */
    public void setDaoName(String daoName) {
		this.daoName = daoName;
	}

    /**
     * Gets primary key.
     *
     * @return the primary key
     */
    public String getPrimaryKey() {
		return primaryKey;
	}

    /**
     * Sets primary key.
     *
     * @param primaryKey the primary key
     */
    public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

    /**
     * Gets encoding.
     *
     * @return the encoding
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Sets encoding.
     *
     * @param encoding the encoding
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Gets use table name alias.
     *
     * @return the use table name alias
     */
    public boolean getUseTableNameAlias() {
		return useTableNameAlias;
	}

    /**
     * Sets use table name alias.
     *
     * @param useTableNameAlias the use table name alias
     */
    public void setUseTableNameAlias(boolean useTableNameAlias) {
		this.useTableNameAlias = useTableNameAlias;
	}

    /**
     * Is use table name alias boolean.
     *
     * @return the boolean
     */
    public boolean isUseTableNameAlias() {
		return useTableNameAlias;
	}

    /**
     * Is override xml boolean.
     *
     * @return the boolean
     */
    public boolean isOverrideXML() {
		return overrideXML;
	}

    /**
     * Sets override xml.
     *
     * @param overrideXML the override xml
     */
    public void setOverrideXML(boolean overrideXML) {
		this.overrideXML = overrideXML;
	}

    /**
     * Sets use dao extend style.
     *
     * @param useDAOExtendStyle the use dao extend style
     */
    public void setUseDAOExtendStyle(boolean useDAOExtendStyle) {
		this.useDAOExtendStyle = useDAOExtendStyle;
	}

    /**
     * Is use dao extend style boolean.
     *
     * @return the boolean
     */
    public boolean isUseDAOExtendStyle() {
		return useDAOExtendStyle;
	}

    /**
     * Gets model mvn path.
     *
     * @return the model mvn path
     */
    public String getModelMvnPath() {
		return modelMvnPath;
	}

    /**
     * Sets model mvn path.
     *
     * @param modelMvnPath the model mvn path
     */
    public void setModelMvnPath(String modelMvnPath) {
		this.modelMvnPath = modelMvnPath;
	}

    /**
     * Gets dao mvn path.
     *
     * @return the dao mvn path
     */
    public String getDaoMvnPath() {
		return daoMvnPath;
	}

    /**
     * Sets dao mvn path.
     *
     * @param daoMvnPath the dao mvn path
     */
    public void setDaoMvnPath(String daoMvnPath) {
		this.daoMvnPath = daoMvnPath;
	}

    /**
     * Gets xml mvn path.
     *
     * @return the xml mvn path
     */
    public String getXmlMvnPath() {
		return xmlMvnPath;
	}

    /**
     * Sets xml mvn path.
     *
     * @param xmlMvnPath the xml mvn path
     */
    public void setXmlMvnPath(String xmlMvnPath) {
		this.xmlMvnPath = xmlMvnPath;
	}

    /**
     * Gets dao postfix.
     *
     * @return the dao postfix
     */
    public String getDaoPostfix() {
		return daoPostfix;
	}

    /**
     * Sets dao postfix.
     *
     * @param daoPostfix the dao postfix
     */
    public void setDaoPostfix(String daoPostfix) {
		this.daoPostfix = daoPostfix;
	}


    /**
     * Is override java boolean.
     *
     * @return the boolean
     */
    public boolean isOverrideJava() {
		return overrideJava;
	}

    /**
     * Sets override java.
     *
     * @param overrideJava the override java
     */
    public void setOverrideJava(boolean overrideJava) {
		this.overrideJava = overrideJava;
	}
}
