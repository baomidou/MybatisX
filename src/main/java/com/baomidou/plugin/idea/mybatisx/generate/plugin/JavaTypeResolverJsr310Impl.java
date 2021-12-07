package com.baomidou.plugin.idea.mybatisx.generate.plugin;

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

    @Override
    public void addConfigurationProperties(Properties properties) {
        super.addConfigurationProperties(properties);
        this.supportJsr = "true".equals(properties.getProperty("supportJsr"));
        this.supportAutoNumeric = "true".equals(properties.getProperty("supportAutoNumeric"));
    }

    private FullyQualifiedJavaType calcNumeric(IntrospectedColumn column, FullyQualifiedJavaType defaultType) {
        FullyQualifiedJavaType answer = defaultType;
        switch (column.getJdbcType()) {
            case TINYINT:
            case SMALLINT:
                answer = new FullyQualifiedJavaType(Integer.class.getName());
                break;
            case NUMERIC:
            case DECIMAL:
                answer = this.calculateBigDecimalReplacement(column, defaultType);
                break;
            case BIT:
                answer = this.calculateBitReplacement(column, defaultType);
                break;
            case BOOLEAN:
                answer = new FullyQualifiedJavaType(Boolean.class.getName());
                break;
        }
        return answer;
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
        FullyQualifiedJavaType answer = defaultType;
        switch (column.getJdbcType()) {
            case DATE:
                answer = new FullyQualifiedJavaType(LocalDate.class.getName());
                break;
            case TIME:
                answer = new FullyQualifiedJavaType(LocalTime.class.getName());
                break;
            case TIMESTAMP:
                answer = new FullyQualifiedJavaType(LocalDateTime.class.getName());
        }
        return answer;
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
