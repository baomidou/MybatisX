package com.baomidou.plugin.idea.mybatisx.tip.hashmark;

import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.util.JavaUtils;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.util.Query;

import java.util.Collection;
import java.util.Optional;

/**
 * 类型处理器的提示
 *
 * @author ls9527
 */
public class TypeHandlerHashMarkTip implements HashMarkTip {

    private static final String ORG_APACHE_IBATIS_TYPE_TYPE_HANDLER = "org.apache.ibatis.type.TypeHandler";

    @Override
    public String getName() {
        return "typeHandler";
    }

    /**
     * 通常希望提示自定义的类型处理器
     *
     * @param completionResultSet
     * @param mapper
     */
    @Override
    public void tipValue(CompletionResultSet completionResultSet, Mapper mapper) {
        Project project = mapper.getXmlTag().getProject();
        Optional<PsiClass> typeHandlerOptional = JavaUtils.findClazz(project, ORG_APACHE_IBATIS_TYPE_TYPE_HANDLER);
        if (typeHandlerOptional.isPresent()) {
            PsiClass typeHandler = typeHandlerOptional.get();
            Query<PsiClass> search = ClassInheritorsSearch.search(typeHandler, true);
            Collection<PsiClass> all = search.findAll();
            for (PsiClass typeHandlerImplClass : all) {
                // 抽象类型不提示
                if (typeHandlerImplClass.hasModifierProperty(PsiModifier.ABSTRACT)) {
                    continue;
                }
                completionResultSet.addElement(LookupElementBuilder.create(typeHandlerImplClass.getQualifiedName()));
            }
        }
    }
}
