package com.baomidou.plugin.idea.mybatisx.alias;

import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.spring.boot.model.SpringBootModelConfigFileContributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.composer.ComposerException;
import org.yaml.snakeyaml.parser.ParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * The type springboot Bean alias resolver.
 *
 * @author ls9527
 */
public class SpringBootPackageResolver extends PackageAliasResolver {

    private static final String YML = "yml";
    private static final String YAML = "yaml";
    private static final String PROPERTIES = "properties";
    private static final Logger logger = LoggerFactory.getLogger(SpringBootPackageResolver.class);

    /**
     * Instantiates a new Bean alias resolver.
     *
     * @param project the project
     */
    public SpringBootPackageResolver(Project project) {
        super(project);
    }

    @NotNull
    @Override
    public Collection<String> getPackages(@Nullable PsiElement element) {
        Set<String> pkgSet = new HashSet<>();
        ExtensionPointName<SpringBootModelConfigFileContributor> objectExtensionPointName = ExtensionPointName.create("com.intellij.spring.boot.modelConfigFileContributor");
        List<SpringBootModelConfigFileContributor> extensionList = objectExtensionPointName.getExtensionList();
        for (SpringBootModelConfigFileContributor extension : extensionList) {
            Collection<Module> modulesOfType = ModuleUtil.getModulesOfType(project, JavaModuleType.getModuleType());
            for (Module module : modulesOfType) {
                List<VirtualFile> configurationFiles = extension.getConfigurationFiles(module, true);
                for (VirtualFile configurationFile : configurationFiles) {
                    readAliasesPackage(pkgSet, configurationFile);
                }
            }
        }

        return pkgSet;
    }

    private void readAliasesPackage(Set<String> pkgSet, VirtualFile configurationFile) {
        FileType fileType = configurationFile.getFileType();
        // yml ,yaml 后缀读取支持
        if (fileType.getDefaultExtension().equals(YML) ||
            fileType.getDefaultExtension().equals(YAML)) {
            readClassesFromYaml(pkgSet, configurationFile);
        }
        // properties 读取支持
        if (fileType.getDefaultExtension().equals(PROPERTIES)) {
            readClassesFromProperties(pkgSet, configurationFile);
        }
    }

    private void readClassesFromProperties(Set<String> pkgSet, VirtualFile configurationFile) {
        Properties properties = new Properties();
        try (InputStream inputStream = configurationFile.getInputStream()) {
            properties.load(inputStream);
            String typeAliasesPackage = findTypeAliasesPackageByProperties(properties);
            if (typeAliasesPackage != null) {
                if (!StringUtils.isEmpty(typeAliasesPackage)) {
                    pkgSet.add(typeAliasesPackage);
                }
            }
        } catch (IOException e) {
            logger.error("read alias exception, fileName: {}", configurationFile.getName(), e);
        }
    }

    private String findTypeAliasesPackageByProperties(Properties properties) {
        String typeAliasesPackage = properties.getProperty("mybatis.type-aliases-package");
        if (typeAliasesPackage == null) {
            typeAliasesPackage = properties.getProperty("mybatis.typeAliasesPackage");
        }
        if (typeAliasesPackage == null) {
            typeAliasesPackage = properties.getProperty("mybatis-plus.type-aliases-package");
        }
        if (typeAliasesPackage == null) {
            typeAliasesPackage = properties.getProperty("mybatis-plus.typeAliasesPackage");
        }
        return typeAliasesPackage;
    }

    private void readClassesFromYaml(Set<String> classSet, VirtualFile configurationFile) {
        Yaml yaml = new Yaml();

        try (InputStream inputStream = configurationFile.getInputStream()) {
            Iterable<Object> objects = yaml.loadAll(inputStream);
            for (Object object : objects) {
                Object config = findConfig(object);
                Object typeAliasesPackage = findAliasPackage(config);
                if (typeAliasesPackage != null) {
                    if (!StringUtils.isEmpty(typeAliasesPackage.toString())) {
                        classSet.add(typeAliasesPackage.toString());
                    }
                }
            }
        } catch (ParserException | ComposerException e) {
            logger.info("yml parse fail, fileName: {}", configurationFile.getName(), e);
        } catch (IOException e) {
            logger.error("read alias exception, fileName: {}", configurationFile.getName(), e);
        }
    }

    private Object findAliasPackage(Object config) {
        Object typeAliasesPackage = null;
        if (config != null) {
            if (config instanceof Map) {
                Map mapConfig = (Map) config;
                typeAliasesPackage = mapConfig.get("type-aliases-package");
                if (typeAliasesPackage == null) {
                    typeAliasesPackage = mapConfig.get("typeAliasesPackage");
                }
            }
        }
        return typeAliasesPackage;
    }

    @Nullable
    private Object findConfig(Object object) {
        Object config = null;
        if (object instanceof Map) {
            Map jsonObject = (Map) object;
            Object mybatis = jsonObject.get("mybatis");
            if (mybatis != null) {
                if (jsonObject.containsKey("mybatis-plus")) {
                    config = jsonObject.get("mybatis-plus");
                }
                if (jsonObject.containsKey("mybatisPlus")) {
                    config = jsonObject.get("mybatisPlus");
                }
            }
        }
        return config;
    }


}
