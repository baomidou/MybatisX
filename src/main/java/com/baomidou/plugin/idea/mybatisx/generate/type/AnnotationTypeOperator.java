package com.baomidou.plugin.idea.mybatisx.generate.type;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.TopLevelClass;

public interface AnnotationTypeOperator {
    void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable);

    void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn);

    void addSerialVersionUIDAnnotation(Field field, IntrospectedTable introspectedTable);
}
