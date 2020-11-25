package com.baomidou.plugin.idea.mybatisx.alias;

import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.spring.CommonSpringModel;
import com.intellij.spring.boot.model.SpringBootModelConfigFileContributor;
import com.intellij.spring.model.utils.SpringModelUtils;
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
import java.util.Set;

/**
 * The type springboot Bean alias resolver.
 *
 * @author ls9527
 */
public class SpringBootPackageResolver extends PackageAliasResolver {

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
        CommonSpringModel springModel = SpringModelUtils.getInstance().getSpringModel(element);

        Set<String> classSet = new HashSet<>();
        ExtensionPointName<SpringBootModelConfigFileContributor> objectExtensionPointName = ExtensionPointName.create("com.intellij.spring.boot.modelConfigFileContributor");
        List<SpringBootModelConfigFileContributor> extensionList = objectExtensionPointName.getExtensionList();
        for (SpringBootModelConfigFileContributor extension : extensionList) {
            List<VirtualFile> configurationFiles = extension.getConfigurationFiles(springModel.getModule(), true);
            for (VirtualFile configurationFile : configurationFiles) {
                readAliasesPackage(classSet, configurationFile);
            }
        }

        return classSet;
    }

    private void readAliasesPackage(Set<String> classSet, VirtualFile configurationFile) {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = configurationFile.getInputStream();) {
            Map jsonObject = yaml.loadAs(inputStream, Map.class);
            if (jsonObject == null) {
                return;
            }
            Object config = null;
            Object mybatis = jsonObject.get("mybatis");
            if (mybatis != null) {
                config = mybatis;
            }
            if (config == null) {
                if (jsonObject.containsKey("mybatis-plus")) {
                    config = jsonObject.get("mybatis-plus");
                }
                if (jsonObject.containsKey("mybatisPlus")) {
                    config = jsonObject.get("mybatisPlus");
                }
            }
            Object typeAliasesPackage = null;
            if (config != null) {
                Map mapConfig = (Map) config;
                typeAliasesPackage = mapConfig.get("type-aliases-package");
                if (typeAliasesPackage == null) {
                    typeAliasesPackage = mapConfig.get("typeAliasesPackage");
                }
            }
            if (typeAliasesPackage != null) {
                if (!StringUtils.isEmpty(typeAliasesPackage.toString())) {
                    classSet.add(typeAliasesPackage.toString());
                }
            }

        } catch (ParserException | ComposerException e) {
            logger.info("yml parse fail", e);
        } catch (IOException e) {
            logger.error("read alias exception", e);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(SpringBootPackageResolver.class);


}
