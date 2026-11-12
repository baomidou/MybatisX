package com.baomidou.plugin.idea.mybatisx.generate.typemapping;

import com.intellij.ide.extensionResources.ExtensionsRootType;
import com.intellij.ide.scratch.ScratchFileService;
import com.intellij.openapi.extensions.PluginId;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * 用户扩展目录中的类型映射文件。内置文件只在本地还没有时复制，不覆盖用户改过的内容。
 */
@Slf4j
public final class TypeMappingCatalog {
    private static final String DIRECTORY = "type-mapping";
    private static final List<String> BUNDLED = List.of("mysql.json", "postgresql.json", "oracle.json");

    private TypeMappingCatalog() {
    }

    public static List<TypeMappingOption> listOptions() {
        try {
            File directory = ensureDirectory();
            File[] files = directory.listFiles(file -> file.isFile()
                && file.getName().toLowerCase(Locale.ROOT).endsWith(".json")
                && !file.getName().startsWith("."));
            if (files == null || files.length == 0) {
                return List.of();
            }
            Arrays.sort(files, Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));
            List<TypeMappingOption> options = new ArrayList<>();
            for (File file : files) {
                Optional<TypeMapping> mapping = read(file);
                if (mapping.isEmpty()) {
                    log.warn("类型映射文件无法解析, 已跳过: {}", file.getAbsolutePath());
                    continue;
                }
                options.add(new TypeMappingOption(file.getName(), mapping.get().displayName(file.getName())));
            }
            return options;
        } catch (Exception ex) {
            log.warn("加载类型映射目录失败", ex);
            return List.of();
        }
    }

    public static TypeMapping load(String fileName) {
        if (!isSafeFileName(fileName)) {
            return null;
        }
        try {
            File file = new File(ensureDirectory(), fileName);
            if (!file.isFile()) {
                return null;
            }
            return read(file).orElse(null);
        } catch (Exception ex) {
            log.warn("读取类型映射失败: {}", fileName, ex);
            return null;
        }
    }

    private static Optional<TypeMapping> read(File file) {
        try {
            return TypeMappingLoader.parse(Files.readString(file.toPath()));
        } catch (IOException ex) {
            log.warn("读取类型映射失败: {}", file.getAbsolutePath(), ex);
            return Optional.empty();
        }
    }

    private static boolean isSafeFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return false;
        }
        return !fileName.contains("..") && !fileName.contains("/") && !fileName.contains("\\");
    }

    private static File ensureDirectory() throws IOException {
        PluginId pluginId = Objects.requireNonNull(PluginId.findId("com.baomidou.plugin.idea.mybatisx"));
        String root = ScratchFileService.getInstance().getRootPath(ExtensionsRootType.getInstance());
        File directory = new File(root + "/" + pluginId.getIdString() + "/" + DIRECTORY);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("无法创建类型映射目录: " + directory.getAbsolutePath());
        }
        for (String bundled : BUNDLED) {
            File target = new File(directory, bundled);
            if (target.exists()) {
                continue;
            }
            try (InputStream input = TypeMappingCatalog.class.getResourceAsStream("/extensions/type-mapping/" + bundled)) {
                if (input == null) {
                    log.warn("插件内没有内置类型映射: {}", bundled);
                    continue;
                }
                Files.copy(input, target.toPath());
            }
        }
        return directory;
    }
}
