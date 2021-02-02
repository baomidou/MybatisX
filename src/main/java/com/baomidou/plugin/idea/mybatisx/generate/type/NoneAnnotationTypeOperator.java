package com.baomidou.plugin.idea.mybatisx.generate.type;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.TopLevelClass;

/**
 * 什么也不做
 */
public class NoneAnnotationTypeOperator implements AnnotationTypeOperator {
    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {

    }

    @Override
    public void addSerialVersionUIDAnnotation(Field field, IntrospectedTable introspectedTable) {

    }
}
