package com.baomidou.plugin.idea.mybatisx.intention;

import com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest.ConditionFieldWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest.NeverContainsFieldWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.PlatformGenerator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * 在mapper类中通过名字生成方法和xml内容
 *
 * @author ls9527
 */
public class GenerateMapperMethodSmartJpaAction extends GenerateMapperMethodSmartJpaAdvanceAction {


    private static final Logger logger = LoggerFactory.getLogger(GenerateMapperMethodSmartJpaAction.class);

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getText() {
        return "Generate Mybatis Sql";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "Statement Complete";
    }


    @Override
    protected Optional<ConditionFieldWrapper> getConditionFieldWrapper(@NotNull Project project, PlatformGenerator platformGenerator) {
        return Optional.of(new NeverContainsFieldWrapper());
    }
}
