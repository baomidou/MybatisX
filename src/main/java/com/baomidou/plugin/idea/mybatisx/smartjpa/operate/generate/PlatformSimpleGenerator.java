package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate;

import com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping.EntityMappingResolver;
import com.baomidou.plugin.idea.mybatisx.smartjpa.db.adaptor.DasTableAdaptor;
import com.baomidou.plugin.idea.mybatisx.smartjpa.db.adaptor.DbmsAdaptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class PlatformSimpleGenerator {


    @NotNull
    public PlatformGenerator getPlatformGenerator(@NotNull Project project, @NotNull PsiElement element, PsiClass entityClass, EntityMappingResolver entityMappingResolver, String text) {
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

    protected DasTableAdaptor findAdaptor(@NotNull Project project, PsiClass entityClass, EntityMappingResolver entityMappingResolver) {
        DasTableAdaptor dasTableAdaptor = new DasTableAdaptor();
        try {
            // 名字可能会找到合适的表名
            getTableName(entityClass, project, entityMappingResolver.getTableName(), dasTableAdaptor);
        } catch (NoClassDefFoundError ignore) {
        }
        return dasTableAdaptor;
    }

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

    protected DbmsAdaptor getDbmsAdaptor(@NotNull Project project, @NotNull PsiElement element) {
      return DbmsAdaptor.MYSQL;
    }


    /**
     * 遍历所有数据源的表名
     *
     * @param entityClass
     * @param dataSources
     * @param foundTableName
     * @return
     */
    protected String getTableName(PsiClass entityClass,
                                  Project project,
                                  String foundTableName,
                                  DasTableAdaptor dasTableAdaptor) {
        return foundTableName;
    }


}
