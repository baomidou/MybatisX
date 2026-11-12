package com.baomidou.plugin.idea.mybatisx.generate.plugin;

import com.baomidou.plugin.idea.mybatisx.generate.typemapping.MappedColumnType;
import com.baomidou.plugin.idea.mybatisx.generate.typemapping.TypeMapping;
import com.baomidou.plugin.idea.mybatisx.generate.typemapping.TypeMappingCatalog;
import com.baomidou.plugin.idea.mybatisx.generate.typemapping.TypeMappingEngine;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Properties;

import static java.sql.Types.BIT;
import static java.sql.Types.BOOLEAN;
import static java.sql.Types.DATE;
import static java.sql.Types.DECIMAL;
import static java.sql.Types.NUMERIC;
import static java.sql.Types.SMALLINT;
import static java.sql.Types.TIME;
import static java.sql.Types.TIMESTAMP;
import static java.sql.Types.TINYINT;

/**
 * JSR 类型
 */
public class JavaTypeResolverJsr310Impl extends JavaTypeResolverDefaultImpl {

    private boolean supportJsr;
    private boolean supportAutoNumeric;
    private String typeMappingFile;
    private TypeMapping typeMapping;
    private boolean typeMappingResolved;
    private final TypeMappingEngine typeMappingEngine = new TypeMappingEngine();

    @Override
    public void addConfigurationProperties(Properties properties) {
        super.addConfigurationProperties(properties);
        this.supportJsr = "true".equals(properties.getProperty("supportJsr"));
        this.supportAutoNumeric = "true".equals(properties.getProperty("supportAutoNumeric"));
        this.typeMappingFile = properties.getProperty("typeMapping");
    }

    @Override
    public FullyQualifiedJavaType calculateJavaType(IntrospectedColumn introspectedColumn) {
        MappedColumnType mapped = mappedColumn(introspectedColumn);
        if (mapped != null) {
            return new FullyQualifiedJavaType(mapped.javaType());
        }
        return super.calculateJavaType(introspectedColumn);
    }

    @Override
    public String calculateJdbcTypeName(IntrospectedColumn introspectedColumn) {
        MappedColumnType mapped = mappedColumn(introspectedColumn);
        if (mapped != null) {
            return mapped.jdbcType();
        }
        return super.calculateJdbcTypeName(introspectedColumn);
    }

    private MappedColumnType mappedColumn(IntrospectedColumn column) {
        TypeMapping mapping = resolveTypeMapping();
        if (mapping == null || column.getProperties() == null) {
            return null;
        }
        String rawTypeName = column.getProperties().getProperty(TypeMappingEngine.RAW_TYPE_NAME);
        if (rawTypeName == null || rawTypeName.isBlank()) {
            return null;
        }
        Integer length = column.getLength() > 0 ? column.getLength() : null;
        return typeMappingEngine.match(mapping, rawTypeName, length, column.getScale(), column.isNullable(), supportJsr)
            .orElse(null);
    }

    private TypeMapping resolveTypeMapping() {
        if (!typeMappingResolved) {
            typeMappingResolved = true;
            if (typeMappingFile != null && !typeMappingFile.isBlank()) {
                typeMapping = TypeMappingCatalog.load(typeMappingFile);
            }
        }
        return typeMapping;
    }

    private FullyQualifiedJavaType calcNumeric(IntrospectedColumn column, FullyQualifiedJavaType defaultType) {
        return switch (column.getJdbcType()) {
            case TINYINT, SMALLINT -> new FullyQualifiedJavaType(Integer.class.getName());
            case NUMERIC, DECIMAL -> this.calculateBigDecimalReplacement(column, defaultType);
            case BIT -> this.calculateBitReplacement(column, defaultType);
            case BOOLEAN -> new FullyQualifiedJavaType(Boolean.class.getName());
            default -> defaultType;
        };
    }

    @Override
    protected FullyQualifiedJavaType overrideDefaultType(IntrospectedColumn column, FullyQualifiedJavaType defaultType) {
        if (!supportJsr) {
            final FullyQualifiedJavaType fullyQualifiedJavaType = super.overrideDefaultType(column, defaultType);
            return calcNumeric(column, fullyQualifiedJavaType);
        }
        defaultType = calcNumeric(column, defaultType);
        return calcDate(column, defaultType);
    }

    private FullyQualifiedJavaType calcDate(IntrospectedColumn column, FullyQualifiedJavaType defaultType) {
        return switch (column.getJdbcType()) {
            case DATE -> new FullyQualifiedJavaType(LocalDate.class.getName());
            case TIME -> new FullyQualifiedJavaType(LocalTime.class.getName());
            case TIMESTAMP -> new FullyQualifiedJavaType(LocalDateTime.class.getName());
            default -> defaultType;
        };
    }


    @Override
    protected FullyQualifiedJavaType calculateBigDecimalReplacement(IntrospectedColumn column,
                                                                    FullyQualifiedJavaType defaultType) {
        if (!supportAutoNumeric) {
            return super.calculateBigDecimalReplacement(column, defaultType);
        }
        FullyQualifiedJavaType answer;

        if (column.getScale() > 0) {
            answer = defaultType;
        } else if (column.getLength() > 10) {
            answer = new FullyQualifiedJavaType(Long.class.getName());
        } else {
            answer = new FullyQualifiedJavaType(Integer.class.getName());
        }

        return answer;
    }
}
