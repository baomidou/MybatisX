package com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping;


import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    /**
     * The constant BASE_MAPPER.
     */
    public static final String BASE_MAPPER = "com.baomidou.mybatisplus.core.mapper.BaseMapper";
    private static final String TABLE_ID = "com.baomidou.mybatisplus.annotation.TableId";

    /**
     * Instantiates a new Mybatis plus 3 mapping resolver.
     */
    public MybatisPlus3MappingResolver() {
    }

    @Override
    protected @NotNull
    String getTableNameAnnotation() {
        return TABLE_NAME;
    }


    @Override
    protected @Nullable String getTableFieldAnnotation(@NotNull PsiField field) {
        String columnName = null;
        PsiAnnotation fieldAnnotation = field.getAnnotation(TABLE_FIELD);
        if (fieldAnnotation != null) {
            columnName = getAttributeValue(fieldAnnotation, AbstractMybatisPlusMappingResolver.VALUE);
        }
        PsiAnnotation idAnnotation = field.getAnnotation(TABLE_ID);
        if (StringUtils.isEmpty(columnName) && idAnnotation != null) {
            columnName = getAttributeValue(idAnnotation, AbstractMybatisPlusMappingResolver.VALUE);
        }
        return columnName;
    }

    @Override
    protected String getBaseMapperClassName() {
        return BASE_MAPPER;
    }


}
