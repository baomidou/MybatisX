package com.baomidou.plugin.idea.mybatisx.generate.util;

import androidx.annotation.NonNull;
import com.baomidou.plugin.idea.mybatisx.generate.dto.DomainInfo;
import com.baomidou.plugin.idea.mybatisx.generate.ui.CodeGenerateUI;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : ls9527
 * @date : 2021/7/2
 */
@Slf4j
public class DomainPlaceHolder {

    public static String replace(String templateText, DomainInfo domainInfo) {
        if (templateText == null || templateText.trim().isEmpty()) {
            return templateText;
        }
        if (domainInfo == null) {
            return templateText;
        }
        try {
            String defaultTemplateName = "default";
            Configuration cfg = createCfg(templateText, domainInfo, defaultTemplateName);

            Template templateName = cfg.getTemplate(defaultTemplateName);
            Writer writer = new StringWriter();
            Map<String, Object> map = new HashMap<>();

            map.put(CodeGenerateUI.DOMAIN, domainInfo);
            templateName.process(map, writer);
            return writer.toString();
        } catch (Exception e) {
            log.error("动态参数替换错误", e);
            return templateText;
        }
    }

    @NonNull
    private static Configuration createCfg(String templateText, DomainInfo domainInfo, String defaultTemplateName) {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        // 设置模板加载器
        StringTemplateLoader templateLoader = new StringTemplateLoader();
        templateLoader.putTemplate(defaultTemplateName, templateText);
        cfg.setTemplateLoader(templateLoader);

        cfg.setDefaultEncoding(domainInfo.getEncoding());
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        return cfg;
    }
}
