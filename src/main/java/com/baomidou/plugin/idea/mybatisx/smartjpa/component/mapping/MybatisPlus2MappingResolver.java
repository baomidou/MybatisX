package com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping;


import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiField;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * The type Mybatis plus 2 mapping resolver.
 */
public class MybatisPlus2MappingResolver extends AbstractMybatisPlusMappingResolver {

    /**
     * The constant TABLE_FIELD.
     */
    public static final String TABLE_FIELD = "com.baomidou.mybatisplus.annotations.TableField";
    /**
     * The constant TABLE_NAME.
     */
    public static final String TABLE_NAME = "com.baomidou.mybatisplus.annotations.TableName";
    /**
     * The constant TABLE_ID.
     */
    public static final String TABLE_ID = "com.baomidou.mybatisplus.annotations.TableId";
    /**
     * The constant BASE_MAPPER.
     */
    public static final String BASE_MAPPER = "com.baomidou.mybatisplus.mapper.BaseMapper";

    /**
     * Instantiates a new Mybatis plus 2 mapping resolver.
     */
    public MybatisPlus2MappingResolver() {
    }

    @Override
    protected @NotNull String getTableNameAnnotation() {
        return TABLE_NAME;
    }

    @Override
    protected @NotNull String getTableFieldAnnotation(@NotNull PsiField field) {
        String columnName = null;
        // 获取 mp 的 TableField 注解
        PsiAnnotation fieldAnnotation = field.getAnnotation(TABLE_FIELD);
        if (fieldAnnotation != null) {
            columnName = getAttributeValue(fieldAnnotation, AbstractMybatisPlusMappingResolver.VALUE);
        }
        //  获取 mp 的 id 注解
        if (StringUtils.isBlank(columnName)) {
            PsiAnnotation idAnnotation = field.getAnnotation(TABLE_ID);
            if (idAnnotation != null) {
                columnName = getAttributeValue(idAnnotation, AbstractMybatisPlusMappingResolver.VALUE);
            }
        }
        // 获取 jpa 注解
        if (StringUtils.isBlank(columnName)) {
            columnName = getColumnNameByJpaOrCamel(field);
        }
        return columnName;
    }


    @Override
    protected String getBaseMapperClassName() {
        return BASE_MAPPER;
    }

}
