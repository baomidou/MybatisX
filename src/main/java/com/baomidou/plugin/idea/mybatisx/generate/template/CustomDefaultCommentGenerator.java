package com.baomidou.plugin.idea.mybatisx.generate.template;

import com.baomidou.plugin.idea.mybatisx.generate.type.AnnotationTypeOperator;
import com.baomidou.plugin.idea.mybatisx.generate.type.AnnotationTypeOperatorFactory;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.MergeConstants;
import org.mybatis.generator.internal.DefaultCommentGenerator;

import java.util.Properties;
import java.util.Set;

public class CustomDefaultCommentGenerator extends DefaultCommentGenerator implements CommentGenerator {
    private AnnotationTypeOperator annotationTypeOperator;

    public CustomDefaultCommentGenerator() {

    }

    @Override
    public void addJavaFileComment(CompilationUnit compilationUnit) {

    }

    @Override
    public void addConfigurationProperties(Properties properties) {
        super.addConfigurationProperties(properties);
        String annotations = properties.getProperty("annotationType");
        this.annotationTypeOperator = AnnotationTypeOperatorFactory.findByType(annotations);
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
    }

    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine(" * " + introspectedTable.getRemarks());
        topLevelClass.addJavaDocLine(" * @TableName " + introspectedTable.getFullyQualifiedTable().getIntrospectedTableName());
        topLevelClass.addJavaDocLine(" */");
        annotationTypeOperator.addModelClassComment(topLevelClass, introspectedTable);

    }


    @Override
    public void addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        method.addJavaDocLine("/**");
        if (!StringUtils.isEmpty(introspectedColumn.getRemarks())) {
            method.addJavaDocLine(" * " + introspectedColumn.getRemarks());
        }
        method.addJavaDocLine(" */");
    }

    @Override
    public void addComment(XmlElement xmlElement) {
        xmlElement.addElement(new TextElement("<!--" + MergeConstants.NEW_ELEMENT_TAG + "-->"));
    }

    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
    }

    @Override
    public void addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        method.addJavaDocLine("/**");
        if (!StringUtils.isEmpty(introspectedColumn.getRemarks())) {
            method.addJavaDocLine(" * " + introspectedColumn.getRemarks());
        }
        method.addJavaDocLine(" */");
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        field.addJavaDocLine("/**");
        StringBuilder sb = new StringBuilder();
        sb.append(" * ");
        if (!StringUtils.isEmpty(introspectedColumn.getRemarks())) {
            sb.append(introspectedColumn.getRemarks());
        }
        field.addJavaDocLine(sb.toString());

        field.addJavaDocLine(" */");

        annotationTypeOperator.addFieldComment(field, introspectedTable, introspectedColumn);

    }


    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
        innerClass.addJavaDocLine("/**");
        innerClass.addJavaDocLine(" */");
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
        field.addJavaDocLine("/**");
        field.addJavaDocLine(" * " + field.getName());
        field.addJavaDocLine(" */");
    }

    @Override
    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> imports) {
    }

    @Override
    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> imports) {
        addSetterComment(method, introspectedTable, introspectedColumn);
    }

    @Override
    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> imports) {

        annotationTypeOperator.addSerialVersionUIDAnnotation(field,introspectedTable);
    }

    @Override
    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> imports) {
        addFieldComment(field, introspectedTable, introspectedColumn);
    }


}
