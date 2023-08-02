package com.baomidou.plugin.idea.mybatisx.system.template;

import com.baomidou.plugin.idea.mybatisx.util.Icons;
import com.intellij.ide.fileTemplates.FileTemplateDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

import static com.baomidou.plugin.idea.mybatisx.util.Icons.JAVAEE_PERSISTENCE_ID_PNG;

/**
 * The type Mybatis file template descriptor factory.
 *
 * @author yanglin
 */
public class MybatisFileTemplateDescriptorFactory implements FileTemplateGroupDescriptorFactory {

    /**
     * The constant MYBATIS_MAPPER_XML_TEMPLATE.
     */
    public static final String MYBATIS_MAPPER_XML_TEMPLATE = "Mybatis Mapper.xml";

    @Override
    public FileTemplateGroupDescriptor getFileTemplatesDescriptor() {
        Icon icon = IconLoader.getIcon(JAVAEE_PERSISTENCE_ID_PNG);
        FileTemplateGroupDescriptor group = new FileTemplateGroupDescriptor("Mybatis", icon);
        group.addTemplate(new FileTemplateDescriptor(MYBATIS_MAPPER_XML_TEMPLATE, icon));
        return group;
    }

}
