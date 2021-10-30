package com.baomidou.plugin.idea.mybatisx.smartjpa;

import com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest.ConditionFieldWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest.NeverContainsFieldWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * 在mapper类中通过名字生成方法和xml内容
 *
 * @author ls9527
 */
public class GenerateSmartJpaAction extends GenerateSmartJpaAdvanceAction {


    private static final Logger logger = LoggerFactory.getLogger(GenerateSmartJpaAction.class);

    @NotNull
    @Override
    public String getText() {
        return "[MybatisX] Generate Mybatis Sql";
    }


    @Override
    protected Optional<ConditionFieldWrapper> getConditionFieldWrapper(@NotNull Project project,
                                                                       String defaultDateWord,
                                                                       List<TxField> allFields,
                                                                       List<String> resultFields,
                                                                       List<String> conditionFields,
                                                                       PsiClass entityClass, boolean isSelect) {
        return Optional.of(new NeverContainsFieldWrapper(project,allFields));
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
