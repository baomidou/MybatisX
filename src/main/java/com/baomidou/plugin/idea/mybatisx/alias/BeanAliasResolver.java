package com.baomidou.plugin.idea.mybatisx.alias;

import com.baomidou.plugin.idea.mybatisx.util.JavaUtils;
import com.google.common.collect.Sets;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.spring.CommonSpringModel;
import com.intellij.spring.SpringManager;
import com.intellij.spring.model.SpringBeanPointer;
import com.intellij.spring.model.utils.SpringPropertyUtils;
import com.intellij.spring.model.xml.beans.SpringPropertyDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * @author yanglin
 */
public class BeanAliasResolver extends PackageAliasResolver {

    private static final String MAPPER_ALIAS_PACKAGE_CLASS = "org.mybatis.spring.SqlSessionFactoryBean";
    private static final String MAPPER_ALIAS_PROPERTY = "typeAliasesPackage";
    private ModuleManager moduleManager;
    private SpringManager springManager;

    public BeanAliasResolver(Project project) {
        super(project);
        this.moduleManager = ModuleManager.getInstance(project);
        this.springManager = SpringManager.getInstance(project);
    }

    @NotNull
    @Override
    public Collection<String> getPackages(@Nullable PsiElement element) {
        Set<String> res = Sets.newHashSet();
        for (Module module : moduleManager.getModules()) {
            // TODO old springManager.getCombinedModel(module).getModelsToProcess
            for (CommonSpringModel springModel : springManager.getCombinedModel(module).getRelatedModels()) {
                addPackages(res, springModel);
            }
        }
        return res;
    }

    private void addPackages(Set<String> res, CommonSpringModel springModel) {
        //TODO 这里要适配MP的话就改动一下。
        Optional<PsiClass> sqlSessionFactoryClazzOpt = JavaUtils.findClazz(project, MAPPER_ALIAS_PACKAGE_CLASS);
        if (sqlSessionFactoryClazzOpt.isPresent()) {
            //TODO old:  Collection domBeans = springModel.getAllCommonBeans();
            Collection<SpringBeanPointer> domBeans = springModel.getAllCommonBeans();
            PsiClass sqlSessionFactoryClazz = sqlSessionFactoryClazzOpt.get();

            for (SpringBeanPointer pointer : domBeans) {
                PsiClass beanClass = pointer.getBeanClass();
                if (beanClass != null && beanClass.equals(sqlSessionFactoryClazz)) {
                    SpringPropertyDefinition basePackages = SpringPropertyUtils.findPropertyByName(pointer.getSpringBean(), MAPPER_ALIAS_PROPERTY);
                    if (basePackages != null) {
                        final String value = basePackages.getValueElement().getStringValue();
                        if (value != null) {
                            res.add(value);
                        }
                    }
                }
            }

        }

    }

}
