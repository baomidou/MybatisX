package com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping;


import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.JdbcTypeUtils;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
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

/**
 * The type Abstract mybatis plus mapping resolver.
 */
public abstract class AbstractMybatisPlusMappingResolver extends JpaMappingResolver implements EntityMappingResolver {

    /**
     * The constant VALUE.
     */
    public static final String VALUE = "value";

    /**
     * 获得表名注解
     *
     * @return table name annotation
     */
    @NotNull
    protected abstract String getTableNameAnnotation();

    @NotNull
    private List<TxField> determineFields(PsiClass psiClass) {
        return Arrays.stream(psiClass.getAllFields())
            .filter(this::filterField)
            .map(field -> {
                TxField txField = new TxField();
                txField.setTipName(StringUtils.upperCaseFirstChar(field.getName()));
                txField.setFieldType(field.getType().getCanonicalText());

                String columnName = getTableFieldAnnotation(field);
                // 实体的字段名称
                txField.setFieldName(field.getName());
                // 表的列名
                txField.setColumnName(columnName);

                txField.setClassName(field.getContainingClass().getQualifiedName());
                Optional<String> jdbcTypeByJavaType = JdbcTypeUtils.findJdbcTypeByJavaType(field.getType().getCanonicalText());
                jdbcTypeByJavaType.ifPresent(txField::setJdbcType);
                return txField;
            }).collect(Collectors.toList());
    }

    /**
     * Gets table field annotation.
     *
     * @param field the field
     * @return the table field annotation
     */
    @NotNull
    protected abstract String getTableFieldAnnotation(@NotNull PsiField field);

    @Override
    public Optional<String> findTableName(PsiClass entityClass) {
        PsiAnnotation annotation = entityClass.getAnnotation(getTableNameAnnotation());
        // 获取 mp 的注解
        Optional<String> tableName = Optional.ofNullable(getAttributeValue(annotation, VALUE));

        // 获取 jpa 注解
        if (!tableName.isPresent()) {
            tableName = getTableNameByJpa(entityClass);
        }
        return tableName;
    }

    /**
     * Gets attribute value.
     *
     * @param fieldAnnotation the field annotation
     * @param value           the value
     * @return the attribute value
     */
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
        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(mapperClass.getProject());
        for (PsiJavaCodeReferenceElement referenceElement : referenceElements) {

            String qualifiedName = referenceElement.getQualifiedName();

            if (getBaseMapperClassName().equals(qualifiedName)) {

                PsiType typeParameter = referenceElement.getTypeParameters()[0];

                PsiClass entityClass = javaPsiFacade.findClass(typeParameter.getCanonicalText(), mapperClass.getResolveScope());

                if (entityClass == null) {
                    continue;
                }

                return Optional.of(entityClass);
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


    /**
     * Gets base mapper class name.
     *
     * @return the base mapper class name
     */
    protected abstract String getBaseMapperClassName();

    @Override
    public List<TxField> findFields(PsiClass mapperClass, PsiClass entityClass) {
        return determineFields(entityClass);
    }

}
