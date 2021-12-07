package com.baomidou.plugin.idea.mybatisx.generate.dto;



import com.baomidou.plugin.idea.mybatisx.generate.classname.ClassNameStrategy;

import java.util.Collections;
import java.util.List;

/**
 * 默认生成器配置
 * @author : ls9527
 * @date : 2021/7/2
 */
public class DefaultGenerateConfig extends GenerateConfig {
    private TemplateContext templateContext;

    public DefaultGenerateConfig(TemplateContext templateContext) {
        this.templateContext = templateContext;
    }

    @Override
    public String getModuleName() {
        return templateContext.getModuleName();
    }

    @Override
    public String getAnnotationType() {
        return templateContext.getAnnotationType();
    }

    @Override
    public List<ModuleInfoGo> getModuleUIInfoList() {
        return Collections.emptyList();
    }

    @Override
    public boolean isNeedsComment() {
        return true;
    }

    @Override
    public boolean isNeedToStringHashcodeEquals() {
        return true;
    }

    @Override
    public String getBasePackage() {
        return "generator";
    }

    @Override
    public String getRelativePackage() {
        return "domain";
    }

    @Override
    public String getBasePath() {
        return "src/main/java";
    }


    @Override
    public String getEncoding() {
        return "UTF-8";
    }

    @Override
    public String getClassNameStrategy() {
        return ClassNameStrategy.ClassNameStrategyEnum.CAMEL.name();
    }
}
