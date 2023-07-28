package com.baomidou.plugin.idea.mybatisx.generate.type;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;

public class MybatisPlus3AnnotationTypeOperator implements AnnotationTypeOperator {
    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addImportedType(new FullyQualifiedJavaType("com.baomidou.mybatisplus.annotation.IdType"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("com.baomidou.mybatisplus.annotation.TableField"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("com.baomidou.mybatisplus.annotation.TableId"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("com.baomidou.mybatisplus.annotation.TableName"));
        topLevelClass.addAnnotation("@TableName(value =\"" + introspectedTable.getFullyQualifiedTableNameAtRuntime() + "\")");
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        for (IntrospectedColumn column : introspectedTable.getPrimaryKeyColumns()) {
            if (introspectedColumn == column) {
                if (introspectedColumn.isAutoIncrement()) {
                    if (useActualColumnAnnotationInject(introspectedColumn)) {
                        field.addAnnotation("@TableId(value = \"" + introspectedColumn.getActualColumnName() + "\", type = IdType.AUTO)");
                    } else {
                        field.addAnnotation("@TableId(type = IdType.AUTO)");
                    }

                } else {
                    if (useActualColumnAnnotationInject(introspectedColumn)) {
                        field.addAnnotation("@TableId(value = \"" + introspectedColumn.getActualColumnName() + "\")");
                    } else {
                        field.addAnnotation("@TableId");
                    }
                }
                break;
            }
        }

        if (useActualColumnAnnotationInject(introspectedColumn)) {
            if (!introspectedTable.getPrimaryKeyColumns().contains(introspectedColumn)) {
                field.addAnnotation("@TableField(value = \"" + introspectedColumn.getActualColumnName() + "\")");
            }
        }
    }

    @Override
    public void addSerialVersionUIDAnnotation(Field field, IntrospectedTable introspectedTable) {
        field.addAnnotation("@TableField(exist = false)");
    }

    private boolean useActualColumnAnnotationInject(IntrospectedColumn introspectedColumn) {
        return "true".equals(introspectedColumn.getProperties().getProperty("useActualColumnAnnotationInject"));
    }
}
