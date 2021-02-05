package com.baomidou.plugin.idea.mybatisx.generate.template;

import com.baomidou.plugin.idea.mybatisx.generate.dto.CustomTemplateRoot;
import com.baomidou.plugin.idea.mybatisx.generate.dto.TemplateSettingDTO;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.mybatis.generator.api.JavaFormatter;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.config.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * freemarker 模板格式化代码生成
 */
public class FreeMakerFormatter implements JavaFormatter {

    public static final String TEMPLATE = "template";
    private TemplateSettingDTO templateSettingDTO;
    private CustomTemplateRoot rootObject;
    private final ClassInfo classInfo;
    private String templateText;
    private String modulePath;
    private Context context;

    public FreeMakerFormatter(TemplateSettingDTO templateSettingDTO, CustomTemplateRoot rootObject, ClassInfo classInfo, String templateText, String modulePath) {
        this.templateSettingDTO = templateSettingDTO;
        this.rootObject = rootObject;
        this.classInfo = classInfo;
        this.templateText = templateText;
        this.modulePath = modulePath;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private static final Logger logger = LoggerFactory.getLogger(FreeMakerFormatter.class);

    /**
     * free marker 生成的代码, 不关心这里设置的任何属性
     *
     * @param compilationUnit
     * @return
     */
    public String getFormattedContent(CompilationUnit compilationUnit) {
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
            cfg.setDirectoryForTemplateLoading(new File(modulePath));
            // 设置模板加载器
            StringTemplateLoader templateLoader = new StringTemplateLoader();
            templateLoader.putTemplate(TEMPLATE, templateText);
            cfg.setTemplateLoader(templateLoader);

            cfg.setDefaultEncoding(templateSettingDTO.getEncoding());
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

            Template templateName = cfg.getTemplate(TEMPLATE);
            Writer writer = new StringWriter();
            Map<String, Object> map = new HashMap<>();

            map.put("baseInfo", templateSettingDTO);
            map.put("tableClass", classInfo);
            map.putAll(rootObject.toMap());
            templateName.process(map, writer);
            final String templateContent = writer.toString();
            logger.info("模板内容生成成功, pathname: {}", modulePath);
            return templateContent;
        } catch (IOException | TemplateException e) {
            StringWriter out = new StringWriter();
            PrintWriter stringWriter = new PrintWriter(out);
            e.printStackTrace(stringWriter);
            logger.error("模板内容生成失败, pathname: {}", e);
            return "填充模板出错," + out.toString();
        }
    }

}
