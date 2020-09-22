package com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 实体映射工厂
 * 支持多种获取实体的方式
 * 默认按照 mybatis-plus3 > mybatis-plus2 > xml(resultMap 最短的标签)
 */
public class EntityMappingResolverFactory {
    /**
     * The Project.
     */
    Project project;


    /**
     * The Mapper class.
     */
    PsiClass mapperClass;

    /**
     * The Entity mapping resolver list.
     */
    List<EntityMappingResolver> entityMappingResolverList = new ArrayList<>();

    /**
     * Instantiates a new Entity mapping resolver factory.
     *
     * @param project     the project
     * @param mapperClass the mapper class
     */
    public EntityMappingResolverFactory(Project project, PsiClass mapperClass) {
        this.project = project;
        this.mapperClass = mapperClass;

        entityMappingResolverList.add(new MybatisPlus3MappingResolver());
        entityMappingResolverList.add(new MybatisPlus2MappingResolver());
        entityMappingResolverList.add(new ResultMapMappingResolver());
        // 自定义mapper的泛型加入了实体类, 实体类必须有@Table注解
        entityMappingResolverList.add(new JpaAnnotationMappingResolver());
        // mapper 类的注释, 可能有点卡顿?
        entityMappingResolverList.add(new CommentAnnotationMappingResolver());

    }

    private EntityMappingResolver currentEntityMappingResolver;

    /**
     * Search entity psi class.
     *
     * @return the psi class
     */
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


    /**
     * Gets entity mapping resolver.
     *
     * @return the entity mapping resolver
     */
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
