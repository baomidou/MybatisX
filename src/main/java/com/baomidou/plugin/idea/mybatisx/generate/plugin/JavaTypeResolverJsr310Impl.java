package com.baomidou.plugin.idea.mybatisx.generate.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Properties;

import static java.sql.Types.BIT;
import static java.sql.Types.DATE;
import static java.sql.Types.DECIMAL;
import static java.sql.Types.NUMERIC;
import static java.sql.Types.TIME;
import static java.sql.Types.TIMESTAMP;

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

    protected FullyQualifiedJavaType overrideDefaultType(IntrospectedColumn column, FullyQualifiedJavaType defaultType) {
        if (!supportJsr) {
            return super.overrideDefaultType(column, defaultType);
        }
        FullyQualifiedJavaType answer = defaultType;
        switch (column.getJdbcType()) {
            case BIT:
                answer = this.calculateBitReplacement(column, defaultType);
                break;
            case NUMERIC:
            case DECIMAL:
                answer = this.calculateBigDecimalReplacement(column, defaultType);
                break;
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
