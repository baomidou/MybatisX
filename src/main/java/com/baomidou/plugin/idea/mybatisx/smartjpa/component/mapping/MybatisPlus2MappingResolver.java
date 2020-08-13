package com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping;


import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiField;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class MybatisPlus2MappingResolver extends AbstractMybatisPlusMappingResolver {

    public static final String TABLE_FIELD = "com.baomidou.mybatisplus.annotations.TableField";
    public static final String TABLE_NAME = "com.baomidou.mybatisplus.annotations.TableName";
    public static final String TABLE_ID = "com.baomidou.mybatisplus.annotations.TableId";
    public static final String BASE_MAPPER = "com.baomidou.mybatisplus.mapper.BaseMapper";

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
            columnName = getAttributeValue(fieldAnnotation, "value");
        }
        //  获取 mp 的 id 注解
        if (StringUtils.isBlank(columnName)) {
            PsiAnnotation idAnnotation = field.getAnnotation(TABLE_ID);
            if (idAnnotation != null) {
                columnName = getAttributeValue(idAnnotation, "value");
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
