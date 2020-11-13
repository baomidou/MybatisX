package com.baomidou.plugin.idea.mybatisx.setting;

import com.baomidou.plugin.idea.mybatisx.ui.MybatisGeneratorSettingUI;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * The type Mybatis generator configurable.
 */
public class MybatisGeneratorConfigurable implements SearchableConfigurable {
    private MybatisGeneratorSettingUI mainPanel;

    @SuppressWarnings("FieldCanBeLocal")
    private final Project project;


    /**
     * Instantiates a new Mybatis generator configurable.
     *
     * @param project the project
     */
    public MybatisGeneratorConfigurable(@NotNull Project project) {
        this.project = project;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Mybatis generator setting";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return "gene.helpTopic";
    }

    @NotNull
    @Override
    public String getId() {
        return "baomidou.MybatisGeneratorConfigurable";
    }

    @Nullable
    @Override
    public Runnable enableSearch(String s) {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mainPanel = new MybatisGeneratorSettingUI();
        mainPanel.createUI(project);
        return mainPanel.getContentPane();
    }

    @Override
    public boolean isModified() {
        return mainPanel.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        mainPanel.apply();
    }

    @Override
    public void reset() {
        mainPanel.reset();
    }

    @Override
    public void disposeUIResources() {
        mainPanel = null;
    }
}
