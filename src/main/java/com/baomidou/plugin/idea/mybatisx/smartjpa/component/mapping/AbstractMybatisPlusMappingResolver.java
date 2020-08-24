package com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping;


import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.StringUtils;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiReferenceList;
import com.intellij.psi.PsiType;
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
        tableName = determineTableName(entityClass, "value");
        txFields = determineFields(entityClass);
    }


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

    private String determineTableName(PsiClass psiClass, String tableNameValue) {
        PsiAnnotation annotation = psiClass.getAnnotation(getTableNameAnnotation());
        if (annotation != null) {
            PsiAnnotationMemberValue attributeValue = annotation.findAttributeValue(tableNameValue);
            if(attributeValue!=null){
                return attributeValue.getText();
            }
        }
        String tableName = null;
        // 获取 mp 的注解
        tableName = getAttributeValue(annotation, VALUE);
        // 获取 jpa 注解
        if (tableName == null) {
            tableName = getTableNameByJpaOrCamel(psiClass);
        }
        return tableName;
    }

    protected String getAttributeValue(PsiAnnotation fieldAnnotation, String value) {
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
        if (referenceElements.length != 1) {
            return Optional.empty();
        }
        for (PsiJavaCodeReferenceElement referenceElement : referenceElements) {
            String qualifiedName = referenceElement.getQualifiedName();
            if (getBaseMapperClassName().equals(qualifiedName)) {

                PsiType typeParameter = referenceElement.getTypeParameters()[0];

                PsiClass entityClass = JavaPsiFacade.getInstance(mapperClass.getProject())
                    .findClass(typeParameter.getCanonicalText(), mapperClass.getResolveScope());

                initDatas(entityClass);

                return Optional.of(entityClass);
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
