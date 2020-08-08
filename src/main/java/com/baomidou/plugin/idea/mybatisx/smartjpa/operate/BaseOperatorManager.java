package com.baomidou.plugin.idea.mybatisx.smartjpa.operate;


import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.completion.parameter.MxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager.AreaOperateManager;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager.SyntaxAppenderFactoryManager;
import com.baomidou.plugin.idea.mybatisx.smartjpa.ui.MapperTagInfo;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class BaseOperatorManager implements AreaOperateManager {

    List<String> operatorNameList = new ArrayList<>();
    private final SyntaxAppenderFactoryManager syntaxAppenderFactoryManager = new SyntaxAppenderFactoryManager();

    protected List<String> getOperatorNameList() {
        return operatorNameList;
    }

    protected void setOperatorNameList(final String nameList) {
        operatorNameList = Arrays.asList(nameList.split(","));
    }

    @NotNull
    @Override
    public LinkedList<SyntaxAppender> getJpaList(final String splitParam) {
        return this.syntaxAppenderFactoryManager.getJpaStringList(splitParam);
    }

    protected boolean canExecute(final String areaName) {
        return this.operatorNameList.contains(areaName);
    }

    @Override
    public Set<String> getCompletionContent(final LinkedList<SyntaxAppender> splitList) {
        if (splitList.size() > 0 && !this.canExecute(splitList.peek().getText())) {
            return Collections.emptySet();
        }
        return this.syntaxAppenderFactoryManager.getCompletionContent(splitList);
    }

    /**
     * 获取参数列表
     *
     * @param entityClass
     * @param jpaStringList
     * @return
     */
    @Override
    public List<MxParameter> getParameters(PsiClass entityClass,
                                           LinkedList<SyntaxAppender> jpaStringList) {
        return syntaxAppenderFactoryManager.getMxParameter(entityClass, jpaStringList);
    }

    @Override
    public boolean support(String text) {
        return operatorNameList.contains(text);
    }


    private static final Logger logger = LoggerFactory.getLogger(BaseOperatorManager.class);


    public void registerAppenderFactory(final SyntaxAppenderFactory resultAppenderFactory) {
        this.syntaxAppenderFactoryManager.registerAppenderFactory(resultAppenderFactory);
    }

    protected abstract String getTagType();

    @Override
    public MapperTagInfo generateMapperXml(String text,
                                           PsiClass entityClass,
                                           PsiMethod psiMethod,
                                           String tableNameByEntityName) {


        LinkedList<SyntaxAppender> jpaStringList = getJpaList(text);

        String mapperXml = syntaxAppenderFactoryManager.generateMapperXml(jpaStringList,
            entityClass,
            psiMethod,
            tableNameByEntityName);

        MapperTagInfo mapperTagProcessor = new MapperTagInfo();
        mapperTagProcessor.setId(text);
        mapperTagProcessor.setMapperXml(mapperXml);
        mapperTagProcessor.setTagType(getTagType());
        return mapperTagProcessor;

    }


}
