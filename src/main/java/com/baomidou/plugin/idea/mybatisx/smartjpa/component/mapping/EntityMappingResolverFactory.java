package com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping;

import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.util.CollectionUtils;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 实体映射工厂
 * 支持多种获取实体的方式
 * 默认按照 mybatis-plus3 > mybatis-plus2 > xml(resultMap 最短的标签)
 */
public class EntityMappingResolverFactory {
    private final ResultMapMappingResolver defaultMappingResolver;
    /**
     * The Project.
     */
    Project project;


    /**
     * The Entity mapping resolver list.
     */
    List<EntityMappingResolver> entityMappingResolverList = new ArrayList<>();

    /**
     * Instantiates a new Entity mapping resolver factory.
     *
     * @param project the project
     */
    public EntityMappingResolverFactory(Project project) {
        this.project = project;

        entityMappingResolverList.add(new JpaAnnotationMappingResolver());
        entityMappingResolverList.add(new MybatisPlus3MappingResolver());
        entityMappingResolverList.add(new MybatisPlus2MappingResolver());
        this.defaultMappingResolver = new ResultMapMappingResolver(project);
        entityMappingResolverList.add(defaultMappingResolver);
        // 自定义mapper的泛型加入了实体类, 实体类必须有@Table注解
        // mapper 类的注释, 可能有点卡顿?
        entityMappingResolverList.add(new CommentAnnotationMappingResolver());

    }


    /**
     * Search entity psi class.
     *
     * @return the psi class
     */
    public EntityMappingHolder searchEntity(PsiClass mapperClass) {
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

            // 字段以 resultMap 标签的配置为准
            Map<String, TxField> resultMapMapping = defaultMappingResolver.findFields(mapperClass, entityClass).stream()
                .collect(Collectors.toMap(TxField::getFieldName, v -> v, (l, r) -> l));
            // 处理字段上面没有注解的情况,
            for (TxField field : entityMappingHolder.getFields()) {
                TxField defaultField = resultMapMapping.get(field.getFieldName());
                // 处理列名
                String columnName = null;
                // 处理列名, 强制以 resultMap 为准
                if (defaultField != null) {
                    columnName = defaultField.getColumnName();
                }
                // 原先映射的列名
                if (columnName == null) {
                    columnName = field.getColumnName();
                }
                // 如果没有映射, 默认按照下划线映射
                if (columnName == null) {
                    columnName = StringUtils.camelToSlash(field.getFieldName());
                }
                field.setColumnName(columnName);
                // 处理jdbcType, 强制以 resultMap 为准
                if (defaultField != null && defaultField.getJdbcType() != null) {
                    field.setJdbcType(defaultField.getJdbcType());
                }


            }
        }
        return entityMappingHolder;
    }


    public String findTableName(PsiClass entityClass) {
        String tableName = null;
        for (EntityMappingResolver entityMappingResolver : entityMappingResolverList) {
            Optional<String> tableNameOptional = entityMappingResolver.findTableName(entityClass);
            if (tableNameOptional.isPresent()) {
                tableName = tableNameOptional.get();
            }
        }
        if (tableName == null) {
            tableName = getUnderLineFromEntityClassName(entityClass.getName());
        }
        return tableName;
    }


    @NotNull
    private String getUnderLineFromEntityClassName(String camelName) {
        String[] strings = org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase(camelName);
        return Arrays.stream(strings).map(x -> com.baomidou.plugin.idea.mybatisx.util.StringUtils.lowerCaseFirstChar(x))
            .collect(Collectors.joining("_"));
    }

}
