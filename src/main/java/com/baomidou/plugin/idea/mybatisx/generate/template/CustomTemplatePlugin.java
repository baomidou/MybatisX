package com.baomidou.plugin.idea.mybatisx.generate.template;

import com.baomidou.plugin.idea.mybatisx.generate.dto.CustomTemplateRoot;
import com.baomidou.plugin.idea.mybatisx.generate.dto.TemplateSettingDTO;
import org.jetbrains.annotations.Nullable;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 自定义模板填充插件
 */
public class CustomTemplatePlugin extends PluginAdapter {

    public static final String CURRENT_NAME = "currentName";
    public static final String TEMPLATE_TEXT = "templateText";
    public static final String ROOT = "root";

    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {

        String currentName = properties.getProperty(CURRENT_NAME);
        String templateText = properties.getProperty(TEMPLATE_TEXT);
        String root = properties.getProperty(ROOT);


        CustomTemplateRoot rootObject = readRootObject(root);
        Optional<TemplateSettingDTO> customTemplateConfigDTOOptional = rootObject.findByName(currentName);

        if (!customTemplateConfigDTOOptional.isPresent()) {
            throw new RuntimeException("无法找到模板, 模板名称: " + currentName);
        }
        TemplateSettingDTO templateSettingDTO = customTemplateConfigDTOOptional.get();

        TopLevelClass topLevelClass = new TopLevelClass(templateSettingDTO.getFileName());
        FreeMakerFormatter javaFormatter = new FreeMakerFormatter(templateSettingDTO, rootObject, ClassInfo.build(introspectedTable), templateText);
        javaFormatter.setContext(context);

        String modulePath = rootObject.getTargetProject() + "/" + templateSettingDTO.getBasePath();
        GeneratedJavaFile generatedJavaFile = new FreemarkerFile(topLevelClass,
            javaFormatter,
            modulePath,
            templateSettingDTO.getEncoding(),
            templateSettingDTO.getSuffix(),
            templateSettingDTO.getPackageName());
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
