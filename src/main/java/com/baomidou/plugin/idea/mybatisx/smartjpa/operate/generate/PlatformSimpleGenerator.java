package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate;

import com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping.EntityMappingResolver;
import com.baomidou.plugin.idea.mybatisx.smartjpa.db.adaptor.DasTableAdaptor;
import com.baomidou.plugin.idea.mybatisx.smartjpa.db.adaptor.DbmsAdaptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * The type Platform simple generator.
 */
public class PlatformSimpleGenerator {


    /**
     * Gets platform generator.
     *
     * @param project               the project
     * @param element               the element
     * @param entityClass           the entity class
     * @param entityMappingResolver the entity mapping resolver
     * @param text                  the text
     * @return the platform generator
     */
    @NotNull
    public PlatformGenerator getPlatformGenerator(@NotNull Project project,
                                                  @NotNull PsiElement element,
                                                  PsiClass entityClass,
                                                  EntityMappingResolver entityMappingResolver,
                                                  String text) {
        DbmsAdaptor dbms = getDbmsAdaptor(project, element);
        // 名字默认是从实体上面解析到的
        DasTableAdaptor dasTableAdaptor = findAdaptor(project, entityClass, entityMappingResolver);
        String tableName = findTableName(project, entityClass, entityMappingResolver);

        return CommonGenerator.createEditorAutoCompletion(entityClass,
            text,
            dbms,
            dasTableAdaptor,
            tableName,
            entityMappingResolver.getFields());
    }

    /**
     * Find adaptor das table adaptor.
     *
     * @param project               the project
     * @param entityClass           the entity class
     * @param entityMappingResolver the entity mapping resolver
     * @return the das table adaptor
     */
    protected DasTableAdaptor findAdaptor(@NotNull Project project, PsiClass entityClass, EntityMappingResolver entityMappingResolver) {
        DasTableAdaptor dasTableAdaptor = new DasTableAdaptor();
        try {
            // 名字可能会找到合适的表名
            getTableName(entityClass, project, entityMappingResolver.getTableName(), dasTableAdaptor);
        } catch (NoClassDefFoundError ignore) {
        }
        return dasTableAdaptor;
    }

    /**
     * Find table name string.
     *
     * @param project               the project
     * @param entityClass           the entity class
     * @param entityMappingResolver the entity mapping resolver
     * @return the string
     */
    protected String findTableName(@NotNull Project project, PsiClass entityClass, EntityMappingResolver entityMappingResolver) {
        String tableName = entityMappingResolver.getTableName();
        try {
            DasTableAdaptor dasTableAdaptor = new DasTableAdaptor();
            // 名字可能会找到合适的表名
            tableName = getTableName(entityClass, project, entityMappingResolver.getTableName(), dasTableAdaptor);
        } catch (NoClassDefFoundError ignore) {
        }
        return tableName;
    }

    /**
     * Gets dbms adaptor.
     *
     * @param project the project
     * @param element the element
     * @return the dbms adaptor
     */
    protected DbmsAdaptor getDbmsAdaptor(@NotNull Project project, @NotNull PsiElement element) {
      return DbmsAdaptor.MYSQL;
    }


    /**
     * 遍历所有数据源的表名
     *
     * @param entityClass     the entity class
     * @param project         the project
     * @param foundTableName  the found table name
     * @param dasTableAdaptor the das table adaptor
     * @return table name
     */
    protected String getTableName(PsiClass entityClass,
                                  Project project,
                                  String foundTableName,
                                  DasTableAdaptor dasTableAdaptor) {
        return foundTableName;
    }


}
