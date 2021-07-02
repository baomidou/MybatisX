package com.baomidou.plugin.idea.mybatisx.generate.template;

import com.baomidou.plugin.idea.mybatisx.generate.dto.CustomTemplateRoot;
import com.baomidou.plugin.idea.mybatisx.generate.dto.ModuleUIInfo;
import com.baomidou.plugin.idea.mybatisx.generate.dto.TemplateSettingDTO;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 自定义模板填充插件
 */
public class CustomTemplatePlugin extends PluginAdapter {

    public static final String ROOT = "root";
    private static final Logger logger = LoggerFactory.getLogger(CustomTemplatePlugin.class);

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        String root = properties.getProperty(ROOT);
        CustomTemplateRoot rootObject = readRootObject(root);

        ModuleUIInfo moduleUIInfo = rootObject.getModuleUIInfo();

        String modulePath = rootObject.getModuleUIInfo().getModulePath() + "/" + moduleUIInfo.getBasePath();
        final File file = new File(modulePath);
        if (!file.exists()) {
            final boolean created = file.mkdirs();
            logger.info("模块目录不存在,已创建目录. modulePath: {},created:{}", file.getAbsolutePath(), created);
        }
        TopLevelClass topLevelClass = new TopLevelClass(moduleUIInfo.getFileName());
        FreeMakerFormatter javaFormatter = new FreeMakerFormatter(rootObject, ClassInfo.build(introspectedTable));
        javaFormatter.setContext(context);

        GeneratedJavaFile generatedJavaFile = new FreemarkerFile(topLevelClass,
            javaFormatter,
            modulePath,
            moduleUIInfo.getEncoding(),
            moduleUIInfo.getFileNameWithSuffix(),
            moduleUIInfo.getPackageName());
        logger.info("模板文件构建完成, modulePath: {}", modulePath);
        return Collections.singletonList(generatedJavaFile);
    }

    @Nullable
    private CustomTemplateRoot readRootObject(String root) {
        CustomTemplateRoot rootObject = null;
        try {
            byte[] decode = Base64.getDecoder().decode(root.getBytes(StandardCharsets.UTF_8));
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(decode))) {
                rootObject = (CustomTemplateRoot) objectInputStream.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return rootObject;
    }
}
