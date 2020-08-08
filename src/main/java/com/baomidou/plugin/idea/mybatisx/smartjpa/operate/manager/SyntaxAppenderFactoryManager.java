package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager;


import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.completion.parameter.MxParameter;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 符号追加器 工厂管理器
 */
public class SyntaxAppenderFactoryManager {

    private final List<SyntaxAppenderFactory> syntaxAppenderFactoryList = new ArrayList<>();

    private SyntaxSplitHelper syntaxSplitHelper = new SyntaxSplitHelper(syntaxAppenderFactoryList);


    public SyntaxAppenderFactoryManager() {
    }


    public Set<String> getCompletionContent(final LinkedList<SyntaxAppender> splitList) {
        final LinkedHashSet<String> result = new LinkedHashSet<>();
        // 第一次填充
        for (int index = 0; index < syntaxAppenderFactoryList.size(); index++) {
            final SyntaxAppenderFactory syntaxAppenderFactory = this.syntaxAppenderFactoryList.get(index);

            final List<String> appendNames = syntaxAppenderFactory.getAppendNames(splitList);
            if (appendNames.size() > 0) {
                result.addAll(appendNames);
            }

        }

        return result;
    }

    @NotNull
    public LinkedList<SyntaxAppender> getJpaStringList(String splitParam) {
        return syntaxSplitHelper.splitAppenderByText(splitParam);
    }

    public void registerAppenderFactory(final SyntaxAppenderFactory resultAppenderFactory) {
        this.syntaxAppenderFactoryList.add(resultAppenderFactory);
    }

    public List<MxParameter> getMxParameter(PsiClass entityClass, LinkedList<SyntaxAppender> jpaStringList) {

        final List<MxParameter> result = new ArrayList<>();
        // 第一次填充
        LinkedList<SyntaxAppender> parameterAppender = new LinkedList<>(jpaStringList);
        for (SyntaxAppenderFactory syntaxAppenderFactory : syntaxAppenderFactoryList) {
            List<MxParameter> mxList = syntaxAppenderFactory.getMxParameter(parameterAppender, entityClass);
            result.addAll(mxList);
        }

        return result;
    }


    public String generateMapperXml(LinkedList<SyntaxAppender> jpaStringList,
                                    PsiClass entityClass,
                                    PsiMethod psiMethod, String tableNameByEntityName) {
        StringBuilder stringBuilder = new StringBuilder();

        PsiParameterList parameterList = psiMethod.getParameterList();
        LinkedList<PsiParameter> parameters = Arrays.stream(parameterList.getParameters())
            .collect(Collectors.toCollection(LinkedList::new));


        // 第一次填充
        for (SyntaxAppenderFactory syntaxAppenderFactory : syntaxAppenderFactoryList) {
            String templateText = syntaxAppenderFactory.getFactoryTemplateText(jpaStringList,
                entityClass,
                parameters,
                tableNameByEntityName);
            stringBuilder.append(templateText);
        }
        return stringBuilder.toString();
    }


    private static final Logger logger = LoggerFactory.getLogger(SyntaxAppenderFactoryManager.class);

}
