package com.baomidou.plugin.idea.mybatisx.generate.util;

import com.baomidou.plugin.idea.mybatisx.generate.dto.DomainInfo;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : ls9527
 * @date : 2021/7/2
 */
public class DomainPlaceHolder {

    private static final Logger logger = LoggerFactory.getLogger(DomainPlaceHolder.class);

    public static String replace(String templateText, DomainInfo domainInfo) {
        if (templateText == null || "".equals(templateText.trim())) {
            return templateText;
        }
        if (domainInfo == null) {
            return templateText;
        }
        try {
            String defaultTemplateName = "default";
            freemarker.template.Configuration cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_22);
            // 设置模板加载器
            StringTemplateLoader templateLoader = new StringTemplateLoader();
            templateLoader.putTemplate(defaultTemplateName, templateText);
            cfg.setTemplateLoader(templateLoader);

            cfg.setDefaultEncoding(domainInfo.getEncoding());
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

            Template templateName = cfg.getTemplate(defaultTemplateName);
            Writer writer = new StringWriter();
            Map<String, Object> map = new HashMap<>();

            map.put("domain", domainInfo);
            templateName.process(map, writer);
            return writer.toString();
        } catch (Exception e) {
            logger.error("动态参数替换错误", e);
            return templateText;
        }
    }
}
