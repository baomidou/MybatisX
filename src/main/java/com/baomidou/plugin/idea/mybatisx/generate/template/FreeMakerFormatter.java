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
    private Context context;

    public FreeMakerFormatter(TemplateSettingDTO templateSettingDTO, CustomTemplateRoot rootObject, ClassInfo classInfo, String templateText) {
        this.templateSettingDTO = templateSettingDTO;
        this.rootObject = rootObject;
        this.classInfo = classInfo;
        this.templateText = templateText;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    /**
     * free marker 生成的代码, 不关心这里设置的任何属性
     *
     * @param compilationUnit
     * @return
     */
    public String getFormattedContent(CompilationUnit compilationUnit) {
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
            String pathname = rootObject.getTargetProject() + "/" + templateSettingDTO.getBasePath();
            cfg.setDirectoryForTemplateLoading(new File(pathname));
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
            return writer.toString();
        } catch (IOException | TemplateException e) {
            StringWriter out = new StringWriter();
            PrintWriter stringWriter = new PrintWriter(out);
            e.printStackTrace(stringWriter);
            return "填充模板出错," + out.toString();
        }
    }

}
