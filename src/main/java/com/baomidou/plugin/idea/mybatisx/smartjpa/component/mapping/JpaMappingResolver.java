package com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping;

import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiReferenceList;
import com.intellij.psi.PsiReferenceParameterList;
import com.intellij.psi.PsiType;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class JpaMappingResolver {


    public static final String JAVAX_PERSISTENCE_TABLE = "javax.persistence.Table";
    public static final String JAVAX_PERSISTENCE_COLUMN = "javax.persistence.Column";
    public static final String COLUMN_NAME = "name";
    public static final String TABLE_NAME = "name";

    protected String getTableNameByJpaOrCamel(PsiClass entityClass) {
        String tableName = null;
        PsiAnnotation annotation = entityClass.getAnnotation(JAVAX_PERSISTENCE_TABLE);
        if (annotation != null) {
            PsiAnnotationMemberValue originTable = annotation.findAttributeValue(TABLE_NAME);
            PsiLiteralExpression expression = (PsiLiteralExpression) originTable;
            tableName = expression.getValue().toString();
        }
        if (tableName == null) {
            tableName = getUnderLineName(entityClass.getName());
        }
        return tableName;
    }

    protected String getColumnNameByJpaOrCamel(PsiField field) {
        String columnName = null;
        // 根据jpa的方式修改列名
        PsiAnnotation annotation = field.getAnnotation(JAVAX_PERSISTENCE_COLUMN);
        if (annotation != null) {
            PsiAnnotationMemberValue originFieldAnnotation = annotation.findAttributeValue(COLUMN_NAME);
            PsiLiteralExpression expression = (PsiLiteralExpression) originFieldAnnotation;
            columnName = expression.getValue().toString();
        }
        // 驼峰转下划线
        if (columnName == null) {
            columnName = getUnderLineName(field.getName());
        }
        return columnName;
    }

    @NotNull
    private String getUnderLineName(String camelName) {
        String[] strings = org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase(camelName);
        return Arrays.stream(strings).map(x -> com.baomidou.plugin.idea.mybatisx.util.StringUtils.lowerCaseFirstChar(x))
            .collect(Collectors.joining("_"));
    }

    public static Optional<PsiClass> findEntityClassByMapperClass(PsiClass mapperClass) {
        JavaPsiFacade instance = JavaPsiFacade.getInstance(mapperClass.getProject());
        PsiReferenceList extendsList = mapperClass.getExtendsList();
        if (extendsList != null) {
            @NotNull PsiJavaCodeReferenceElement[] referenceElements = extendsList.getReferenceElements();
            if (referenceElements.length > 0) {
                for (PsiJavaCodeReferenceElement referenceElement : referenceElements) {
                    PsiReferenceParameterList parameterList = referenceElement.getParameterList();
                    if (parameterList != null) {
                        @NotNull PsiType[] typeArguments = parameterList.getTypeArguments();
                        if (typeArguments != null) {
                            for (PsiType type : typeArguments) {
                                String canonicalText = type.getCanonicalText();
                                // 当存在多个类型的时候, 排除主键类型.  java开头的包
                                if (!canonicalText.startsWith("java")
                                 &&StringUtils.isNotBlank(canonicalText)) {
                                    PsiClass entityClass = instance.findClass(canonicalText, mapperClass.getResolveScope());
                                    if (entityClass != null) {
                                        PsiAnnotation annotation = entityClass.getAnnotation(JAVAX_PERSISTENCE_TABLE);
                                        if (annotation != null) {
                                            return Optional.of(entityClass);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }


    protected List<TxField> initDataByCamel(PsiClass entityClass) {
        return Arrays.stream(entityClass.getAllFields()).map(field -> {
            TxField txField = new TxField();
            txField.setTipName(com.baomidou.plugin.idea.mybatisx.util.StringUtils.upperCaseFirstChar(field.getName()));
            txField.setFieldType(field.getType().getCanonicalText());

            String columnName = getColumnNameByJpaOrCamel(field);
            // 实体的字段名称
            txField.setFieldName(field.getName());
            // 表的列名
            txField.setColumnName(columnName);

            return txField;
        }).collect(Collectors.toList());
    }

}
