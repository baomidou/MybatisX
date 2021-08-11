package com.baomidou.plugin.idea.mybatisx.dom.description;

import com.baomidou.plugin.idea.mybatisx.dom.model.Configuration;
import com.intellij.openapi.module.Module;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The type Configuration description.
 *
 * @author yanglin
 */
public class ConfigurationDescription extends DomFileDescription<Configuration> {

    /**
     * Instantiates a new Configuration description.
     */
    public ConfigurationDescription() {
        super(Configuration.class, "configuration");
    }

    @Override
    protected void initializeFileDescription() {
        registerNamespacePolicy("MybatisConfiguration", "-//mybatis.org//DTD Config 3.0//EN", "http://mybatis.org/dtd/mybatis-3-config.dtd");
    }
}
