package com.baomidou.plugin.idea.mybatisx.intention;

import com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest.ConditionFieldWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest.NeverContainsFieldWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.PlatformGenerator;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.psi.PsiClass;
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
public class GenerateSmartJpaAction extends GeneratSmartJpaAdvanceAction {


    private static final Logger logger = LoggerFactory.getLogger(GenerateSmartJpaAction.class);

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getText() {
        return "[MybatisX] Generate Mybatis Sql";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "Statement Complete";
    }


    @Override
    protected Optional<ConditionFieldWrapper> getConditionFieldWrapper(@NotNull Project project,
                                                                       PlatformGenerator platformGenerator,
                                                                       PsiClass mapperClass) {
        return Optional.of(new NeverContainsFieldWrapper(project));
    }


}
