package com.baomidou.plugin.idea.mybatisx.generate;


import com.baomidou.plugin.idea.mybatisx.generate.plugin.CommonDAOInterfacePlugin;
import com.baomidou.plugin.idea.mybatisx.generate.plugin.DaoEntityAnnotationInterfacePlugin;
import com.baomidou.plugin.idea.mybatisx.generate.plugin.DbRemarksCommentGenerator;
import com.baomidou.plugin.idea.mybatisx.generate.plugin.IntellijMyBatisGenerator;
import com.baomidou.plugin.idea.mybatisx.generate.plugin.MySQLForUpdatePlugin;
import com.baomidou.plugin.idea.mybatisx.generate.plugin.MySQLLimitPlugin;
import com.baomidou.plugin.idea.mybatisx.generate.plugin.RepositoryPlugin;
import com.baomidou.plugin.idea.mybatisx.generate.plugin.helper.IntellijTableInfo;
import com.baomidou.plugin.idea.mybatisx.generate.plugin.helper.MergeJavaCallBack;
import com.baomidou.plugin.idea.mybatisx.model.Config;
import com.baomidou.plugin.idea.mybatisx.model.DbType;
import com.baomidou.plugin.idea.mybatisx.setting.PersistentConfig;
import com.baomidou.plugin.idea.mybatisx.util.DbToolsUtils;
import com.baomidou.plugin.idea.mybatisx.util.GeneratorCallback;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.google.common.base.Strings;
import com.intellij.database.model.RawConnectionConfig;
import com.intellij.database.psi.DbTable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.config.CommentGeneratorConfiguration;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.JavaTypeResolverConfiguration;
import org.mybatis.generator.config.ModelType;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.plugins.EqualsHashCodePlugin;
import org.mybatis.generator.plugins.MapperAnnotationPlugin;
import org.mybatis.generator.plugins.ToStringPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 生成mybatis相关代码
 */
public class MybatisGenerator {
    private static Logger logger = LoggerFactory.getLogger(MybatisGenerator.class);
    private String currentDbName;
    private Project project;
    private PersistentConfig persistentConfig;//持久化的配置
    private Config config;//界面默认配置
    private DbType dbType;//数据库类型
    private IntellijTableInfo intellijTableInfo;

    /**
     * Instantiates a new Mybatis generator.
     *
     * @param config the config
     */
    public MybatisGenerator(Config config) {
        this.config = config;
    }

    /**
     * 自动生成的主逻辑
     *
     * @param anActionEvent the an action event
     * @param saveConfig    the save config
     * @return the list
     * @throws Exception the exception
     */
    public List<String> execute(final PsiElement psiElement, Project project, boolean saveConfig) throws Exception {
        List<String> result = new ArrayList<>();
        this.project = project;
        this.persistentConfig = PersistentConfig.getInstance(project);

        if (saveConfig) {
            saveConfig();//执行前 先保存一份当前配置
        }

        if (!(psiElement instanceof DbTable)) {
            result.add("can not generate! \nplease select table");
            return result;
        }

        intellijTableInfo = DbToolsUtils.buildIntellijTableInfo((DbTable) psiElement);

        RawConnectionConfig connectionConfig = ((DbTable) psiElement).getDataSource().getConnectionConfig();

        String driverClass = connectionConfig.getDriverClass();
        if (driverClass.contains("mysql")) {
            currentDbName = ((DbTable) psiElement).getParent().getName();
            dbType = DbType.MySQL;
        } else if (driverClass.contains("oracle")) {
            currentDbName = ((DbTable) psiElement).getParent().getName();
            dbType = DbType.Oracle;
        } else if (driverClass.contains("postgresql")) {
            currentDbName = ((DbTable) psiElement).getParent().getParent().getName();
            dbType = DbType.PostgreSQL;
        } else if (driverClass.contains("sqlserver")) {
            currentDbName = ((DbTable) psiElement).getParent().getName();
            dbType = DbType.SqlServer;
        } else if (driverClass.contains("mariadb")) {
            currentDbName = ((DbTable) psiElement).getParent().getName();
            dbType = DbType.MariaDB;
        } else {
            String failMessage = String.format("db type not support!" +
                    "\n your driver class:%s" +
                    "\n current support db type:mysql，mariadb，oracle,postgresql",
                driverClass);
            Messages.showMessageDialog(project, failMessage,
                "Test Connection Error", Messages.getInformationIcon());
            return result;
        }

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {

                Configuration configuration = new Configuration();
                Context context = new Context(ModelType.CONDITIONAL) {
                    @Override
                    public void validate(List<String> errors) {

                    }
                };
                configuration.addContext(context);

                context.setId("myid");
                context.addProperty("autoDelimitKeywords", "true");

                if (DbType.MySQL.equals(dbType) || DbType.MariaDB.equals(dbType)) {
                    // 由于beginningDelimiter和endingDelimiter的默认值为双引号(")，在Mysql中不能这么写，所以还要将这两个默认值改为`
                    context.addProperty("beginningDelimiter", "`");
                    context.addProperty("endingDelimiter", "`");
                }

                context.addProperty("javaFileEncoding", "UTF-8");
                context.addProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING, "UTF-8");
                context.setTargetRuntime("MyBatis3");

                TableConfiguration tableConfig = buildTableConfig(context);
                JavaModelGeneratorConfiguration modelConfig = buildModelConfig();
                SqlMapGeneratorConfiguration mapperConfig = buildMapperXmlConfig();
                JavaClientGeneratorConfiguration daoConfig = buildDaoConfig();
                CommentGeneratorConfiguration commentConfig = buildCommentConfig();

                context.addTableConfiguration(tableConfig);
                context.setJavaModelGeneratorConfiguration(modelConfig);
                context.setSqlMapGeneratorConfiguration(mapperConfig);
                context.setJavaClientGeneratorConfiguration(daoConfig);
                context.setCommentGeneratorConfiguration(commentConfig);
                addPluginConfiguration(context);

                createFolderForNeed(config);
                List<String> warnings = new ArrayList<>();
                ShellCallback shellCallback = new MergeJavaCallBack(true);
                Set<String> fullyqualifiedTables = new HashSet<>();
                Set<String> contexts = new HashSet<>();
                try {
                    IntellijMyBatisGenerator intellijMyBatisGenerator = new IntellijMyBatisGenerator(configuration, shellCallback, warnings);
                    intellijMyBatisGenerator.generate(new GeneratorCallback(), contexts, fullyqualifiedTables, intellijTableInfo);
                    if (!warnings.isEmpty()) {
                        result.addAll(warnings);
                    }
                } catch (Exception e) {
                    result.add(e.getMessage());
                }
                if (!config.getSimpleMode()) {
                    if (intellijTableInfo.getPrimaryKeyColumns().size() == 0) {
                        Messages.showWarningDialog("the table does not exists primary key, can not generate method like ByPrimaryKey", "Generate Info");
                    }
                }
                VirtualFile virtualFile = ProjectUtil.guessProjectDir(project);
                virtualFile.refresh(true, true);
            }
        });
        return result;
    }

    /**
     * 创建所需目录
     *
     * @param config
     */
    private void createFolderForNeed(Config config) {

        String modelMvnPath = config.getModelMvnPath();
        String daoMvnPath = config.getDaoMvnPath();
        String xmlMvnPath = config.getXmlMvnPath();

        String modelPath = config.getProjectFolder() + "/" + modelMvnPath + "/";
        String daoPath = config.getProjectFolder() + "/" + daoMvnPath + "/";
        String xmlPath = config.getProjectFolder() + "/" + xmlMvnPath + "/";

        File modelFile = new File(modelPath);
        if (!modelFile.exists() && !modelFile.isDirectory()) {
            modelFile.mkdirs();
        }

        File daoFile = new File(daoPath);
        if (!daoFile.exists() && !daoFile.isDirectory()) {
            daoFile.mkdirs();
        }

        File xmlFile = new File(xmlPath);
        if (!xmlFile.exists() && !xmlFile.isDirectory()) {
            xmlFile.mkdirs();
        }

    }


    /**
     * 保存当前配置到历史记录
     */
    private void saveConfig() {
        Map<String, Config> historyConfigList = persistentConfig.getHistoryConfigList();
        if (historyConfigList == null) {
            historyConfigList = new HashMap<>();
        }

        String daoName = config.getDaoName();
        String modelName = config.getModelName();
        String daoPostfix = daoName.replace(modelName, "");
        config.setDaoPostfix(daoPostfix);

        historyConfigList.put(config.getName(), config);
        persistentConfig.setHistoryConfigList(historyConfigList);

    }


    /**
     * 生成table配置
     *
     * @param context
     * @return
     */
    private TableConfiguration buildTableConfig(Context context) {
        boolean simpleMode = config.getSimpleMode();
        TableConfiguration tableConfig = new TableConfiguration(context) {
            @Override
            public boolean areAnyStatementsEnabled() {
                return simpleMode || super.areAnyStatementsEnabled();
            }
        };
        tableConfig.setTableName(config.getTableName());
        tableConfig.setDomainObjectName(config.getModelName());
        String schema;
        if (!Strings.isNullOrEmpty(currentDbName)) {
            schema = currentDbName;
        } else {
            throw new RuntimeException("can not find schema");

        }
        if (dbType.equals(DbType.MySQL)
            || dbType.equals(DbType.MariaDB)
            || dbType.equals(DbType.PostgreSQL)) {
            tableConfig.setSchema(schema);
        } else {
            tableConfig.setCatalog(schema);
        }

        if (!config.isUseExample()) {
            tableConfig.setUpdateByExampleStatementEnabled(false);
            tableConfig.setCountByExampleStatementEnabled(false);
            tableConfig.setDeleteByExampleStatementEnabled(false);
            tableConfig.setSelectByExampleStatementEnabled(false);
        }
        if (config.isUseSchemaPrefix()) {
            if (DbType.MySQL.equals(dbType)) {
                tableConfig.setSchema(schema);
            } else if (DbType.Oracle.equals(dbType)) {
                tableConfig.setSchema(schema);
            } else {
                tableConfig.setCatalog(schema);
            }
        }

        if (DbType.PostgreSQL.equals(dbType)) {
            tableConfig.setDelimitIdentifiers(true);
        }

        if (!StringUtils.isEmpty(config.getPrimaryKey())) {
            tableConfig.setGeneratedKey(new GeneratedKey(config.getPrimaryKey(), "JDBC", true, null));
        }

        if (config.isUseActualColumnNames()) {
            tableConfig.addProperty("useActualColumnNames", "true");
        }

        if (config.isUseTableNameAlias()) {
            tableConfig.setAlias(config.getTableName());
        }

        if (simpleMode) {
            tableConfig.setInsertStatementEnabled(false);
            tableConfig.setSelectByPrimaryKeyStatementEnabled(false);
            tableConfig.setUpdateByPrimaryKeyStatementEnabled(false);
            tableConfig.setDeleteByPrimaryKeyStatementEnabled(false);
        }

        tableConfig.setMapperName(config.getDaoName());
        return tableConfig;
    }


    /**
     * 生成实体类配置
     *
     * @return
     */
    private JavaModelGeneratorConfiguration buildModelConfig() {
        String projectFolder = config.getProjectFolder();
        String modelPackage = config.getModelPackage();
        String modelPackageTargetFolder = config.getModelTargetFolder();
        String modelMvnPath = config.getModelMvnPath();

        JavaModelGeneratorConfiguration modelConfig = new JavaModelGeneratorConfiguration();

        if (!StringUtils.isEmpty(modelPackage)) {
            modelConfig.setTargetPackage(modelPackage);
        } else {
            modelConfig.setTargetPackage("generator");
        }

        if (!StringUtils.isEmpty(modelPackageTargetFolder)) {
            modelConfig.setTargetProject(modelPackageTargetFolder + "/" + modelMvnPath + "/");
        } else {
            modelConfig.setTargetProject(projectFolder + "/" + modelMvnPath + "/");
        }
        return modelConfig;
    }

    /**
     * 生成mapper.xml文件配置
     *
     * @return
     */
    private SqlMapGeneratorConfiguration buildMapperXmlConfig() {
        String projectFolder = config.getProjectFolder();
        String mappingXMLPackage = config.getXmlPackage();
        String mappingXMLTargetFolder = config.getProjectFolder();
        String xmlMvnPath = config.getXmlMvnPath();

        SqlMapGeneratorConfiguration mapperConfig = new SqlMapGeneratorConfiguration();

        if (!StringUtils.isEmpty(mappingXMLPackage)) {
            mapperConfig.setTargetPackage(mappingXMLPackage);
        } else {
            mapperConfig.setTargetPackage("generator");
        }

        if (!StringUtils.isEmpty(mappingXMLTargetFolder)) {
            mapperConfig.setTargetProject(mappingXMLTargetFolder + "/" + xmlMvnPath + "/");
        } else {
            mapperConfig.setTargetProject(projectFolder + "/" + xmlMvnPath + "/");
        }


        return mapperConfig;
    }

    /**
     * 生成dao接口文件配置
     *
     * @return
     */
    private JavaClientGeneratorConfiguration buildDaoConfig() {

        String projectFolder = config.getProjectFolder();
        String daoPackage = config.getDaoPackage();
        String daoTargetFolder = config.getDaoTargetFolder();
        String daoMvnPath = config.getDaoMvnPath();

        JavaClientGeneratorConfiguration daoConfig = new JavaClientGeneratorConfiguration();
        daoConfig.setConfigurationType("XMLMAPPER");
        daoConfig.setTargetPackage(daoPackage);

        if (!StringUtils.isEmpty(daoPackage)) {
            daoConfig.setTargetPackage(daoPackage);
        } else {
            daoConfig.setTargetPackage("generator");
        }

        if (!StringUtils.isEmpty(daoTargetFolder)) {
            daoConfig.setTargetProject(daoTargetFolder + "/" + daoMvnPath + "/");
        } else {
            daoConfig.setTargetProject(projectFolder + "/" + daoMvnPath + "/");
        }

        return daoConfig;
    }

    /**
     * 生成注释配置
     *
     * @return
     */
    private CommentGeneratorConfiguration buildCommentConfig() {
        CommentGeneratorConfiguration commentConfig = new CommentGeneratorConfiguration();
        commentConfig.setConfigurationType(DbRemarksCommentGenerator.class.getName());

        if (config.isComment()) {
            commentConfig.addProperty("columnRemarks", "true");
        }
        if (config.isAnnotation()) {
            commentConfig.addProperty("annotations", "true");
        }

        return commentConfig;
    }

    /**
     * 添加相关插件（注意插件文件需要通过jar引入）
     *
     * @param context
     */
    private void addPluginConfiguration(Context context) {


        //实体添加序列化
        PluginConfiguration serializablePlugin = new PluginConfiguration();
        serializablePlugin.addProperty("type", "org.mybatis.generator.plugins.SerializablePlugin");
        serializablePlugin.setConfigurationType("org.mybatis.generator.plugins.SerializablePlugin");
        context.addPluginConfiguration(serializablePlugin);

        PluginConfiguration daoEntityAnnotationPlugin = new PluginConfiguration();
        daoEntityAnnotationPlugin.setConfigurationType(DaoEntityAnnotationInterfacePlugin.class.getName());
        daoEntityAnnotationPlugin.setConfigurationType(DaoEntityAnnotationInterfacePlugin.class.getName());
        String domainObjectName = context.getTableConfigurations().get(0).getDomainObjectName();
        String targetPackage = context.getJavaModelGeneratorConfiguration().getTargetPackage();
        daoEntityAnnotationPlugin.addProperty("domainName", targetPackage + "." + domainObjectName);
        context.addPluginConfiguration(daoEntityAnnotationPlugin);


        if (config.isNeedToStringHashcodeEquals()) {
            PluginConfiguration equalsHashCodePlugin = new PluginConfiguration();
            equalsHashCodePlugin.addProperty("type", EqualsHashCodePlugin.class.getName());
            equalsHashCodePlugin.setConfigurationType(EqualsHashCodePlugin.class.getName());
            context.addPluginConfiguration(equalsHashCodePlugin);
            PluginConfiguration toStringPluginPlugin = new PluginConfiguration();
            toStringPluginPlugin.addProperty("type", ToStringPlugin.class.getName());
            toStringPluginPlugin.setConfigurationType(ToStringPlugin.class.getName());
            context.addPluginConfiguration(toStringPluginPlugin);
        }

        if (config.isNeedMapperAnnotation()) {
            PluginConfiguration mapperAnnotation = new PluginConfiguration();
            mapperAnnotation.addProperty("type", MapperAnnotationPlugin.class.getName());
            mapperAnnotation.setConfigurationType(MapperAnnotationPlugin.class.getName());
            context.addPluginConfiguration(mapperAnnotation);
        }


        // limit/offset插件
        if (config.isOffsetLimit()) {
            if (DbType.MySQL.equals(dbType)
                || DbType.PostgreSQL.equals(dbType)) {
                PluginConfiguration mySQLLimitPlugin = new PluginConfiguration();
                mySQLLimitPlugin.addProperty("type", MySQLLimitPlugin.class.getName());
                mySQLLimitPlugin.setConfigurationType(MySQLLimitPlugin.class.getName());
                context.addPluginConfiguration(mySQLLimitPlugin);
            }
        }

        //for JSR310
        if (config.isJsr310Support()) {
            JavaTypeResolverConfiguration javaTypeResolverPlugin = new JavaTypeResolverConfiguration();
            context.setJavaTypeResolverConfiguration(javaTypeResolverPlugin);
        }

        //forUpdate 插件
        if (config.isNeedForUpdate()) {
            if (DbType.MySQL.equals(dbType)
                || DbType.PostgreSQL.equals(dbType)) {
                PluginConfiguration mySQLForUpdatePlugin = new PluginConfiguration();
                mySQLForUpdatePlugin.addProperty("type", MySQLForUpdatePlugin.class.getName());
                mySQLForUpdatePlugin.setConfigurationType(MySQLForUpdatePlugin.class.getName());
                context.addPluginConfiguration(mySQLForUpdatePlugin);
            }
        }

        //repository 插件
        if (config.isAnnotationDAO()) {
            PluginConfiguration repositoryPlugin = new PluginConfiguration();
            repositoryPlugin.addProperty("type", RepositoryPlugin.class.getName());
            repositoryPlugin.setConfigurationType(RepositoryPlugin.class.getName());
            context.addPluginConfiguration(repositoryPlugin);
        }

        if (config.isUseDAOExtendStyle()) {
            if (DbType.MySQL.equals(dbType)
                || DbType.PostgreSQL.equals(dbType)) {
                PluginConfiguration commonDAOInterfacePlugin = new PluginConfiguration();
                commonDAOInterfacePlugin.addProperty("type", CommonDAOInterfacePlugin.class.getName());
                commonDAOInterfacePlugin.setConfigurationType(CommonDAOInterfacePlugin.class.getName());
                context.addPluginConfiguration(commonDAOInterfacePlugin);
            }
        }
        // Lombok 插件
        if (config.isUseLombokPlugin()) {
            PluginConfiguration pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.addProperty("type", "com.softwareloop.mybatis.generator.plugins.LombokPlugin");
            pluginConfiguration.setConfigurationType("com.softwareloop.mybatis.generator.plugins.LombokPlugin");
            context.addPluginConfiguration(pluginConfiguration);
        }

    }

    /**
     * 获取xml文件路径 用以删除之前的xml
     *
     * @param config
     * @return
     */
    private String getMappingXMLFilePath(Config config) {
        StringBuilder sb = new StringBuilder();
        String mappingXMLPackage = config.getXmlPackage();
        String mappingXMLTargetFolder = config.getProjectFolder();
        String xmlMvnPath = config.getXmlMvnPath();
        sb.append(mappingXMLTargetFolder + "/" + xmlMvnPath + "/");

        if (!StringUtils.isEmpty(mappingXMLPackage)) {
            sb.append(mappingXMLPackage.replace(".", "/")).append("/");
        }
        if (!StringUtils.isEmpty(config.getDaoName())) {
            sb.append(config.getDaoName()).append(".xml");
        } else {
            sb.append(config.getModelName()).append("Dao.xml");
        }

        return sb.toString();
    }
}
