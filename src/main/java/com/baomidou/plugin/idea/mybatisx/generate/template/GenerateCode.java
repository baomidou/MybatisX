package com.baomidou.plugin.idea.mybatisx.generate.template;

import com.baomidou.plugin.idea.mybatisx.generate.dto.CustomTemplateRoot;
import com.baomidou.plugin.idea.mybatisx.generate.dto.DomainInfo;
import com.baomidou.plugin.idea.mybatisx.generate.dto.GenerateConfig;
import com.baomidou.plugin.idea.mybatisx.generate.dto.TemplateContext;
import com.baomidou.plugin.idea.mybatisx.generate.dto.TemplateSettingDTO;
import com.baomidou.plugin.idea.mybatisx.generate.plugin.DaoEntityAnnotationInterfacePlugin;
import com.baomidou.plugin.idea.mybatisx.generate.plugin.IntellijMyBatisGenerator;
import com.baomidou.plugin.idea.mybatisx.generate.plugin.JavaTypeResolverJsr310Impl;
import com.baomidou.plugin.idea.mybatisx.generate.plugin.helper.IntellijTableInfo;
import com.baomidou.plugin.idea.mybatisx.generate.plugin.helper.MergeJavaCallBack;
import com.baomidou.plugin.idea.mybatisx.generate.setting.TemplatesSettings;
import com.baomidou.plugin.idea.mybatisx.util.DbToolsUtils;
import com.intellij.database.psi.DbTable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.softwareloop.mybatis.generator.plugins.LombokPlugin;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.mybatis.generator.config.CommentGeneratorConfiguration;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.JavaTypeResolverConfiguration;
import org.mybatis.generator.config.ModelType;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.internal.NullProgressCallback;
import org.mybatis.generator.plugins.EqualsHashCodePlugin;
import org.mybatis.generator.plugins.SerializablePlugin;
import org.mybatis.generator.plugins.ToStringPlugin;

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

/**
 * 代码生成入口
 */
public class GenerateCode {

    public static void generate(Project project,
                                GenerateConfig generateConfig,
                                PsiElement psiElement) throws Exception {

        List<String> warnings = new ArrayList<>();
        Configuration config = new Configuration();


        Context context = new Context(ModelType.CONDITIONAL) {
            @Override
            public void validate(List<String> errors) {
            }
        };
        context.setId("MybatisXContext");

        JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
        String targetProject = generateConfig.getModulePath() + "/" + generateConfig.getBasePath();
        javaModelGeneratorConfiguration.setTargetProject(targetProject);
        javaModelGeneratorConfiguration.setTargetPackage(generateConfig.getBasePackage() + "." + generateConfig.getRelativePackage());
        context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);

        buildTableConfiguration(generateConfig, context);

        CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();
        commentGeneratorConfiguration.setConfigurationType(CustomDefaultCommentGenerator.class.getName());
        commentGeneratorConfiguration.addProperty("suppressDate", "true");
        commentGeneratorConfiguration.addProperty("annotationType", generateConfig.getAnnotationType());

        context.setCommentGeneratorConfiguration(commentGeneratorConfiguration);

        JavaTypeResolverConfiguration javaTypeResolverConfiguration = new JavaTypeResolverConfiguration();
        javaTypeResolverConfiguration.addProperty("forceBigDecimals", "false");
        context.setJavaTypeResolverConfiguration(javaTypeResolverConfiguration);
        // 根据模板生成代码的插件
        configExtraPlugin(project, generateConfig, context);
        // 界面配置的扩展插件
        addPluginConfiguration(context, generateConfig);
        config.addContext(context);

        IntellijTableInfo intellijTableInfo = DbToolsUtils.buildIntellijTableInfo((DbTable) psiElement);
        Set<String> contexts = new HashSet<>();
        Set<String> fullyqualifiedTables = new HashSet<>();
        IntellijMyBatisGenerator intellijMyBatisGenerator = new IntellijMyBatisGenerator(config, new MergeJavaCallBack(true), warnings);
        intellijMyBatisGenerator.generate(new NullProgressCallback(), contexts, fullyqualifiedTables, intellijTableInfo);

        VirtualFile virtualFile = ProjectUtil.guessProjectDir(project);
        if (virtualFile != null) {
            virtualFile.refresh(true, true);
        }
    }

    private static void buildTableConfiguration(GenerateConfig generateConfig, Context context) {
        TableConfiguration tc = new TableConfiguration(context);
        tc.setTableName(generateConfig.getTableName());
        tc.setDomainObjectName(generateConfig.getDomainObjectName());


        if (generateConfig.isUseActualColumns()) {
            tc.addProperty("useActualColumnNames", "true");
        }

        context.addTableConfiguration(tc);
    }

    private static void configExtraPlugin(Project project, GenerateConfig generateConfig, Context context) {
        TemplatesSettings instance = TemplatesSettings.getInstance(project);
        TemplateContext templateConfigs = instance.getTemplateConfigs();
        List<TemplateSettingDTO> list = templateConfigs.getTemplateSettingMap().get(TemplatesSettings.DEFAULT_TEMPLATE_NAME);
        CustomTemplateRoot templateRoot = buildRootConfig(generateConfig.getTargetProject(),
            generateConfig.getDomainObjectName(),
            generateConfig.getBasePackage(),
            generateConfig.getRelativePackage(),
            generateConfig.getEncoding(),
            list);

        for (String extraTemplateName : generateConfig.getExtraTemplateNames()) {
            Optional<TemplateSettingDTO> first = list.stream().filter(x -> x.getConfigName().equalsIgnoreCase(extraTemplateName)).findFirst();
            if (first.isPresent()) {
                TemplateSettingDTO templateSettingDTO = first.get();
                addPlugin(context, templateRoot, extraTemplateName, templateSettingDTO.getTemplateText());
            }

        }
    }

    private static void addPlugin(Context context, Serializable serializable, String javaService, String templateText) {
        PluginConfiguration serviceJavaPluginConfiguration = new PluginConfiguration();
        serviceJavaPluginConfiguration.setConfigurationType(CustomTemplatePlugin.class.getName());
        serviceJavaPluginConfiguration.addProperty("currentName", javaService);
        // 模板的内容
        serviceJavaPluginConfiguration.addProperty("templateText", templateText);
        addRootMapToConfig(serializable, serviceJavaPluginConfiguration);
        context.addPluginConfiguration(serviceJavaPluginConfiguration);
    }

    private static void addRootMapToConfig(Serializable serializable, PluginConfiguration customPluginConfiguration) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(out)) {
            objectOutputStream.writeObject(serializable);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] encode = Base64.getEncoder().encode(out.toByteArray());
        customPluginConfiguration.addProperty("root", new String(encode));
    }

    private static CustomTemplateRoot buildRootConfig(String targetProject,
                                                      String domainObjectName,
                                                      String basePackage,
                                                      String relativePackage,
                                                      String encoding,
                                                      List<TemplateSettingDTO> templateConfigs) {
        // 生成的模板参照 https://gitee.com/baomidou/SpringWind/tree/spring-mvc/SpringWind/src/main/java/com/baomidou/springwind/mapper
        CustomTemplateRoot customTemplateRoot = new CustomTemplateRoot();
        DomainInfo domainInfo = new DomainInfo();
        domainInfo.setFileName(domainObjectName);
        domainInfo.setBasePackage(basePackage);
        domainInfo.setRelativePackage(relativePackage);
        domainInfo.setEncoding(encoding);
        customTemplateRoot.setDomainInfo(domainInfo);
        customTemplateRoot.setTargetProject(targetProject);

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

        //for JSR310
        if (generateConfig.isJsr310Support()) {
            JavaTypeResolverConfiguration javaTypeResolverPlugin = new JavaTypeResolverConfiguration();
            javaTypeResolverPlugin.addProperty("type", JavaTypeResolverJsr310Impl.class.getName());
            javaTypeResolverPlugin.setConfigurationType(JavaTypeResolverJsr310Impl.class.getName());
            context.setJavaTypeResolverConfiguration(javaTypeResolverPlugin);
        }

        // Lombok 插件
        if (generateConfig.isUseLombokPlugin()) {
            PluginConfiguration pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.addProperty("type", LombokPlugin.class.getName());
            pluginConfiguration.setConfigurationType(LombokPlugin.class.getName());
            context.addPluginConfiguration(pluginConfiguration);
        }


    }

}


