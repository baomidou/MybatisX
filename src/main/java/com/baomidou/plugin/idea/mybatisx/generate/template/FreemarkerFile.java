package com.baomidou.plugin.idea.mybatisx.generate.template;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.JavaFormatter;
import org.mybatis.generator.api.dom.java.CompilationUnit;

public class FreemarkerFile extends GeneratedJavaFile {
    private String suffix;
    private String packageName;

    public FreemarkerFile(CompilationUnit compilationUnit, JavaFormatter javaFormatter, String targetProject, String fileEncoding, String suffix, String packageName) {
        super(compilationUnit, targetProject, fileEncoding, javaFormatter);
        this.suffix = suffix;
        this.packageName = packageName;
    }

    @Override
    public String getFileName() {
        return getCompilationUnit().getType().getShortNameWithoutTypeArguments() + suffix;
    }

    @Override
    public String getFormattedContent() {
        return super.getFormattedContent();
    }

    @Override
    public String getTargetPackage() {
        return packageName;
    }
}
