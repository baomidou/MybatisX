package com.baomidou.plugin.idea.mybatisx.generate.template;

import com.baomidou.plugin.idea.mybatisx.generate.dto.ConfigSetting;
import com.baomidou.plugin.idea.mybatisx.generate.dto.CustomTemplateRoot;
import com.baomidou.plugin.idea.mybatisx.generate.dto.DomainInfo;
import com.baomidou.plugin.idea.mybatisx.generate.dto.GenerateConfig;
import com.baomidou.plugin.idea.mybatisx.generate.dto.ModuleInfoGo;
import com.baomidou.plugin.idea.mybatisx.generate.dto.TemplateSettingDTO;
import com.baomidou.plugin.idea.mybatisx.generate.plugin.DaoEntityAnnotationInterfacePlugin;
import com.baomidou.plugin.idea.mybatisx.generate.plugin.IgnoreParentFieldsPlugin;
import com.baomidou.plugin.idea.mybatisx.generate.plugin.IntellijMyBatisGenerator;
import com.baomidou.plugin.idea.mybatisx.generate.plugin.JavaTypeResolverJsr310Impl;
import com.baomidou.plugin.idea.mybatisx.generate.plugin.helper.IntellijTableInfo;
import com.baomidou.plugin.idea.mybatisx.generate.plugin.helper.MergeJavaCallBack;
import com.baomidou.plugin.idea.mybatisx.generate.ui.CodeGenerateUI;
import com.baomidou.plugin.idea.mybatisx.generate.util.DomainPlaceHolder;
import com.baomidou.plugin.idea.mybatisx.util.DbToolsUtils;
import com.baomidou.plugin.idea.mybatisx.util.JavaUtils;
import com.baomidou.plugin.idea.mybatisx.util.SpringStringUtils;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.intellij.database.psi.DbTable;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.ClassUtil;
import com.softwareloop.mybatis.generator.plugins.LombokPlugin;
import org.jetbrains.annotations.NotNull;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedKotlinFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.ProgressCallback;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * 代码生成入口
 *
 * @author ls9527
 */
public class GenerateCode {

    public static final MergeJavaCallBack SHELL_CALLBACK = new MergeJavaCallBack(true);
    private static final Logger logger = LoggerFactory.getLogger(GenerateCode.class);

    public static void generate(Project project,
                                GenerateConfig generateConfig,
                                Map<String, ConfigSetting> configSettingMap,
                                DbTable dbTable,
                                String domainName,
                                String tableName) throws Exception {
        List<String> warnings = new ArrayList<>();
        Configuration config = new Configuration();

        Context context = new Context(ModelType.CONDITIONAL) {
            @Override
            public void validate(List<String> errors) {
            }

            @Override
            public void generateFiles(ProgressCallback callback, List<GeneratedJavaFile> generatedJavaFiles, List<GeneratedXmlFile> generatedXmlFiles, List<GeneratedKotlinFile> generatedKotlinFiles, List<String> warnings) throws InterruptedException {
                super.generateFiles(callback, generatedJavaFiles, generatedXmlFiles, generatedKotlinFiles, warnings);
                if (!generateConfig.isNeedsModel()) {
                    generatedJavaFiles.removeIf(next -> !(next instanceof FreemarkerFile));
                }
            }
        };
        context.setId("MybatisXContext");
        String encoding = generateConfig.getEncoding();
        if (StringUtils.isEmpty(encoding)) {
            encoding = "UTF-8";
        }
        context.addProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING, encoding);
        context.addProperty(PropertyRegistry.CONTEXT_JAVA_FORMATTER, CustomJavaFormatter.class.getName());

        JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
        String targetProject = generateConfig.getModulePath() + "/" + generateConfig.getBasePath();
        javaModelGeneratorConfiguration.setTargetProject(targetProject);

        String relativePackage = generateConfig.getRelativePackage();
        if (null != relativePackage && !"".equals(relativePackage)) {
            relativePackage = "." + relativePackage;
        }
        javaModelGeneratorConfiguration.setTargetPackage(generateConfig.getBasePackage() + relativePackage);

        context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);

        final List<ClassLoader> classLoaderList = new ArrayList<>();
        if (SpringStringUtils.hasText(generateConfig.getSuperClass())) {
            javaModelGeneratorConfiguration.addProperty(PropertyRegistry.ANY_ROOT_CLASS, generateConfig.getSuperClass());
            final Optional<PsiClass> psiClassOptional = JavaUtils.findClazz(project, generateConfig.getSuperClass());
            psiClassOptional.ifPresent(psiClass -> classLoaderList.add(new MybatisXClassPathDynamicClassLoader(psiClass)));

        }

        String extraDomainName = domainName;
        if (!StringUtils.isEmpty(generateConfig.getExtraClassSuffix())) {
            extraDomainName = extraDomainName + generateConfig.getExtraClassSuffix();
        }

        buildTableConfiguration(generateConfig, context, tableName, extraDomainName);

        CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();
        commentGeneratorConfiguration.setConfigurationType(CustomDefaultCommentGenerator.class.getName());
        commentGeneratorConfiguration.addProperty("suppressDate", "true");
        commentGeneratorConfiguration.addProperty("annotationType", generateConfig.getAnnotationType());
        commentGeneratorConfiguration.addProperty("needsComment", Boolean.valueOf(generateConfig.isNeedsComment()).toString());

        context.setCommentGeneratorConfiguration(commentGeneratorConfiguration);

        JavaTypeResolverConfiguration javaTypeResolverConfiguration = new JavaTypeResolverConfiguration();
        javaTypeResolverConfiguration.addProperty("forceBigDecimals", "false");
        context.setJavaTypeResolverConfiguration(javaTypeResolverConfiguration);
        final ConfigSetting configSetting = configSettingMap.get(generateConfig.getTemplatesName());
        if (configSetting == null) {
            logger.error("未选择模板组名称, templatesName: {}", generateConfig.getTemplatesName());
            return;
        }
        DomainInfo domainInfo = buildDomainInfo(generateConfig, domainName);
        // 根据模板生成代码的插件
        configExtraPlugin(extraDomainName, context, domainInfo, configSetting, generateConfig.getModuleUIInfoList());
        // 界面配置的扩展插件
        addPluginConfiguration(context, generateConfig);
        addIgnoreDupFieldsPlugin(context,generateConfig, project);
        config.addContext(context);


        IntellijTableInfo intellijTableInfo = DbToolsUtils.buildIntellijTableInfo(dbTable);
        Set<String> contexts = new HashSet<>();
        Set<String> fullyqualifiedTables = new HashSet<>();
        IntellijMyBatisGenerator intellijMyBatisGenerator = new IntellijMyBatisGenerator(config, SHELL_CALLBACK, warnings);


        intellijMyBatisGenerator.generate(new NullProgressCallback(),
            contexts,
            fullyqualifiedTables,
            true,
            classLoaderList,
            intellijTableInfo);
    }

    private static void addIgnoreDupFieldsPlugin(Context context, GenerateConfig generateConfig, Project project) {
        PluginConfiguration ignoreParentDupFieldsPlugin = new PluginConfiguration();
        PsiManager instance = PsiManager.getInstance(project);
        PsiClass psiClass = ClassUtil.findPsiClass(instance, generateConfig.getSuperClass());
        if (psiClass != null) {
            String parentFields = Arrays.stream(psiClass.getAllFields()).map(PsiField::getName).collect(Collectors.joining(","));
            ignoreParentDupFieldsPlugin.addProperty("parentFields", parentFields);
        }
        ignoreParentDupFieldsPlugin.addProperty("superClass", generateConfig.getSuperClass());

        ignoreParentDupFieldsPlugin.setConfigurationType(IgnoreParentFieldsPlugin.class.getName());
        context.addPluginConfiguration(ignoreParentDupFieldsPlugin);
    }

    private static DomainInfo buildDomainInfo(GenerateConfig generateConfig, String domainName) {
        DomainInfo domainInfo = new DomainInfo();
        domainInfo.setModulePath(generateConfig.getModulePath());
        domainInfo.setBasePath(generateConfig.getBasePath());
        domainInfo.setBasePackage(generateConfig.getBasePackage());
        domainInfo.setRelativePackage(generateConfig.getRelativePackage());
        domainInfo.setEncoding(generateConfig.getEncoding());
        domainInfo.setFileName(domainName);
        return domainInfo;
    }

    private static void buildTableConfiguration(GenerateConfig generateConfig, Context context, @NotNull String tableName, String extraDomainName) {
        TableConfiguration tc = new TableConfiguration(context);
        tc.setTableName(tableName);
        tc.setDomainObjectName(extraDomainName);
        if (generateConfig.isUseActualColumns()) {
            tc.addProperty("useActualColumnNames", "true");
        }

        if (generateConfig.isUseActualColumnAnnotationInject()) {
            tc.addProperty("useActualColumnAnnotationInject", "true");
        }
        boolean replaceNecessary = false;
        StringJoiner stringJoiner = new StringJoiner("|");
        if (!StringUtils.isEmpty(generateConfig.getIgnoreFieldPrefix())) {
            stringJoiner.add("^" + generateConfig.getIgnoreFieldPrefix());
            replaceNecessary = true;
        }
        if (!StringUtils.isEmpty(generateConfig.getIgnoreFieldSuffix())) {
            stringJoiner.add(generateConfig.getIgnoreFieldSuffix() + "$");
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

    private static void configExtraPlugin(String extraDomainName,
                                          Context context,
                                          DomainInfo domainInfo,
                                          ConfigSetting configSetting,
                                          List<ModuleInfoGo> extraTemplateNames) {


        Map<String, TemplateSettingDTO> templateSettingDTOMap = configSetting.getTemplateSettingDTOList()
            .stream()
            .collect(Collectors.toMap(TemplateSettingDTO::getConfigName, m -> m, (a, b) -> a));

        List<CustomTemplateRoot> templateRootList = new ArrayList<>();
        List<ModuleInfoGo> rootModuleInfo = new ArrayList<>();
        for (ModuleInfoGo moduleInfo : extraTemplateNames) {
            TemplateSettingDTO templateSettingDTO = templateSettingDTOMap.get(moduleInfo.getConfigName());
            if (templateSettingDTO == null) {
                continue;
            }
            DomainInfo customDomainInfo = determineDomainInfo(extraDomainName, domainInfo, moduleInfo);
            ModuleInfoGo moduleInfoReplaced = replaceByDomainInfo(moduleInfo, customDomainInfo);
            // 文件不存在, 不生成代码
            moduleInfoReplaced.setEnable(templateSettingDTO.getExistsFileNames().contains(moduleInfo.getConfigFileName()));
            CustomTemplateRoot templateRoot = buildRootConfig(customDomainInfo, moduleInfoReplaced, configSetting, rootModuleInfo);
            templateRootList.add(templateRoot);
        }

        for (CustomTemplateRoot templateRoot : templateRootList) {
            addPlugin(context, templateRoot);
        }
    }

    private static DomainInfo determineDomainInfo(String extraDomainName, DomainInfo domainInfo, ModuleInfoGo moduleInfo) {
        DomainInfo customDomainInfo = null;
        // 重置实体模板的类名填充
        if (CodeGenerateUI.DOMAIN.equals(moduleInfo.getConfigName())) {
            customDomainInfo = domainInfo.copyFromFileName(extraDomainName);
        }
        if (customDomainInfo == null) {
            customDomainInfo = domainInfo;
        }
        return customDomainInfo;
    }

    private static ModuleInfoGo replaceByDomainInfo(ModuleInfoGo moduleInfo, DomainInfo domainInfo) {
        ModuleInfoGo moduleUIInfo = new ModuleInfoGo();
        moduleUIInfo.setConfigName(moduleInfo.getConfigName());
        moduleUIInfo.setModulePath(DomainPlaceHolder.replace(moduleInfo.getModulePath(), domainInfo));
        moduleUIInfo.setBasePath(DomainPlaceHolder.replace(moduleInfo.getBasePath(), domainInfo));
        moduleUIInfo.setPackageName(DomainPlaceHolder.replace(moduleInfo.getPackageName(), domainInfo));
        moduleUIInfo.setFileName(DomainPlaceHolder.replace(moduleInfo.getFileName(), domainInfo));
        moduleUIInfo.setFileNameWithSuffix(DomainPlaceHolder.replace(moduleInfo.getFileNameWithSuffix(), domainInfo));
        moduleUIInfo.setConfigFileName(moduleInfo.getConfigFileName());
        moduleUIInfo.setEncoding(DomainPlaceHolder.replace(moduleInfo.getEncoding(), domainInfo));
        // 校验文件模板必须存在
        if (moduleUIInfo.getConfigFileName() == null) {
            throw new RuntimeException("模板文件为空, 无法生成代码. config: " + moduleUIInfo.getConfigName());
        }
        return moduleUIInfo;
    }

    private static void addPlugin(Context context, Serializable serializable) {
        PluginConfiguration serviceJavaPluginConfiguration = new PluginConfiguration();
        serviceJavaPluginConfiguration.setConfigurationType(CustomTemplatePlugin.class.getName());
        // 模板的内容
        addRootMapToConfig(serializable, serviceJavaPluginConfiguration);
        context.addPluginConfiguration(serviceJavaPluginConfiguration);
    }

    private static void addRootMapToConfig(Serializable serializable, PluginConfiguration customPluginConfiguration) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(out)) {
            objectOutputStream.writeObject(serializable);
        } catch (IOException e) {
            logger.error("序列化数据失败", e);
            throw new RuntimeException("序列化数据失败");
        }
        byte[] encode = Base64.getEncoder().encode(out.toByteArray());
        customPluginConfiguration.addProperty(CustomTemplatePlugin.ROOT, new String(encode));
    }

    private static CustomTemplateRoot buildRootConfig(DomainInfo domainInfo,
                                                      ModuleInfoGo moduleUIInfo,
                                                      ConfigSetting configSetting,
                                                      List<ModuleInfoGo> rootModuleUIInfo) {
        // 生成的模板参照 https://gitee.com/baomidou/SpringWind/tree/spring-mvc/SpringWind/src/main/java/com/baomidou/springwind/mapper
        CustomTemplateRoot customTemplateRoot = new CustomTemplateRoot();
        customTemplateRoot.setDomainInfo(domainInfo);
        customTemplateRoot.setModuleUIInfo(moduleUIInfo);
        // 替换模板内容
        customTemplateRoot.setModuleInfoList(rootModuleUIInfo);
        rootModuleUIInfo.add(moduleUIInfo);
        customTemplateRoot.setTemplateSettingDTOList(configSetting.getTemplateSettingDTOList());
        customTemplateRoot.setTemplateBasePath(configSetting.getPath());
        return customTemplateRoot;
    }


    /**
     * 添加相关插件（注意插件文件需要通过jar引入）
     *
     * @param context
     */
    private static void addPluginConfiguration(Context context, GenerateConfig generateConfig) {
        //实体添加序列化
        if (generateConfig.isNeedSerializable()){
            PluginConfiguration serializablePlugin = new PluginConfiguration();
            serializablePlugin.addProperty("type", SerializablePlugin.class.getName());
            serializablePlugin.setConfigurationType(SerializablePlugin.class.getName());
            context.addPluginConfiguration(serializablePlugin);
        }
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
        javaTypeResolverPlugin.addProperty("supportAutoNumeric", "true");
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
