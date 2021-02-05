package com.baomidou.plugin.idea.mybatisx.generate.action;

import com.baomidou.plugin.idea.mybatisx.generate.dto.GenerateConfig;
import com.baomidou.plugin.idea.mybatisx.generate.dto.TemplateContext;
import com.baomidou.plugin.idea.mybatisx.generate.dto.TemplateSettingDTO;
import com.baomidou.plugin.idea.mybatisx.generate.setting.TemplatesSettings;
import com.baomidou.plugin.idea.mybatisx.generate.template.GenerateCode;
import com.baomidou.plugin.idea.mybatisx.generate.ui.CodeGenerateUI;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ClassGenerateDialogWrapper extends DialogWrapper {

    private static final Logger logger = LoggerFactory.getLogger(ClassGenerateDialogWrapper.class);

    private CodeGenerateUI codeGenerateUI = new CodeGenerateUI();

    protected ClassGenerateDialogWrapper(@Nullable Project project) {
        super(project);
        super.init();
        setTitle("Generate Options");
        setSize(600, 400);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return codeGenerateUI.getRootPanel();
    }

    public void generateCode(Project project, PsiElement[] psiElements) {
        try {
            // 获取配置
            GenerateConfig generateConfig = codeGenerateUI.getGenerateConfig(project);

            // 保存配置, 更新最后一次存储的配置
            TemplatesSettings templatesSettings = TemplatesSettings.getInstance(project);
            TemplateContext templateConfigs = templatesSettings.getTemplateConfigs();
            templateConfigs.setGenerateConfig(generateConfig);
            templatesSettings.setTemplateConfigs(templateConfigs);

            for (PsiElement psiElement : psiElements) {
                // 生成代码
                GenerateCode.generate(generateConfig, templatesSettings.getTemplateSettingMap(), psiElement);
            }
            VirtualFileManager.getInstance().refreshWithoutFileWatcher(true);
            logger.info("全部代码生成成功, 文件内容已更新. config: {}",generateConfig);
        } catch (Exception e) {
            logger.error("生成代码出错", e);
        }
    }


    public void fillData(Project project, PsiElement[] tableElements) {
        TemplatesSettings templatesSettings = TemplatesSettings.getInstance(project);
        final TemplateContext templateContext = templatesSettings.getTemplateConfigs();
        GenerateConfig generateConfig = templateContext.getGenerateConfig();
        if (generateConfig == null) {
            generateConfig = new DefaultGenerateConfig(templateContext);
        }

        final Map<String, List<TemplateSettingDTO>> settingMap = templatesSettings.getTemplateSettingMap();
        if (settingMap.isEmpty()) {
            throw new RuntimeException("无法获取模板");
        }
        codeGenerateUI.fillData(project,
            tableElements,
            generateConfig,
            templateContext.getTemplateName(),
            settingMap);
    }

    /**
     * 默认生成器配置
     */
    private class DefaultGenerateConfig extends GenerateConfig {
        private TemplateContext templateContext;

        public DefaultGenerateConfig(TemplateContext templateContext) {
            this.templateContext = templateContext;
        }

        @Override
        public String getTargetProject() {
            return templateContext.getProjectPath();
        }

        @Override
        public String getModuleName() {
            return templateContext.getModuleName();
        }

        @Override
        public String getAnnotationType() {
            return templateContext.getAnnotationType();
        }

        @Override
        public List<String> getExtraTemplateNames() {
            return Collections.emptyList();
        }

        @Override
        public boolean isNeedsComment() {
            return true;
        }

        @Override
        public boolean isNeedToStringHashcodeEquals() {
            return true;
        }

        @Override
        public String getBasePackage() {
            return "generator";
        }

        @Override
        public String getRelativePackage() {
            return "domain";
        }

        @Override
        public String getBasePath() {
            return "src/main/java";
        }


        @Override
        public String getEncoding() {
            return "UTF-8";
        }
    }

}
