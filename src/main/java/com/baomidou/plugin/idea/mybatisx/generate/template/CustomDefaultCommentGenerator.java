package com.baomidou.plugin.idea.mybatisx.generate.template;

import com.baomidou.plugin.idea.mybatisx.generate.type.AnnotationTypeOperator;
import com.baomidou.plugin.idea.mybatisx.generate.type.AnnotationTypeOperatorFactory;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import org.jetbrains.annotations.NotNull;
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
    private Boolean needsComment;

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
        this.needsComment = determineNeedsComment(properties);
    }

    @NotNull
    private Boolean determineNeedsComment(Properties properties) {
        String needsComment = properties.getProperty("needsComment");
        if (needsComment == null) {
            needsComment = "false";
        }
        return Boolean.valueOf(needsComment);
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
    }

    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        topLevelClass.addJavaDocLine("/**");
        // 加入表注释
        if (needsComment) {
            String remarks = introspectedTable.getRemarks();
            if (remarks == null) {
                remarks = "";
            }
            topLevelClass.addJavaDocLine(" * " + remarks);
        }
        // 强制加入@TableName注释, 为了后续的JPA识别表名
        topLevelClass.addJavaDocLine(" * @TableName " + introspectedTable.getFullyQualifiedTable().getIntrospectedTableName());
        topLevelClass.addJavaDocLine(" */");
        annotationTypeOperator.addModelClassComment(topLevelClass, introspectedTable);

    }


    @Override
    public void addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        if (!needsComment) {
            return;
        }
        method.addJavaDocLine("/**");
        String remarks = introspectedColumn.getRemarks();
        if (StringUtils.isEmpty(remarks)) {
            remarks = "";
        }
        method.addJavaDocLine(" * " + remarks);

        method.addJavaDocLine(" */");
    }

    @Override
    public void addComment(XmlElement xmlElement) {
        if (!needsComment) {
            return;
        }
        xmlElement.addElement(new TextElement("<!--" + MergeConstants.NEW_ELEMENT_TAG + "-->"));
    }

    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
    }

    @Override
    public void addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        if (!needsComment) {
            return;
        }
        method.addJavaDocLine("/**");
        String remarks = introspectedColumn.getRemarks();
        if (StringUtils.isEmpty(remarks)) {
            remarks = "";
        }
        method.addJavaDocLine(" * " + remarks);

        method.addJavaDocLine(" */");
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        if (!needsComment) {
            return;
        }
        field.addJavaDocLine("/**");
        String remarks = introspectedColumn.getRemarks();
        if (StringUtils.isEmpty(remarks)) {
            remarks = "";
        }
        field.addJavaDocLine(" * " + remarks);
        field.addJavaDocLine(" */");

        annotationTypeOperator.addFieldComment(field, introspectedTable, introspectedColumn);

    }


    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
        if (!needsComment) {
            return;
        }
        innerClass.addJavaDocLine("/**");
        innerClass.addJavaDocLine(" */");
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
        if (!needsComment) {
            return;
        }
        field.addJavaDocLine("/**");
        String remarks = field.getName();
        if (StringUtils.isEmpty(remarks)) {
            remarks = "";
        }
        field.addJavaDocLine(" * " + remarks);
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
        if (!needsComment) {
            return;
        }
        annotationTypeOperator.addSerialVersionUIDAnnotation(field, introspectedTable);
    }

    @Override
    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> imports) {
        addFieldComment(field, introspectedTable, introspectedColumn);
    }


}
