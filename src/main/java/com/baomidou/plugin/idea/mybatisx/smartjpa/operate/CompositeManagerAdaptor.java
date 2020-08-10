package com.baomidou.plugin.idea.mybatisx.smartjpa.operate;


import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxReturnDescriptor;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.MybatisXmlGenerator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager.AreaOperateManager;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.model.AppendTypeEnum;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CompositeManagerAdaptor implements AreaOperateManager {
    List<AreaOperateManager> typeManagers = new ArrayList<>();

    public CompositeManagerAdaptor(final List<TxField> mappingField) {
        this.init(mappingField);
    }

    protected void init(final List<TxField> mappingField) {
        this.typeManagers.add(new SelectOperator(mappingField));
        this.typeManagers.add(new InsertOperator(mappingField));
        this.typeManagers.add(new UpdateOperator(mappingField));
        this.typeManagers.add(new DeleteOperator(mappingField));
    }

    @NotNull
    @Override
    public LinkedList<SyntaxAppender> splitAppenderByText(final String splitParam) {
        final LinkedList<SyntaxAppender> results = new LinkedList<>();
        for (final AreaOperateManager typeManager : this.typeManagers) {
            results.addAll(typeManager.splitAppenderByText(splitParam));
        }
        return results;
    }


    @Override
    public List<String> getCompletionContent(final LinkedList<SyntaxAppender> splitList) {
        final List<String> results = new ArrayList<>();
        for (final AreaOperateManager typeManager : this.typeManagers) {
            if (splitList.size() > 0) {
                results.addAll(typeManager.getCompletionContent(splitList));
            }
        }
        return results;
    }

    @Override
    public List<String> getCompletionContent() {
        final List<String> results = new ArrayList<>();
        for (final AreaOperateManager typeManager : this.typeManagers) {
            results.addAll(typeManager.getCompletionContent());
        }
        return results;
}

    @Override
    public List<TxParameter> getParameters(PsiClass entityClass,
                                           LinkedList<SyntaxAppender> jpaStringList) {
        if (jpaStringList.size() == 0 || jpaStringList.get(0).getType() != AppendTypeEnum.AREA) {
            return Collections.emptyList();
        }
        SyntaxAppender syntaxAppender = jpaStringList.peek();

        final List<TxParameter> results = new ArrayList<>();
        for (final AreaOperateManager typeManager : this.typeManagers) {
            if (typeManager.support(syntaxAppender.getText())) {
                results.addAll(typeManager.getParameters(entityClass, jpaStringList));
            }
        }
        return results;
    }

    @Override
    public TxReturnDescriptor getReturnWrapper(String text, PsiClass entityClass, @NotNull LinkedList<SyntaxAppender> linkedList) {
        if (linkedList.size() == 0 || linkedList.get(0).getType() != AppendTypeEnum.AREA) {
            return null;
        }
        SyntaxAppender syntaxAppender = linkedList.peek();

        for (AreaOperateManager typeManager : this.typeManagers) {
            if (typeManager.support(syntaxAppender.getText())) {
                return typeManager.getReturnWrapper(text, entityClass, linkedList);
            }
        }
        return null;
    }

    @Override
    public boolean support(String operatorText) {
        return true;
    }

    @Override
    public void generateMapperXml(String id, LinkedList<SyntaxAppender> jpaList, PsiClass entityClass, PsiMethod psiMethod, String tableNameByEntityName, MybatisXmlGenerator mybatisXmlGenerator) {
        if (jpaList.size() == 0 || jpaList.get(0).getType() != AppendTypeEnum.AREA) {
            return;
        }
        SyntaxAppender syntaxAppender = jpaList.peek();

        for (AreaOperateManager typeManager : this.typeManagers) {
            if (typeManager.support(syntaxAppender.getText())) {
                typeManager.generateMapperXml(id, jpaList, entityClass, psiMethod, tableNameByEntityName, mybatisXmlGenerator);
                return;
            }
        }
        return;
    }


}
