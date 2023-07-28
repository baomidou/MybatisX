package com.baomidou.plugin.idea.mybatisx.generate.template;

import org.mybatis.generator.api.dom.DefaultJavaFormatter;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.ArrayList;
import java.util.List;

import static org.mybatis.generator.api.dom.java.render.RenderingUtilities.renderImports;
import static org.mybatis.generator.api.dom.java.render.RenderingUtilities.renderInnerClassNoIndent;
import static org.mybatis.generator.api.dom.java.render.RenderingUtilities.renderPackage;
import static org.mybatis.generator.api.dom.java.render.RenderingUtilities.renderStaticImports;

public class CustomJavaFormatter extends DefaultJavaFormatter {
    @Override
    public String visit(TopLevelClass topLevelClass) {
        // refer: org.mybatis.generator.api.dom.java.render.TopLevelClassRenderer
        List<String> lines = new ArrayList<>();

        lines.addAll(topLevelClass.getFileCommentLines());
        lines.addAll(renderPackage(topLevelClass));
        lines.addAll(renderStaticImports(topLevelClass));
        lines.addAll(renderImports(topLevelClass));
        lines.addAll(renderInnerClassNoIndent(topLevelClass, topLevelClass));
        String lineSeparator = "\n";
//        final String lineSeparator = System.getProperty("line.separator");
        return String.join(lineSeparator, lines);
    }
}
