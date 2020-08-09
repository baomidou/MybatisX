package com.baomidou.plugin.idea.mybatisx.smartjpa.operate;


import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.completion.parameter.MxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager.AreaOperateManager;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager.StatementBlock;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager.StatementBlockFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.ui.MapperTagInfo;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseOperatorManager implements AreaOperateManager {

    List<String> operatorNameList = new ArrayList<>();
    private final StatementBlockFactory syntaxAppenderFactoryManager = new StatementBlockFactory();

    protected List<String> getOperatorNameList() {
        return operatorNameList;
    }

    protected void setOperatorNameList(final String nameList) {
        operatorNameList = Arrays.asList(nameList.split(","));
    }

    @NotNull
    @Override
    public LinkedList<SyntaxAppender> splitAppenderByText(final String splitParam) {
        return this.syntaxAppenderFactoryManager.splitAppenderByText(splitParam);
    }

    protected boolean canExecute(final String areaName) {
        return this.operatorNameList.contains(areaName);
    }

    @Override
    public List<String> getCompletionContent(final LinkedList<SyntaxAppender> jpaList) {
        SyntaxAppender firstAreaAppender = jpaList.peek();
        if (firstAreaAppender != null && !this.canExecute(firstAreaAppender.getText())) {
            return Collections.emptyList();
        }
        List<SyntaxAppenderFactory> areaListByJpa = syntaxAppenderFactoryManager.findAreaListByJpa(jpaList);
        return areaListByJpa.stream().flatMap(x -> x.getCompletionContent(jpaList).stream()).collect(Collectors.toList());
    }

    @Override
    public List<String> getCompletionContent() {
        Collection<StatementBlock> allBlock = syntaxAppenderFactoryManager.getAllBlock();
        return allBlock.stream().map(x->x.getResultAppenderFactory())
            .filter(Objects::nonNull)
            .flatMap(x->x.getCompletionContent(new ArrayList<>()).stream())
            .collect(Collectors.toList());
    }

    /**
     * 获取参数列表
     *
     * @param entityClass
     * @return
     */
    @Override
    public List<MxParameter> getParameters(PsiClass entityClass,
                                           LinkedList<SyntaxAppender> jpaList) {
        SyntaxAppender firstAreaAppender = jpaList.peek();
        if (firstAreaAppender != null && !this.canExecute(firstAreaAppender.getText())) {
            return Collections.emptyList();
        }

        List<SyntaxAppenderFactory> areaListByJpa = syntaxAppenderFactoryManager.findAreaListByJpa(jpaList);
        return areaListByJpa.stream().flatMap(x -> x.getMxParameter(entityClass, jpaList).stream()).collect(Collectors.toList());
    }

    @Override
    public boolean support(String text) {
        return operatorNameList.contains(text);
    }


    private static final Logger logger = LoggerFactory.getLogger(BaseOperatorManager.class);


    public void registerStatementBlock(StatementBlock statementBlock) {
        this.syntaxAppenderFactoryManager.registerStatementBlock(statementBlock);
    }

    protected abstract String getTagName();

    @Override
    public MapperTagInfo generateMapperXml(LinkedList<SyntaxAppender> jpaList,
                                           PsiClass entityClass,
                                           PsiMethod psiMethod,
                                           String tableNameByEntityName) {
        SyntaxAppender firstAreaAppender = jpaList.peek();
        if (firstAreaAppender != null && !this.canExecute(firstAreaAppender.getText())) {
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();
        List<SyntaxAppenderFactory> areaListByJpa = syntaxAppenderFactoryManager.findAreaListByJpa(jpaList);
        for (SyntaxAppenderFactory syntaxAppenderFactory : areaListByJpa) {
            LinkedList<PsiParameter> parameters = Arrays.stream(psiMethod.getParameterList().getParameters())
                .collect(Collectors.toCollection(LinkedList::new));
            // 区域生成的xml内容
            String factoryTemplateText = syntaxAppenderFactory.getFactoryTemplateText(jpaList,
                entityClass,
                parameters,
                tableNameByEntityName);
            stringBuilder.append(factoryTemplateText);
        }


        MapperTagInfo mapperTagProcessor = new MapperTagInfo();

        mapperTagProcessor.setMapperXml(stringBuilder.toString());
        mapperTagProcessor.setTagType(getTagName());
        return mapperTagProcessor;

    }


}
