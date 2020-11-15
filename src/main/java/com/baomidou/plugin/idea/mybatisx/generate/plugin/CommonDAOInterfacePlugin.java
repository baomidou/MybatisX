package com.baomidou.plugin.idea.mybatisx.generate.plugin;

/**
 * @author ls9527
 */

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.JavaFormatter;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.internal.util.StringUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CommonDAOInterfacePlugin extends PluginAdapter {
    private static final FullyQualifiedJavaType PARAM_ANNOTATION_TYPE = new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param");
    private static final FullyQualifiedJavaType LIST_TYPE = FullyQualifiedJavaType.getNewListInstance();
    private static final FullyQualifiedJavaType SERIALIZEBLE_TYPE = new FullyQualifiedJavaType("java.io.Serializable");
    private List<Method> methods = new ArrayList();
    private ShellCallback shellCallback = null;

    public CommonDAOInterfacePlugin() {
        this.shellCallback = new DefaultShellCallback(false);
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        boolean hasPk = introspectedTable.hasPrimaryKeyColumns();
        JavaFormatter javaFormatter = this.context.getJavaFormatter();
        String daoTargetDir = this.context.getJavaClientGeneratorConfiguration().getTargetProject();
        String daoTargetPackage = this.context.getJavaClientGeneratorConfiguration().getTargetPackage();
        List<GeneratedJavaFile> mapperJavaFiles = new ArrayList();
        String javaFileEncoding = this.context.getProperty("javaFileEncoding");
        Interface mapperInterface = new Interface(daoTargetPackage + ".MyBatisBaseDao");
        if (StringUtility.stringHasValue(daoTargetPackage)) {
            mapperInterface.addImportedType(PARAM_ANNOTATION_TYPE);
            mapperInterface.addImportedType(LIST_TYPE);
            mapperInterface.addImportedType(SERIALIZEBLE_TYPE);
            mapperInterface.setVisibility(JavaVisibility.PUBLIC);
            mapperInterface.addJavaDocLine("/**");
            mapperInterface.addJavaDocLine(" * DAO公共基类，由MybatisGenerator自动生成请勿修改");
            mapperInterface.addJavaDocLine(" * @param <Model> The Model Class 这里是泛型不是Model类");
            mapperInterface.addJavaDocLine(" * @param <PK> The Primary Key Class 如果是无主键，则可以用Model来跳过，如果是多主键则是Key类");
            mapperInterface.addJavaDocLine(" * @param <E> The Example Class");
            mapperInterface.addJavaDocLine(" */");
            FullyQualifiedJavaType daoBaseInterfaceJavaType = mapperInterface.getType();
            daoBaseInterfaceJavaType.addTypeArgument(new FullyQualifiedJavaType("Model"));
            daoBaseInterfaceJavaType.addTypeArgument(new FullyQualifiedJavaType("PK extends Serializable"));
            daoBaseInterfaceJavaType.addTypeArgument(new FullyQualifiedJavaType("E"));

            if (!this.methods.isEmpty()) {
                for (Method method : methods) {
                    mapperInterface.addMethod(method);
                }
            }

            GeneratedJavaFile mapperJavafile = new GeneratedJavaFile(mapperInterface, daoTargetDir, javaFileEncoding, javaFormatter);

            try {
                File mapperDir = this.shellCallback.getDirectory(daoTargetDir, daoTargetPackage);
                File mapperFile = new File(mapperDir, mapperJavafile.getFileName());
                if (!mapperFile.exists()) {
                    mapperJavaFiles.add(mapperJavafile);
                }
            } catch (ShellException e) {
                e.printStackTrace();
            }
        }

        return mapperJavaFiles;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        interfaze.addJavaDocLine("/**");
        interfaze.addJavaDocLine(" * " + interfaze.getType().getShortName() + "继承基类");
        interfaze.addJavaDocLine(" */");
        String daoSuperClass = interfaze.getType().getPackageName() + ".MyBatisBaseDao";
        FullyQualifiedJavaType daoSuperType = new FullyQualifiedJavaType(daoSuperClass);
        String targetPackage = introspectedTable.getContext().getJavaModelGeneratorConfiguration().getTargetPackage();
        String domainObjectName = introspectedTable.getTableConfiguration().getDomainObjectName();
        FullyQualifiedJavaType baseModelJavaType = new FullyQualifiedJavaType(targetPackage + "." + domainObjectName);
        daoSuperType.addTypeArgument(baseModelJavaType);
        FullyQualifiedJavaType primaryKeyTypeJavaType = null;
        if (introspectedTable.getPrimaryKeyColumns().size() > 1) {
            primaryKeyTypeJavaType = new FullyQualifiedJavaType(targetPackage + "." + domainObjectName + "Key");
        } else if (introspectedTable.hasPrimaryKeyColumns()) {
            primaryKeyTypeJavaType = ((IntrospectedColumn)introspectedTable.getPrimaryKeyColumns().get(0)).getFullyQualifiedJavaType();
        } else {
            primaryKeyTypeJavaType = baseModelJavaType;
        }

        daoSuperType.addTypeArgument(primaryKeyTypeJavaType);
        String exampleType = introspectedTable.getExampleType();
        FullyQualifiedJavaType exampleTypeJavaType = new FullyQualifiedJavaType(exampleType);
        daoSuperType.addTypeArgument(exampleTypeJavaType);
        interfaze.addImportedType(primaryKeyTypeJavaType);
        interfaze.addImportedType(exampleTypeJavaType);
        interfaze.addImportedType(baseModelJavaType);
        interfaze.addImportedType(daoSuperType);
        interfaze.addSuperInterface(daoSuperType);
        return true;
    }
    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    private void interceptExampleParam(Method method) {
        method.getParameters().clear();
        method.addParameter(new Parameter(new FullyQualifiedJavaType("E"), "example"));
        this.methods.add(method);
    }

    private void interceptPrimaryKeyParam(Method method) {
        method.getParameters().clear();
        method.addParameter(new Parameter(new FullyQualifiedJavaType("PK"), "id"));
        this.methods.add(method);
    }

    private void interceptModelParam(Method method) {
        method.getParameters().clear();
        method.addParameter(new Parameter(new FullyQualifiedJavaType("Model"), "record"));
        this.methods.add(method);
    }

    private void interceptModelAndExampleParam(Method method) {
        List<Parameter> parameters = method.getParameters();
        if (parameters.size() == 1) {
            this.interceptExampleParam(method);
        } else {
            method.getParameters().clear();
            Parameter parameter1 = new Parameter(new FullyQualifiedJavaType("Model"), "record");
            parameter1.addAnnotation("@Param(\"record\")");
            method.addParameter(parameter1);
            Parameter parameter2 = new Parameter(new FullyQualifiedJavaType("E"), "example");
            parameter2.addAnnotation("@Param(\"example\")");
            method.addParameter(parameter2);
            this.methods.add(method);
        }

    }
    @Override
    public boolean clientCountByExampleMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        this.interceptExampleParam(method);
        return false;
    }
    @Override
    public boolean clientDeleteByExampleMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        this.interceptExampleParam(method);
        return false;
    }
    @Override
    public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        this.interceptPrimaryKeyParam(method);
        return false;
    }
    @Override
    public boolean clientInsertMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        this.interceptModelParam(method);
        return false;
    }
    @Override
    public boolean clientSelectByExampleWithBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        this.interceptExampleParam(method);
        method.setReturnType(new FullyQualifiedJavaType("List<Model>"));
        return false;
    }
    @Override
    public boolean clientSelectByExampleWithoutBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        this.interceptExampleParam(method);
        method.setReturnType(new FullyQualifiedJavaType("List<Model>"));
        return false;
    }
    @Override
    public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        this.interceptPrimaryKeyParam(method);
        method.setReturnType(new FullyQualifiedJavaType("Model"));
        return false;
    }
    @Override
    public boolean clientUpdateByExampleSelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        this.interceptModelAndExampleParam(method);
        return false;
    }
    @Override
    public boolean clientUpdateByExampleWithBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        this.interceptModelAndExampleParam(method);
        return false;
    }
    @Override
    public boolean clientUpdateByExampleWithoutBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        this.interceptModelAndExampleParam(method);
        return false;
    }
    @Override
    public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        this.interceptModelParam(method);
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        this.interceptModelParam(method);
        return false;
    }
    @Override
    public boolean clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        this.interceptModelParam(method);
        return false;
    }

    @Override
    public boolean clientInsertSelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        this.interceptModelParam(method);
        return false;
    }
}
