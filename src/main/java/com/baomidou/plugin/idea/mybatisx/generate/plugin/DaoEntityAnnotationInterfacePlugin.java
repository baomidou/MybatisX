package com.baomidou.plugin.idea.mybatisx.generate.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Interface;

import java.util.List;

/**
 * Entity  注解
 *
 * @author ls9527
 */
public class DaoEntityAnnotationInterfacePlugin extends PluginAdapter {

    @Override
    public boolean clientGenerated(Interface inter, IntrospectedTable introspectedTable) {

        inter.addJavaDocLine("/**");
        inter.addJavaDocLine(" * @Entity " + getProperties().getProperty("domainName"));
        inter.addJavaDocLine(" */");
        return true;
    }


    @Override
    public boolean validate(List<String> list) {
        return true;
    }


}
