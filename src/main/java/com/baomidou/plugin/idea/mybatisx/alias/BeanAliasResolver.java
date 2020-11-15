package com.baomidou.plugin.idea.mybatisx.alias;

import com.baomidou.plugin.idea.mybatisx.util.JavaUtils;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.google.common.collect.Sets;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.spring.CommonSpringModel;
import com.intellij.spring.model.SpringBeanPointer;
import com.intellij.spring.model.utils.SpringModelUtils;
import com.intellij.spring.model.utils.SpringPropertyUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * The type Bean alias resolver.
 *
 * @author yanglin
 */
public class BeanAliasResolver extends PackageAliasResolver {

    private static final List<String> MAPPER_ALIAS_PACKAGE_CLASSES = new ArrayList<String>() {
        {
            // default
            add("org.mybatis.spring.SqlSessionFactoryBean");
            // mybatis-plus3
            add("com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean");
            // mybatis-plus2
            add("com.baomidou.mybatisplus.spring.MybatisSqlSessionFactoryBean");
        }
    };

    private static final String MAPPER_ALIAS_PROPERTY = "typeAliasesPackage";

    /**
     * Instantiates a new Bean alias resolver.
     *
     * @param project the project
     */
    public BeanAliasResolver(Project project) {
        super(project);
    }

    @NotNull
    @Override
    public Collection<String> getPackages(@Nullable PsiElement element) {
        CommonSpringModel springModel = SpringModelUtils.getInstance().getSpringModel(element);

        Set<PsiClass> classes = findSqlSessionFactories(MAPPER_ALIAS_PACKAGE_CLASSES);

        return determinePackages(classes, springModel.getAllCommonBeans());
    }

    private Set<PsiClass> findSqlSessionFactories(List<String> mapperAliasPackageClasses) {
        Set<PsiClass> sqlSessionFactorySet = new HashSet<>();
        for (String mapperAliasPackageClass : mapperAliasPackageClasses) {
            Optional<PsiClass> clazz = JavaUtils.findClazz(project, mapperAliasPackageClass);
            clazz.ifPresent(sqlSessionFactorySet::add);
        }
        return sqlSessionFactorySet;
    }

    private Set<String> determinePackages(Set<PsiClass> sqlSessionFactoryClasses, @NotNull Collection<SpringBeanPointer> allCommonBeans) {
        Set<String> res = Sets.newHashSet();
        for (SpringBeanPointer pointer : allCommonBeans) {
            PsiClass beanClass = pointer.getBeanClass();
            if (beanClass != null && sqlSessionFactoryClasses.contains(beanClass)) {
                String propertyStringValue = SpringPropertyUtils.getPropertyStringValue(pointer.getSpringBean(), MAPPER_ALIAS_PROPERTY);
                if (!StringUtils.isEmpty(propertyStringValue)) {
                    res.add(propertyStringValue);
                }
            }
        }
        return res;
    }

}
