package com.baomidou.plugin.idea.mybatisx.alias;

import com.baomidou.plugin.idea.mybatisx.util.JavaUtils;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.spring.CommonSpringModel;
import com.intellij.spring.model.SpringModelSearchParameters;
import com.intellij.spring.model.utils.SpringModelUtils;
import com.intellij.spring.model.utils.SpringPropertyUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * The type Bean alias resolver.
 *
 * @author yanglin
 */
@Slf4j
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
        // 兼容低版本获取不到 ServiceManager 报错的场景。 直接忽略
        SpringModelUtils instance = null;
        try {
            instance = SpringModelUtils.getInstance();
        } catch (Throwable e) {
            log.warn("获取SpringManager异常, 无法通过bean定义别名",e);
            return Collections.emptyList();
        }
        Set<String> packages = new HashSet<>();
        Set<PsiClass> classes = findSqlSessionFactories();
        for (PsiClass sqlSessionFactoryClass : classes) {
            CommonSpringModel springModel = instance.getPsiClassSpringModel(sqlSessionFactoryClass);
            SpringModelSearchParameters.BeanClass beanClass = SpringModelSearchParameters.BeanClass.byClass(sqlSessionFactoryClass);
            springModel.processByClass(beanClass, springBeanPointer -> {
                String propertyStringValue = SpringPropertyUtils.getPropertyStringValue(springBeanPointer.getSpringBean(), MAPPER_ALIAS_PROPERTY);
                if (!StringUtils.isEmpty(propertyStringValue)) {
                    packages.add(propertyStringValue);
                    return true;
                }
                return false;
            });
        }
        return packages;
    }

    private Set<PsiClass> findSqlSessionFactories() {
        Set<PsiClass> sqlSessionFactorySet = new HashSet<>();
        for (String mapperAliasPackageClass : BeanAliasResolver.MAPPER_ALIAS_PACKAGE_CLASSES) {
            Optional<PsiClass> clazz = JavaUtils.findClazz(project, mapperAliasPackageClass);
            clazz.ifPresent(sqlSessionFactorySet::add);
        }
        return sqlSessionFactorySet;
    }

}
