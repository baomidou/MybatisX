package com.baomidou.plugin.idea.mybatisx.generate.setting;

import com.baomidou.plugin.idea.mybatisx.generate.dto.TemplateSettingDTO;
import com.baomidou.plugin.idea.mybatisx.generate.util.XmlUtils;
import com.baomidou.plugin.idea.mybatisx.util.IOUtils;
import com.intellij.ide.extensionResources.ExtensionsRootType;
import com.intellij.ide.scratch.ScratchFileService;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.ReflectionUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DefaultSettingsConfig {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSettingsConfig.class);

    public static final String TEMPLATES = "templates";

    private static File getPath(@NotNull String pathParam) throws IOException {
        @NotNull PluginId id = Objects.requireNonNull(PluginId.findId("com.baomidou.plugin.idea.mybatisx"));
        final ScratchFileService scratchFileService = ScratchFileService.getInstance();
        final ExtensionsRootType extensionsRootType = ExtensionsRootType.getInstance();
        final String path = scratchFileService.getRootPath(extensionsRootType) + "/" + id.getIdString() +
            (StringUtil.isEmpty(pathParam) ? "" : "/"
                + pathParam);
        final File file = new File(path);
        if (!file.exists()) {
            extensionsRootType.extractBundledResources(id, "");
        }
        return file;
    }

    /**
     * 读取 mybatisx/templates 下面的所有默认模板
     *
     * @return
     */
    public static Map<String, List<TemplateSettingDTO>> defaultSettings() {
        Map<String, List<TemplateSettingDTO>> map = new HashMap<>();
        try {
            File resourceDirectory = getPath(TEMPLATES);
            if (!resourceDirectory.exists()) {
                return Collections.emptyMap();
            }
            for (File file : resourceDirectory.listFiles()) {

                String configName = file.getName();
                // 模板配置的元数据信息
                File metaFile = new File(file, ".meta.xml");
                if (!metaFile.exists()) {
                    logger.error("元数据文件不存在,无法加载配置.  元数据信息: {}", metaFile.getAbsolutePath());
                    continue;
                }
                Map<String, TemplateSettingDTO> defaultTemplateSettingMapping = null;

                try (FileInputStream metaInputStream = new FileInputStream(metaFile)) {
                    defaultTemplateSettingMapping = XmlUtils.loadTemplatesByFile(metaInputStream);
                } catch (IOException e) {
                    logger.error("加载配置出错", e);
                    continue;
                }
                //defaultTemplateSettingMapping();

                // 模板一定是.ftl后缀名的文件
                File[] templateFiles = file.listFiles(pathname -> pathname.getName().endsWith(".ftl"));
                Set<String> fileNames = defaultTemplateSettingMapping.keySet();
                List<TemplateSettingDTO> templateSettingDTOS = new ArrayList<>();
                // 每个配置文件都有自己的元数据配置文件???
                for (File templateFile : templateFiles) {
                    // 元数据的文件名和文件名不一致
                    String configFileName = templateFile.getName();
                    if (!fileNames.contains(configFileName)) {
                        continue;
                    }
                    TemplateSettingDTO templateSettingDTO = defaultTemplateSettingMapping.get(configFileName);
                    try (FileInputStream fileInputStream = new FileInputStream(templateFile)) {
                        String templateText = IOUtils.toString(fileInputStream, "UTF-8");
                        TemplateSettingDTO templateSetting = copyFromTemplateText(templateSettingDTO, templateText);
                        templateSettingDTOS.add(templateSetting);
                    }
                }
                if (templateSettingDTOS.size() > 0) {
                    map.put(configName, templateSettingDTOS);
                }
            }
        } catch (IOException e) {
            logger.error("加载配置出错", e);
        }
        return map;
    }

    private static TemplateSettingDTO copyFromTemplateText(TemplateSettingDTO templateSetting, String templateText) {
        TemplateSettingDTO templateSettingDTO = new TemplateSettingDTO();
        templateSettingDTO.setBasePath(templateSetting.getBasePath());
        templateSettingDTO.setConfigName(templateSetting.getConfigName());
        templateSettingDTO.setConfigFile(templateSetting.getConfigFile());
        templateSettingDTO.setFileName(templateSetting.getFileName());
        templateSettingDTO.setSuffix(templateSetting.getSuffix());
        templateSettingDTO.setPackageName(templateSetting.getPackageName());
        templateSettingDTO.setEncoding(templateSetting.getEncoding());
        templateSettingDTO.setTemplateText(templateText);
        return templateSettingDTO;
    }


}
