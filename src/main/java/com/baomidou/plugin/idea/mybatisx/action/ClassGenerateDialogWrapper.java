package com.baomidou.plugin.idea.mybatisx.action;

import com.baomidou.plugin.idea.mybatisx.generate.template.GenerateCode;
import com.baomidou.plugin.idea.mybatisx.generate.dto.GenerateConfig;
import com.baomidou.plugin.idea.mybatisx.generate.setting.TemplatesSettings;
import com.baomidou.plugin.idea.mybatisx.generate.dto.TemplateContext;
import com.baomidou.plugin.idea.mybatisx.generate.ui.CodeGenerateUI;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class ClassGenerateDialogWrapper extends DialogWrapper {


    private static final String ERROR = "Error";
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
            if (psiElements.length == 1) {
                // 获取配置
                GenerateConfig generateConfig = codeGenerateUI.getGenerateConfig(project);
                // 保存配置, 更新最后一次存储的配置
                TemplatesSettings templatesSettings = TemplatesSettings.getInstance(project);
                TemplateContext templateConfigs = templatesSettings.getTemplateConfigs();
                templateConfigs.setGenerateConfig(generateConfig);
                templatesSettings.setTemplateConfigs(templateConfigs);
                // 生成代码
                GenerateCode.generate(project, generateConfig, psiElements[0]);
            }
        } catch (Exception e) {
            Messages.showMessageDialog(e.getMessage(), ERROR, Messages.getErrorIcon());
            logger.error("生成代码出错", e);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(ClassGenerateDialogWrapper.class);

    public void fillData(Project project, PsiElement[] tableElements) {
        TemplatesSettings instance = TemplatesSettings.getInstance(project);
        TemplateContext templateContext = instance.getTemplateConfigs();
        codeGenerateUI.fillData(project, tableElements, templateContext);
    }
}
