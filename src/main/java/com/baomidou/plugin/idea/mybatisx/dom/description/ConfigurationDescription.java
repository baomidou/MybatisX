package com.baomidou.plugin.idea.mybatisx.dom.description;

import com.baomidou.plugin.idea.mybatisx.dom.model.Configuration;
import com.baomidou.plugin.idea.mybatisx.util.DomUtils;
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
    public boolean isMyFile(@NotNull XmlFile file, @Nullable Module module) {
        return DomUtils.isMybatisConfigurationFile(file);
    }

}
