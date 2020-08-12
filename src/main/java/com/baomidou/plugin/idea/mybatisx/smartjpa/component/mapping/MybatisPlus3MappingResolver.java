package com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping;


import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiField;
import org.jetbrains.annotations.NotNull;

public class MybatisPlus3MappingResolver extends AbstractMybatisPlusMappingResolver {

    public static final String TABLE_FIELD = "com.baomidou.mybatisplus.annotation.TableField";
    public static final String TABLE_NAME = "com.baomidou.mybatisplus.annotation.TableName";
    private static final String TABLE_ID = "com.baomidou.mybatisplus.annotation.TableId";
    public static final String BASE_MAPPER = "com.baomidou.mybatisplus.core.mapper.BaseMapper";

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
            columnName = getAttributeValue(fieldAnnotation, "value");
        }
        PsiAnnotation idAnnotation = field.getAnnotation(TABLE_ID);
        if (columnName == null && idAnnotation != null) {
            columnName = getAttributeValue(idAnnotation, "value");
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
