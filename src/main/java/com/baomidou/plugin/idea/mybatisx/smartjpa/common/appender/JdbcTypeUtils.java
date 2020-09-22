package com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The type Jdbc type utils.
 */
public class JdbcTypeUtils {
    /**
     * Wrapper field string.
     *
     * @param paramName     the param name
     * @param canonicalText the canonical text
     * @return the string
     */
    public static String wrapperField(String paramName, @NotNull String canonicalText) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("#{").append(paramName);
        String jdbcType = fieldJdbcType.get(canonicalText);
        if (StringUtils.isNotBlank(jdbcType)) {
            stringBuilder.append(",jdbcType=").append(jdbcType);
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }



    /**
     * 后续提取类， 先用静态的
     *
     * @return
     */
    private static Map<String, String> fieldJdbcType = new ConcurrentHashMap<>();

    static {
        fieldJdbcType.put("java.lang.Byte","NUMERIC");
        fieldJdbcType.put("java.lang.Short", "NUMERIC");
        fieldJdbcType.put("java.lang.Integer", "NUMERIC");
        fieldJdbcType.put("java.lang.Long", "NUMERIC");
        fieldJdbcType.put("java.lang.Float", "DECIMAL");
        fieldJdbcType.put("java.lang.Double", "DECIMAL");
        fieldJdbcType.put("java.lang.Boolean", "BOOLEAN");
        fieldJdbcType.put("java.lang.String", "VARCHAR");
        fieldJdbcType.put("java.util.Date", "TIMESTAMP");
        fieldJdbcType.put("java.math.BigDecimal", "DECIMAL");
    }
}
