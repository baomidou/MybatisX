package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.dialect;


import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest.ConditionFieldWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TypeDescriptor;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.CountOperator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.DeleteOperator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.InsertOperator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.SelectOperator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.UpdateOperator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.Generator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager.AreaOperateManager;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.model.AppendTypeEnum;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 基础方言管理器
 */
public class BaseDialectManager implements AreaOperateManager {
    private List<AreaOperateManager> typeManagers = new ArrayList<>();


    /**
     * Init.
     *
     * @param mappingField the mapping field
     * @param entityClass  the entity class
     */
    protected void init(final List<TxField> mappingField, PsiClass entityClass) {
        this.registerManagers(new SelectOperator(mappingField,entityClass));
        this.registerManagers(new CountOperator(mappingField, entityClass));
        this.registerManagers(new InsertOperator(mappingField));
        this.registerManagers(new UpdateOperator(mappingField,entityClass));
        this.registerManagers(new DeleteOperator(mappingField));
    }


    /**
     * Register managers.
     *
     * @param areaOperateManager the area operate manager
     */
    protected void registerManagers(AreaOperateManager areaOperateManager){
        this.typeManagers.add(areaOperateManager);
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
    public TypeDescriptor getReturnWrapper(String text, PsiClass entityClass, @NotNull LinkedList<SyntaxAppender> linkedList) {
        if (linkedList.size() == 0 || linkedList.get(0).getType() != AppendTypeEnum.AREA) {
            return null;
        }
        SyntaxAppender syntaxAppender = linkedList.peek();

        for (AreaOperateManager typeManager : this.typeManagers) {
            if (typeManager.support(syntaxAppender.getText())) {
                return typeManager.getReturnWrapper(syntaxAppender.getText(), entityClass, linkedList);
            }
        }
        return null;
    }

    @Override
    public boolean support(String operatorText) {
        return true;
    }

    @Override
    public void generateMapperXml(String id,
                                  LinkedList<SyntaxAppender> jpaList,
                                  PsiClass entityClass,
                                  PsiMethod psiMethod,
                                  String tableNameByEntityName,
                                  Generator mybatisXmlGenerator,
                                  ConditionFieldWrapper conditionFieldWrapper,
                                  List<TxField> resultFields) {
        if (jpaList.size() == 0 || jpaList.get(0).getType() != AppendTypeEnum.AREA) {
            return;
        }
        SyntaxAppender syntaxAppender = jpaList.peek();

        for (AreaOperateManager typeManager : this.typeManagers) {
            if (typeManager.support(syntaxAppender.getText())) {
                typeManager.generateMapperXml(id,
                    jpaList,
                    entityClass,
                    psiMethod,
                    tableNameByEntityName,
                    mybatisXmlGenerator,
                    conditionFieldWrapper,
                    resultFields);
                break;
            }
        }
    }


}
