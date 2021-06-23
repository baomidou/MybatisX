package com.baomidou.plugin.idea.mybatisx.generate.template;

import com.baomidou.plugin.idea.mybatisx.generate.dto.CustomTemplateRoot;
import com.baomidou.plugin.idea.mybatisx.generate.dto.DomainInfo;
import com.baomidou.plugin.idea.mybatisx.generate.dto.GenerateConfig;
import com.baomidou.plugin.idea.mybatisx.generate.dto.TemplateSettingDTO;
import com.baomidou.plugin.idea.mybatisx.generate.plugin.DaoEntityAnnotationInterfacePlugin;
import com.baomidou.plugin.idea.mybatisx.generate.plugin.IntellijMyBatisGenerator;
import com.baomidou.plugin.idea.mybatisx.generate.plugin.JavaTypeResolverJsr310Impl;
import com.baomidou.plugin.idea.mybatisx.generate.plugin.helper.IntellijTableInfo;
import com.baomidou.plugin.idea.mybatisx.generate.plugin.helper.MergeJavaCallBack;
import com.baomidou.plugin.idea.mybatisx.util.DbToolsUtils;
import com.baomidou.plugin.idea.mybatisx.util.JavaUtils;
import com.baomidou.plugin.idea.mybatisx.util.SpringStringUtils;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.intellij.database.psi.DbTable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.softwareloop.mybatis.generator.plugins.LombokPlugin;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.jetbrains.annotations.NotNull;
import org.mybatis.generator.config.ColumnRenamingRule;
import org.mybatis.generator.config.CommentGeneratorConfiguration;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.JavaTypeResolverConfiguration;
import org.mybatis.generator.config.ModelType;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.internal.NullProgressCallback;
import org.mybatis.generator.plugins.EqualsHashCodePlugin;
import org.mybatis.generator.plugins.SerializablePlugin;
import org.mybatis.generator.plugins.ToStringPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

/**
 * 代码生成入口
 */
public class GenerateCode {

    private static final Logger logger = LoggerFactory.getLogger(GenerateCode.class);
    public static final MergeJavaCallBack SHELL_CALLBACK = new MergeJavaCallBack(true);

    public static void generate(GenerateConfig generateConfig,
                                Map<String, List<TemplateSettingDTO>> templateSettingMap,
                                PsiElement psiElement) throws Exception {

        DbTable dbTable = null;
        if (!(psiElement instanceof DbTable)) {
            logger.info("生成代码出错,选择的元素不是表格类型, psiElement: {}", psiElement.getText());
            return;
        }
        dbTable = (DbTable) psiElement;
        // 对于多选的情况下, 如果表名不一致时, 按照规则生成类名
        String domainName = generateConfig.getDomainObjectName();
        String tableName = dbTable.getName();
        if (!generateConfig.getTableName().equalsIgnoreCase(tableName)) {
            domainName = StringUtils.dbStringToCamelStyle(tableName);
        }

        List<String> warnings = new ArrayList<>();
        Configuration config = new Configuration();

        Context context = new Context(ModelType.CONDITIONAL) {
            @Override
            public void validate(List<String> errors) {
            }
        };
        context.setId("MybatisXContext");
        context.addProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING, generateConfig.getEncoding());
        context.addProperty(PropertyRegistry.CONTEXT_JAVA_FORMATTER, CustomJavaFormatter.class.getName());

        JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
        String targetProject = generateConfig.getModulePath() + "/" + generateConfig.getBasePath();
        javaModelGeneratorConfiguration.setTargetProject(targetProject);
        javaModelGeneratorConfiguration.setTargetPackage(generateConfig.getBasePackage() + "." + generateConfig.getRelativePackage());
        context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);

        final List<ClassLoader> classLoaderList = new ArrayList<>();
        if (SpringStringUtils.hasText(generateConfig.getRootClass())) {
            javaModelGeneratorConfiguration.addProperty(PropertyRegistry.ANY_ROOT_CLASS, generateConfig.getRootClass());
            final Optional<PsiClass> psiClassOptional = JavaUtils.findClazz(psiElement.getProject(), generateConfig.getRootClass());
            psiClassOptional.ifPresent(psiClass -> classLoaderList.add(new MybatisXClassPathDynamicClassLoader(psiClass)));

        }

        buildTableConfiguration(generateConfig, context, tableName, domainName);

        CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();
        commentGeneratorConfiguration.setConfigurationType(CustomDefaultCommentGenerator.class.getName());
        commentGeneratorConfiguration.addProperty("suppressDate", "true");
        commentGeneratorConfiguration.addProperty("annotationType", generateConfig.getAnnotationType());

        context.setCommentGeneratorConfiguration(commentGeneratorConfiguration);

        JavaTypeResolverConfiguration javaTypeResolverConfiguration = new JavaTypeResolverConfiguration();
        javaTypeResolverConfiguration.addProperty("forceBigDecimals", "false");
        context.setJavaTypeResolverConfiguration(javaTypeResolverConfiguration);
        final List<TemplateSettingDTO> templateSettingDTOS = templateSettingMap.get(generateConfig.getTemplatesName());
        if (templateSettingDTOS == null) {
            logger.error("未选择模板组名称, templatesName: {}", generateConfig.getTemplatesName());
            return;
        }
        // 根据模板生成代码的插件
        configExtraPlugin(generateConfig, context, domainName, templateSettingDTOS);
        // 界面配置的扩展插件
        addPluginConfiguration(context, generateConfig);
        config.addContext(context);


        IntellijTableInfo intellijTableInfo = DbToolsUtils.buildIntellijTableInfo(dbTable);
        Set<String> contexts = new HashSet<>();
        Set<String> fullyqualifiedTables = new HashSet<>();
        IntellijMyBatisGenerator intellijMyBatisGenerator = new IntellijMyBatisGenerator(config, SHELL_CALLBACK, warnings);


        intellijMyBatisGenerator.generate(new NullProgressCallback(), contexts, fullyqualifiedTables, true, classLoaderList, intellijTableInfo);
    }

    private static void buildTableConfiguration(GenerateConfig generateConfig, Context context, @NotNull String tableName, String domainName) {
        TableConfiguration tc = new TableConfiguration(context);
        tc.setTableName(tableName);
        tc.setDomainObjectName(domainName);


        if (generateConfig.isUseActualColumns()) {
            tc.addProperty("useActualColumnNames", "true");
        }

        if (generateConfig.isUseActualColumnAnnotationInject()) {
            tc.addProperty("useActualColumnAnnotationInject", "true");
        }
        boolean replaceNecessary = false;
        StringJoiner stringJoiner = new StringJoiner("|");
        if (!StringUtils.isEmpty(generateConfig.getRemovedPrefix())) {
            stringJoiner.add("^" + generateConfig.getRemovedPrefix());
            replaceNecessary = true;
        }
        if (!StringUtils.isEmpty(generateConfig.getRemovedSuffix())) {
            stringJoiner.add(generateConfig.getRemovedSuffix()+"$");
            replaceNecessary = true;
        }
        if (replaceNecessary) {
            final ColumnRenamingRule columnRenamingRule = new ColumnRenamingRule();
            columnRenamingRule.setSearchString(stringJoiner.toString());
            columnRenamingRule.setReplaceString("");
            tc.setColumnRenamingRule(columnRenamingRule);
        }
        context.addTableConfiguration(tc);
    }

    private static void configExtraPlugin(GenerateConfig generateConfig,
                                          Context context,
                                          String domainName,
                                          List<TemplateSettingDTO> templateSettingDTOList) {
        CustomTemplateRoot templateRoot = buildRootConfig(generateConfig.getModulePath(),
            domainName,
            generateConfig.getBasePackage(),
            generateConfig.getRelativePackage(),
            generateConfig.getEncoding(),
            generateConfig.getBasePath(),
            templateSettingDTOList);

        for (String extraTemplateName : generateConfig.getExtraTemplateNames()) {
            Optional<TemplateSettingDTO> first = templateSettingDTOList.stream().filter(x -> x.getConfigName().equalsIgnoreCase(extraTemplateName)).findFirst();
            if (first.isPresent()) {
                TemplateSettingDTO templateSettingDTO = first.get();
                addPlugin(context, templateRoot, extraTemplateName, templateSettingDTO.getTemplateText());
            }

        }
    }

    private static void addPlugin(Context context, Serializable serializable, String javaService, String templateText) {
        PluginConfiguration serviceJavaPluginConfiguration = new PluginConfiguration();
        serviceJavaPluginConfiguration.setConfigurationType(CustomTemplatePlugin.class.getName());
        serviceJavaPluginConfiguration.addProperty(CustomTemplatePlugin.CURRENT_NAME, javaService);
        // 模板的内容
        serviceJavaPluginConfiguration.addProperty(CustomTemplatePlugin.TEMPLATE_TEXT, templateText);
        addRootMapToConfig(serializable, serviceJavaPluginConfiguration);
        context.addPluginConfiguration(serviceJavaPluginConfiguration);
    }

    private static void addRootMapToConfig(Serializable serializable, PluginConfiguration customPluginConfiguration) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(out)) {
            objectOutputStream.writeObject(serializable);
        } catch (IOException e) {
            logger.error("序列化数据失败",e);
            throw new RuntimeException("序列化数据失败");
        }
        byte[] encode = Base64.getEncoder().encode(out.toByteArray());
        customPluginConfiguration.addProperty(CustomTemplatePlugin.ROOT, new String(encode));
    }

    private static CustomTemplateRoot buildRootConfig(String modulePath,
                                                      String domainObjectName,
                                                      String basePackage,
                                                      String relativePackage,
                                                      String encoding,
                                                      String basePath,
                                                      List<TemplateSettingDTO> templateConfigs) {
        // 生成的模板参照 https://gitee.com/baomidou/SpringWind/tree/spring-mvc/SpringWind/src/main/java/com/baomidou/springwind/mapper
        CustomTemplateRoot customTemplateRoot = new CustomTemplateRoot();
        DomainInfo domainInfo = new DomainInfo();
        domainInfo.setFileName(domainObjectName);
        domainInfo.setBasePackage(basePackage);
        domainInfo.setRelativePackage(relativePackage);
        domainInfo.setEncoding(encoding);
        domainInfo.setBasePath(basePath);
        customTemplateRoot.setDomainInfo(domainInfo);
        customTemplateRoot.setModulePath(modulePath);

        for (TemplateSettingDTO templateSettingDTO : templateConfigs) {
            TemplateSettingDTO settingDTO = replaceWithModel(templateSettingDTO, domainInfo);
            customTemplateRoot.add(settingDTO);
        }

        return customTemplateRoot;
    }

    private static TemplateSettingDTO replaceWithModel(TemplateSettingDTO paramSetting, DomainInfo domainInfo) {
        TemplateSettingDTO templateSettingDTO = new TemplateSettingDTO();
        templateSettingDTO.setConfigName(paramSetting.getConfigName());
        templateSettingDTO.setTemplateText(paramSetting.getTemplateText());
        templateSettingDTO.setPackageName(replace(paramSetting.getPackageName(), domainInfo));
        templateSettingDTO.setEncoding(replace(paramSetting.getEncoding(), domainInfo));
        templateSettingDTO.setSuffix(replace(paramSetting.getSuffix(), domainInfo));
        templateSettingDTO.setFileName(replace(paramSetting.getFileName(), domainInfo));
        templateSettingDTO.setBasePath(replace(paramSetting.getBasePath(), domainInfo));
        return templateSettingDTO;
    }

    private static String replace(String templateText, DomainInfo domainInfo) {
        try {
            String defaultTemplateName = "default";
            freemarker.template.Configuration cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_22);
            // 设置模板加载器
            StringTemplateLoader templateLoader = new StringTemplateLoader();
            templateLoader.putTemplate(defaultTemplateName, templateText);
            cfg.setTemplateLoader(templateLoader);

            cfg.setDefaultEncoding(domainInfo.getEncoding());
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

            Template templateName = cfg.getTemplate(defaultTemplateName);
            Writer writer = new StringWriter();
            Map<String, Object> map = new HashMap<>();

            map.put("domain", domainInfo);
            templateName.process(map, writer);
            return writer.toString();
        } catch (Exception e) {
            logger.error("动态参数替换错误", e);
            return templateText;
        }
    }


    /**
     * 添加相关插件（注意插件文件需要通过jar引入）
     *
     * @param context
     */
    private static void addPluginConfiguration(Context context, GenerateConfig generateConfig) {


        //实体添加序列化
        PluginConfiguration serializablePlugin = new PluginConfiguration();
        serializablePlugin.addProperty("type", SerializablePlugin.class.getName());
        serializablePlugin.setConfigurationType(SerializablePlugin.class.getName());
        context.addPluginConfiguration(serializablePlugin);
        /**
         * jpa 支持插件, 内置必选
         */
        PluginConfiguration daoEntityAnnotationPlugin = new PluginConfiguration();
        daoEntityAnnotationPlugin.setConfigurationType(DaoEntityAnnotationInterfacePlugin.class.getName());
        String domainObjectName = context.getTableConfigurations().get(0).getDomainObjectName();
        String targetPackage = context.getJavaModelGeneratorConfiguration().getTargetPackage();
        daoEntityAnnotationPlugin.addProperty("domainName", targetPackage + "." + domainObjectName);
        context.addPluginConfiguration(daoEntityAnnotationPlugin);

        // toString,hashCode,equals
        if (generateConfig.isNeedToStringHashcodeEquals()) {
            PluginConfiguration equalsHashCodePlugin = new PluginConfiguration();
            equalsHashCodePlugin.addProperty("type", EqualsHashCodePlugin.class.getName());
            equalsHashCodePlugin.setConfigurationType(EqualsHashCodePlugin.class.getName());
            context.addPluginConfiguration(equalsHashCodePlugin);
            PluginConfiguration toStringPluginPlugin = new PluginConfiguration();
            toStringPluginPlugin.addProperty("type", ToStringPlugin.class.getName());
            toStringPluginPlugin.setConfigurationType(ToStringPlugin.class.getName());
            context.addPluginConfiguration(toStringPluginPlugin);
        }


        JavaTypeResolverConfiguration javaTypeResolverPlugin = new JavaTypeResolverConfiguration();
        javaTypeResolverPlugin.setConfigurationType(JavaTypeResolverJsr310Impl.class.getName());
        //for JSR310
        javaTypeResolverPlugin.addProperty("supportJsr", String.valueOf(generateConfig.isJsr310Support()));
        javaTypeResolverPlugin.addProperty("supportAutoNumeric","true");
        context.setJavaTypeResolverConfiguration(javaTypeResolverPlugin);

        // Lombok 插件
        if (generateConfig.isUseLombokPlugin()) {
            PluginConfiguration pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.addProperty("type", LombokPlugin.class.getName());
            pluginConfiguration.setConfigurationType(LombokPlugin.class.getName());
            context.addPluginConfiguration(pluginConfiguration);
        }


    }

}


