package com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping;


import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.StringUtils;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiNameValuePair;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TableMappingResolver implements EntityMappingResolver {

    public static final String TABLE_FIELD = "com.baomidou.mybatisplus.annotations.TableField";
    public static final String TABLE_NAME = "com.baomidou.mybatisplus.annotation.TableName";
    public static final String VALUE = "value";
    /**
     * 表名
     */
    private String tableName;


    /**
     * 字段名列表
     */
    private List<TxField> txFields;

    public TableMappingResolver(PsiClass psiClass) {
        tableName = determineTableName(psiClass.getAnnotation(TABLE_NAME), psiClass.getName());
        txFields = determineFields(psiClass);
    }

    @NotNull
    private List<TxField> determineFields(PsiClass psiClass) {
        return Arrays.stream(psiClass.getAllFields())
            .map(field -> {
                TxField txField = new TxField();
                txField.setTipName(StringUtils.upperCaseFirstChar(field.getName()));
                txField.setFieldType(field.getType().getCanonicalText());

                PsiAnnotation annotation = field.getAnnotation(TABLE_FIELD);
                String fieldName = findFieldName(field, annotation);
                txField.setFieldName(fieldName);
                txField.setColumnName(field.getName());
                return txField;
            }).collect(Collectors.toList());
    }

    private String determineTableName(PsiAnnotation annotation, String text) {
        if (annotation == null) {
            return text;
        }
        for (JvmAnnotationAttribute attribute : annotation.getAttributes()) {
            if (VALUE.equals(attribute.getAttributeName())) {
                PsiNameValuePair constantAttribute = (PsiNameValuePair) attribute;
                String constantValue = constantAttribute.getLiteralValue();
                // 如果 value 为空, 直接退出, 返回字段的名称
                if (org.apache.commons.lang3.StringUtils.isEmpty(constantValue)) {
                    break;
                }
                return constantValue;
            }
        }
        return text;
    }

    /**
     * 如果有注解 @TableField 就用这个注解获得字段名
     *
     * @param field
     * @param annotation
     * @return
     */
    protected String findFieldName(com.intellij.psi.PsiField field, PsiAnnotation annotation) {
        if (annotation == null) {
            return field.getName();
        }
        List<JvmAnnotationAttribute> attributes = annotation.getAttributes();
        for (JvmAnnotationAttribute attribute : attributes) {
            if ("value".equals(attribute.getAttributeName())) {
                PsiNameValuePair constantAttribute = (PsiNameValuePair) attribute;
                String constantValue = constantAttribute.getLiteralValue();
                // 如果 value 为空, 直接退出, 返回字段的名称
                if (org.apache.commons.lang3.StringUtils.isEmpty(constantValue)) {
                    break;
                }
                return constantValue;
            }
        }
        return field.getName();
    }

    @Override
    public List<TxField> getFields() {
        return txFields;
    }

    @Override
    public String getTableName() {
        return tableName;
    }
}
