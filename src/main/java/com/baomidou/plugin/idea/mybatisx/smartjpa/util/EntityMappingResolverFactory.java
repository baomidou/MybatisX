package com.baomidou.plugin.idea.mybatisx.smartjpa.util;

import com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping.EntityMappingResolver;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping.MybatisPlus2MappingResolver;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping.MybatisPlus3MappingResolver;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping.ResultMapMappingResolver;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 实体映射工厂
 * 支持多种获取实体的方式
 * 默认按照 mybatis-plus3 > mybatis-plus2 > xml(resultMap 最短的标签)
 *
 */
public class EntityMappingResolverFactory {
    Project project;


    PsiClass mapperClass;

    List<EntityMappingResolver> entityMappingResolverList = new ArrayList<>();

    public EntityMappingResolverFactory(Project project, PsiClass mapperClass) {
        this.project = project;
        this.mapperClass = mapperClass;

        entityMappingResolverList.add(new MybatisPlus3MappingResolver());
        entityMappingResolverList.add(new MybatisPlus2MappingResolver());
        entityMappingResolverList.add(new ResultMapMappingResolver());
    }

    private EntityMappingResolver currentEntityMappingResolver;

    public PsiClass searchEntity() {
        for (EntityMappingResolver entityMappingResolver : entityMappingResolverList) {
            Optional<PsiClass> entity = entityMappingResolver.findEntity(mapperClass);
            if (entity.isPresent()) {
                currentEntityMappingResolver = entityMappingResolver;
                return entity.get();
            }
        }
        return null;
    }


    public EntityMappingResolver getEntityMappingResolver() {
        if (currentEntityMappingResolver != null) {
            return currentEntityMappingResolver;
        }
        for (EntityMappingResolver entityMappingResolver : entityMappingResolverList) {
            if (entityMappingResolver.findEntity(mapperClass).isPresent()) {
                return entityMappingResolver;
            }
        }
        return null;
    }
}
