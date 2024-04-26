package com.baomidou.plugin.idea.mybatisx.generate.plugin;

import org.apache.commons.lang.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;
import java.util.stream.Stream;

public class IgnoreParentFieldsPlugin extends PluginAdapter {

    public IgnoreParentFieldsPlugin() {

    }


    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }


    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        final String name = field.getName();
        return noneMatch(name);
    }

    private boolean noneMatch(String name) {
        String parentFields = getProperties().getProperty("parentFields");
        if (StringUtils.isBlank(parentFields)) {
            return true;
        }
        String[] splitArray = StringUtils.split(parentFields, ",");
        if (splitArray.length == 0) {
            return true;
        }
        return Stream.of(splitArray).noneMatch(item -> item.equals(name));
    }


    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return noneMatch(introspectedColumn.getActualColumnName());
    }

    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return noneMatch(introspectedColumn.getActualColumnName());
    }


    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.setSuperClass(getProperties().getProperty("superClass"));
        return true;

    }
}
