package com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping;

import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.util.CollectionUtils;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        entityMappingResolverList.add(new JpaAnnotationMappingResolver());
        entityMappingResolverList.add(new MybatisPlus3MappingResolver());
        entityMappingResolverList.add(new MybatisPlus2MappingResolver());
        entityMappingResolverList.add(new ResultMapMappingResolver(project));
        // 自定义mapper的泛型加入了实体类, 实体类必须有@Table注解
        // mapper 类的注释, 可能有点卡顿?
        entityMappingResolverList.add(new CommentAnnotationMappingResolver());

    }


    /**
     * Search entity psi class.
     *
     * @return the psi class
     */
    public EntityMappingHolder searchEntity() {
        EntityMappingHolder entityMappingHolder = new EntityMappingHolder();
        for (EntityMappingResolver entityMappingResolver : entityMappingResolverList) {
            Optional<PsiClass> entity = entityMappingResolver.findEntity(mapperClass);
            entity.ifPresent(entityMappingHolder::setEntityClass);
        }
        PsiClass entityClass = entityMappingHolder.getEntityClass();
        if (entityClass != null) {
            String tableName = null;
            for (EntityMappingResolver entityMappingResolver : entityMappingResolverList) {
                Optional<String> tableNameOptional = entityMappingResolver.findTableName(entityClass);
                if (tableNameOptional.isPresent()) {
                    tableName = tableNameOptional.get();
                    entityMappingHolder.setFields(entityMappingResolver.findFields(mapperClass, entityClass));
                    break;
                }
            }
            // default tableName and fields
            if (tableName == null) {
                tableName = getUnderLineFromEntityClassName(entityClass.getName());
                for (EntityMappingResolver entityMappingResolver : entityMappingResolverList) {
                    List<TxField> fields = entityMappingResolver.findFields(mapperClass, entityClass);
                    if (CollectionUtils.isNotEmpty(fields)) {
                        entityMappingHolder.setFields(fields);
                    }
                }
            }
            entityMappingHolder.setTableName(tableName);

        }
        return entityMappingHolder;
    }

    @NotNull
    private String getUnderLineFromEntityClassName(String camelName) {
        String[] strings = org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase(camelName);
        return Arrays.stream(strings).map(x -> com.baomidou.plugin.idea.mybatisx.util.StringUtils.lowerCaseFirstChar(x))
            .collect(Collectors.joining("_"));
    }

}
