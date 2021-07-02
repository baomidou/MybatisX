package com.baomidou.plugin.idea.mybatisx.generate.action;


import com.baomidou.plugin.idea.mybatisx.generate.dto.GenerateConfig;
import com.baomidou.plugin.idea.mybatisx.generate.dto.TableUIInfo;
import com.baomidou.plugin.idea.mybatisx.generate.dto.TemplateContext;
import com.baomidou.plugin.idea.mybatisx.generate.setting.TemplatesSettings;
import com.baomidou.plugin.idea.mybatisx.generate.template.GenerateCode;
import com.baomidou.plugin.idea.mybatisx.util.PluginExistsUtils;
import com.intellij.database.model.DasObject;
import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The type Mybatis generator main action.
 */
public class MybatisGeneratorMainAction extends AnAction {

    /**
     * 点击后打开插件主页面
     *
     * @param e
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        /**
         * 代码生成
         */
        Project project = e.getProject();
        PsiElement[] tableElements = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (tableElements == null) {
            logger.error("未选择表, 无法生成代码");
            return;
        }
        ClassGenerateDialogWrapper classGenerateDialogWrapper = new ClassGenerateDialogWrapper(project);
        List<DbTable> dbTables = Stream.of(tableElements).filter(t -> t instanceof DbTable).map(t -> (DbTable) t).collect(Collectors.toList());
        // 填充默认的选项
        classGenerateDialogWrapper.fillData(project, dbTables);
        classGenerateDialogWrapper.show();
        // 模态窗口选择 OK, 生成相关代码
        if (classGenerateDialogWrapper.getExitCode() == Messages.YES) {
            // 生成代码
            GenerateConfig generateConfig = classGenerateDialogWrapper.determineGenerateConfig();

            generateCode(project, dbTables, generateConfig);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(MybatisGeneratorMainAction.class);

    public void generateCode(Project project, List<DbTable> psiElements, GenerateConfig generateConfig) {
        try {
            // 保存配置, 更新最后一次存储的配置
            TemplatesSettings templatesSettings = TemplatesSettings.getInstance(project);
            TemplateContext templateConfigs = templatesSettings.getTemplateConfigs();
            templateConfigs.setGenerateConfig(generateConfig);
            templatesSettings.setTemplateConfigs(templateConfigs);

            Map<String, DbTable> tableMapping = psiElements.stream().collect(Collectors.toMap(DasObject::getName, a -> a, (a, b) -> a));
            for (TableUIInfo uiInfo : generateConfig.getTableUIInfoList()) {
                String tableName = uiInfo.getTableName();
                DbTable dbTable = tableMapping.get(tableName);
                if (dbTable != null) {
                    // 生成代码
                    GenerateCode.generate(project,
                        generateConfig,
                        templatesSettings.getTemplateSettingMap(),
                        dbTable,
                        uiInfo.getClassName(),
                        uiInfo.getTableName());
                }
            }
            VirtualFileManager.getInstance().refreshWithoutFileWatcher(true);
            logger.info("全部代码生成成功, 文件内容已更新. config: {}", generateConfig);
        } catch (Exception e) {
            logger.error("生成代码出错", e);
        }
    }


    @Override
    public void update(@NotNull AnActionEvent e) {
        Boolean visible = null;
        PsiElement[] psiElements = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (psiElements == null || psiElements.length == 0) {
            visible = false;
        }
        boolean existsDbTools = PluginExistsUtils.existsDbTools();
        if (!existsDbTools) {
            visible = false;
        }
        if (visible == null) {
            if (!Stream.of(psiElements).allMatch(item -> CheckMatch.checkAssignableFrom(item.getClass()))) {
                visible = false;
            }
        }
        // 未安装Database Tools插件时，不展示菜单
        if (visible != null) {
            e.getPresentation().setEnabledAndVisible(visible);
        }

    }

    private static class CheckMatch {
        public static boolean checkAssignableFrom(Class<? extends PsiElement> aClass) {
            try {
                return DbTable.class.isAssignableFrom(aClass);
            } catch (Exception e) {
                return false;
            }
        }
    }

}
