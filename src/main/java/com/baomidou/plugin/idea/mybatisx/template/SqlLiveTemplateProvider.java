package com.baomidou.plugin.idea.mybatisx.template;

import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider;

import org.jetbrains.annotations.Nullable;

/**
 * @author yanglin
 */
public class SqlLiveTemplateProvider implements DefaultLiveTemplatesProvider {

    public static final String[] TEMPLATE_FILES = {"liveTemplates/sql"};

    @Override
    public String[] getDefaultLiveTemplateFiles() {
        return TEMPLATE_FILES;
    }

    @Nullable
    @Override
    public String[] getHiddenLiveTemplateFiles() {
        return null;
    }
}
