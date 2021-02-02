package com.baomidou.plugin.idea.mybatisx.generate.template;

import org.jetbrains.annotations.Nullable;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 自定义模板填充插件
 */
public class CustomTemplatePlugin extends PluginAdapter {

    private static final String UTF_8 = "UTF-8";

    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {

        CustomPluginContextBuilder customPluginContextBuilder = CustomPluginContextBuilder.aCustomPluginContext();
        String currentName = properties.getProperty("currentName");
        String templateText = properties.getProperty("templateText");

        String root = properties.getProperty("root");


        Map<String, Map<String, String>> rootObject = readRootObject(root);
        Map<String, String> jsonObject = rootObject.get(currentName);

        CustomPluginContext customPluginContext = customPluginContextBuilder.buildByProperties(jsonObject);

        TopLevelClass topLevelClass = new TopLevelClass(customPluginContext.getFileName());
        FreeMakerFormatter javaFormatter = new FreeMakerFormatter(customPluginContext, rootObject, ClassInfo.build(introspectedTable),templateText);
        javaFormatter.setContext(context);

        String modulePath = customPluginContext.getModulePath() + "/" + customPluginContext.getBasePath();
        GeneratedJavaFile generatedJavaFile = new FreemarkerFile(topLevelClass,
            javaFormatter,
            modulePath,
            customPluginContext.getEncoding(),
            customPluginContext.getSuffix(),
            customPluginContext.getPackageName());
        return Collections.singletonList(generatedJavaFile);
    }

    @Nullable
    private Map<String, Map<String, String>> readRootObject(String root) {
        Map<String, Map<String, String>> rootObject = null;
        try {
            byte[] decode = Base64.getDecoder().decode(root.getBytes(UTF_8));
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(decode))) {
                rootObject = (Map<String, Map<String, String>>) objectInputStream.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return rootObject;
    }
}
