package com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping;


import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractMybatisPlusMappingResolver extends JpaMappingResolver implements EntityMappingResolver {

    public static final String VALUE = "value";
    /**
     * 表名
     */
    private String tableName;


    /**
     * 字段名列表
     */
    private List<TxField> txFields;


    protected void initDatas(PsiClass entityClass) {
        tableName = determineTableName(entityClass);
        txFields = determineFields(entityClass);
    }

    /**
     * 获得表名注解
     *
     * @return
     */
    @NotNull
    protected abstract String getTableNameAnnotation();

    @NotNull
    private List<TxField> determineFields(PsiClass psiClass) {
        return Arrays.stream(psiClass.getAllFields())
            .map(field -> {
                TxField txField = new TxField();
                txField.setTipName(StringUtils.upperCaseFirstChar(field.getName()));
                txField.setFieldType(field.getType().getCanonicalText());

                String columnName = getTableFieldAnnotation(field);
                // 实体的字段名称
                txField.setFieldName(field.getName());
                // 表的列名
                txField.setColumnName(columnName);
                return txField;
            }).collect(Collectors.toList());
    }

    @NotNull
    protected abstract String getTableFieldAnnotation(@NotNull PsiField field);

    private String determineTableName(PsiClass psiClass) {
        PsiAnnotation annotation = psiClass.getAnnotation(getTableNameAnnotation());
        // 获取 mp 的注解
        String tableName = getAttributeValue(annotation, VALUE);
        // 获取 jpa 注解
        if (tableName == null) {
            tableName = getTableNameByJpaOrCamel(psiClass);
        }
        return tableName;
    }

    protected String getAttributeValue(PsiAnnotation fieldAnnotation, String value) {
        if (fieldAnnotation == null) {
            return null;
        }
        PsiAnnotationMemberValue attributeValue = fieldAnnotation.findAttributeValue(value);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(attributeValue.getText())) {
            PsiLiteralExpression psiLiteralExpression = (PsiLiteralExpression) attributeValue;
            return psiLiteralExpression.getValue().toString();
        }
        return null;
    }

    @Override
    public Optional<PsiClass> findEntity(PsiClass mapperClass) {
        PsiReferenceList extendsList = mapperClass.getExtendsList();
        PsiJavaCodeReferenceElement[] referenceElements = extendsList.getReferenceElements();
        if (referenceElements.length == 0) {
            return Optional.empty();
        }
        for (PsiJavaCodeReferenceElement referenceElement : referenceElements) {
            String qualifiedName = referenceElement.getQualifiedName();

            if (getBaseMapperClassName().equals(qualifiedName)) {

                PsiType typeParameter = referenceElement.getTypeParameters()[0];

                PsiClass entityClass = JavaPsiFacade.getInstance(mapperClass.getProject())
                    .findClass(typeParameter.getCanonicalText(), mapperClass.getResolveScope());

                initDatas(entityClass);

                return Optional.ofNullable(entityClass);
            } else {
                // 递归查找，  通过父类找到entity
                PsiElement resolve = referenceElement.resolve();
                if (resolve instanceof PsiClass) {
                    PsiClass extClass = (PsiClass) resolve;
                    return findEntity(extClass);
                }
            }
        }
        return Optional.empty();
    }

    protected abstract String getBaseMapperClassName();

    @Override
    public List<TxField> getFields() {
        return txFields;
    }

    @Override
    public String getTableName() {
        return tableName;
    }
}
