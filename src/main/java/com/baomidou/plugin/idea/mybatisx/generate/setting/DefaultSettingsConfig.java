package com.baomidou.plugin.idea.mybatisx.generate.setting;

import com.baomidou.plugin.idea.mybatisx.generate.dto.ConfigSetting;
import com.baomidou.plugin.idea.mybatisx.generate.dto.TemplateSettingDTO;
import com.baomidou.plugin.idea.mybatisx.generate.util.XmlUtils;
import com.intellij.ide.extensionResources.ExtensionsRootType;
import com.intellij.ide.scratch.ScratchFileService;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.util.text.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Slf4j
public class DefaultSettingsConfig {

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
    public static Map<String, ConfigSetting> defaultSettings() {
        Map<String, ConfigSetting> map = new TreeMap<>();
        try {
            File resourceDirectory = getPath(TEMPLATES);
            if (!resourceDirectory.exists()) {
                return Collections.emptyMap();
            }
            for (File file : Objects.requireNonNull(resourceDirectory.listFiles())) {

                String configName = file.getName();
                // 模板配置的元数据信息
                File metaFile = new File(file, ".meta.xml");
                if (!metaFile.exists()) {
                    log.error("元数据文件不存在,无法加载配置.  元数据信息: {}", metaFile.getAbsolutePath());
                    continue;
                }
                Map<String, TemplateSettingDTO> defaultTemplateSettingMapping;

                try (FileInputStream metaInputStream = new FileInputStream(metaFile)) {
                    defaultTemplateSettingMapping = XmlUtils.loadTemplatesByFile(metaInputStream);
                } catch (IOException e) {
                    log.error("加载配置出错", e);
                    continue;
                }

                // 模板一定是.ftl后缀名的文件
                Set<String> existsFileNames = Arrays.stream(Objects.requireNonNull(file.listFiles(pathname -> pathname.getName().endsWith(".ftl")))).map(File::getName).collect(Collectors.toSet());
                List<TemplateSettingDTO> templateSettingDTOS = defaultTemplateSettingMapping.values().stream()
                    .map(templateSettingDTO -> copyFromTemplateText(templateSettingDTO, existsFileNames))
                    .collect(Collectors.toList());
                if (!templateSettingDTOS.isEmpty()) {
                    map.put(configName, buildConfigSetting(file, templateSettingDTOS));
                }
            }
        } catch (IOException e) {
            log.error("加载配置出错", e);
        }
        return map;
    }

    @NotNull
    private static ConfigSetting buildConfigSetting(File file, List<TemplateSettingDTO> templateSettingDTOS) {
        ConfigSetting configSetting = new ConfigSetting();
        configSetting.setName(file.getName());
        configSetting.setPath(file.getAbsolutePath());
        configSetting.setTemplateSettingDTOList(templateSettingDTOS);
        return configSetting;
    }

    private static TemplateSettingDTO copyFromTemplateText(TemplateSettingDTO templateSetting, Set<String> existsFileNames) {
        TemplateSettingDTO templateSettingDTO = new TemplateSettingDTO();
        templateSettingDTO.setBasePath(templateSetting.getBasePath());
        templateSettingDTO.setConfigName(templateSetting.getConfigName());
        templateSettingDTO.setConfigFile(templateSetting.getConfigFile());
        templateSettingDTO.setFileName(templateSetting.getFileName());
        templateSettingDTO.setSuffix(templateSetting.getSuffix());
        templateSettingDTO.setPackageName(templateSetting.getPackageName());
        templateSettingDTO.setEncoding(templateSetting.getEncoding());
        templateSettingDTO.setExistsFileNames(existsFileNames);
        return templateSettingDTO;
    }


}
