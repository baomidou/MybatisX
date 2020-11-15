package com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping;


import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiField;
import org.jetbrains.annotations.NotNull;

/**
 * The type Mybatis plus 3 mapping resolver.
 */
public class MybatisPlus3MappingResolver extends AbstractMybatisPlusMappingResolver {

    /**
     * The constant TABLE_FIELD.
     */
    public static final String TABLE_FIELD = "com.baomidou.mybatisplus.annotation.TableField";
    /**
     * The constant TABLE_NAME.
     */
    public static final String TABLE_NAME = "com.baomidou.mybatisplus.annotation.TableName";
    private static final String TABLE_ID = "com.baomidou.mybatisplus.annotation.TableId";
    /**
     * The constant BASE_MAPPER.
     */
    public static final String BASE_MAPPER = "com.baomidou.mybatisplus.core.mapper.BaseMapper";

    /**
     * Instantiates a new Mybatis plus 3 mapping resolver.
     */
    public MybatisPlus3MappingResolver() {
    }

    @Override
    protected @NotNull String getTableNameAnnotation() {
        return TABLE_NAME;
    }

    @Override
    protected @NotNull String getTableFieldAnnotation(@NotNull PsiField field) {
        String columnName = null;
        PsiAnnotation fieldAnnotation = field.getAnnotation(TABLE_FIELD);
        if (fieldAnnotation != null) {
            columnName = getAttributeValue(fieldAnnotation, AbstractMybatisPlusMappingResolver.VALUE);
        }
        PsiAnnotation idAnnotation = field.getAnnotation(TABLE_ID);
        if (columnName == null && idAnnotation != null) {
            columnName = getAttributeValue(idAnnotation, AbstractMybatisPlusMappingResolver.VALUE);
        }
        if (columnName == null) {
            columnName = field.getName();
        }
        return columnName;
    }

    @Override
    protected String getBaseMapperClassName() {
        return BASE_MAPPER;
    }


}
