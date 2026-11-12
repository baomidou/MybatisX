package com.baomidou.plugin.idea.mybatisx.generate.typemapping;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 按一份映射规则把数据库类型名解析成 Java 类型。
 * 显示宽度、词序和大小写在匹配前去掉，规则按数组顺序命中第一条。
 */
public final class TypeMappingEngine {
    public static final String RAW_TYPE_NAME = "mybatisx.rawTypeName";

    private static final Pattern PARENTHESES = Pattern.compile("\\(([^)]*)\\)");
    private static final Map<String, String> JAVA_TYPES = Map.ofEntries(
        Map.entry("int", "int"),
        Map.entry("Integer", "java.lang.Integer"),
        Map.entry("long", "long"),
        Map.entry("Long", "java.lang.Long"),
        Map.entry("boolean", "boolean"),
        Map.entry("Boolean", "java.lang.Boolean"),
        Map.entry("byte", "byte"),
        Map.entry("Byte", "java.lang.Byte"),
        Map.entry("short", "short"),
        Map.entry("Short", "java.lang.Short"),
        Map.entry("float", "float"),
        Map.entry("Float", "java.lang.Float"),
        Map.entry("double", "double"),
        Map.entry("Double", "java.lang.Double"),
        Map.entry("String", "java.lang.String"),
        Map.entry("BigDecimal", "java.math.BigDecimal"),
        Map.entry("BigInteger", "java.math.BigInteger"),
        Map.entry("byte[]", "byte[]"),
        Map.entry("LocalDate", "java.time.LocalDate"),
        Map.entry("LocalTime", "java.time.LocalTime"),
        Map.entry("LocalDateTime", "java.time.LocalDateTime"),
        Map.entry("Date", "java.util.Date"),
        Map.entry("Timestamp", "java.sql.Timestamp"),
        Map.entry("Time", "java.sql.Time")
    );
    private static final Map<String, String> PRIMITIVE_TO_WRAPPER = Map.of(
        "int", "java.lang.Integer",
        "long", "java.lang.Long",
        "boolean", "java.lang.Boolean",
        "byte", "java.lang.Byte",
        "short", "java.lang.Short",
        "float", "java.lang.Float",
        "double", "java.lang.Double",
        "char", "java.lang.Character"
    );

    public Optional<MappedColumnType> match(TypeMapping mapping,
                                             String rawTypeName,
                                             Integer columnLength,
                                             Integer columnScale,
                                             boolean nullable,
                                             boolean jsr310) {
        if (mapping == null || mapping.rules == null || rawTypeName == null || rawTypeName.isBlank()) {
            return Optional.empty();
        }
        Normalized normalized = normalize(mapping, rawTypeName, columnLength, columnScale);
        if (normalized.type.isEmpty()) {
            return Optional.empty();
        }
        for (TypeMapping.Rule rule : mapping.rules) {
            if (rule == null || !matches(mapping, rule, normalized)) {
                continue;
            }
            String javaType = resolveJavaType(rule.javaType, nullable, jsr310);
            if (javaType == null || rule.jdbcType == null || rule.jdbcType.isBlank()) {
                return Optional.empty();
            }
            return Optional.of(new MappedColumnType(javaType, rule.jdbcType));
        }
        return Optional.empty();
    }

    private boolean matches(TypeMapping mapping, TypeMapping.Rule rule, Normalized normalized) {
        if (rule.type == null || !rule.type.equalsIgnoreCase(normalized.type)) {
            return false;
        }
        if (rule.unsigned != null && rule.unsigned != normalized.unsigned) {
            return false;
        }
        if (rule.enabledBy != null && !Boolean.TRUE.equals(option(mapping, rule.enabledBy))) {
            return false;
        }
        TypeMapping.When when = rule.when;
        if (when == null) {
            return true;
        }
        if (when.length != null && !when.length.equals(normalized.length)) {
            return false;
        }
        if (when.scale != null && !when.scale.equals(normalized.scale)) {
            return false;
        }
        if (when.scaleMin != null && (normalized.scale == null || normalized.scale < when.scaleMin)) {
            return false;
        }
        if (when.precisionMax != null && (normalized.precision == null || normalized.precision > when.precisionMax)) {
            return false;
        }
        return true;
    }

    private Boolean option(TypeMapping mapping, String name) {
        if (mapping.options == null) {
            return null;
        }
        for (Map.Entry<String, Boolean> entry : mapping.options.entrySet()) {
            if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(name)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private Normalized normalize(TypeMapping mapping, String rawTypeName, Integer columnLength, Integer columnScale) {
        String source = rawTypeName.toLowerCase(Locale.ROOT).trim();
        Integer length = null;
        Integer scale = null;
        Matcher parentheses = PARENTHESES.matcher(source);
        if (parentheses.find()) {
            String inside = parentheses.group(1).trim();
            int comma = inside.indexOf(',');
            if (comma < 0) {
                length = parseInt(inside);
            } else {
                length = parseInt(inside.substring(0, comma).trim());
                scale = parseInt(inside.substring(comma + 1).trim());
            }
            source = (source.substring(0, parentheses.start()) + " " + source.substring(parentheses.end())).trim();
        }
        boolean unsigned = false;
        StringBuilder typeBuilder = new StringBuilder();
        for (String token : source.split("\\s+")) {
            if (token.isEmpty()) {
                continue;
            }
            if ("unsigned".equals(token) || "zerofill".equals(token)) {
                unsigned = true;
                continue;
            }
            if ("signed".equals(token)) {
                continue;
            }
            if (typeBuilder.length() > 0) {
                typeBuilder.append(' ');
            }
            typeBuilder.append(token);
        }
        String type = typeBuilder.toString();
        if (mapping.rewrites != null) {
            for (TypeMapping.Rewrite rewrite : mapping.rewrites) {
                if (rewrite == null || rewrite.from == null || rewrite.type == null) {
                    continue;
                }
                if (rewrite.from.equalsIgnoreCase(type)) {
                    type = rewrite.type.toLowerCase(Locale.ROOT);
                    if (rewrite.unsigned != null) {
                        unsigned = rewrite.unsigned;
                    }
                    break;
                }
            }
        }
        type = applyAlias(mapping.aliases, type);
        if (length == null && columnLength != null && columnLength > 0) {
            length = columnLength;
        }
        if (scale == null && columnScale != null && columnScale != 0) {
            scale = columnScale;
        }
        if (scale == null && isExactNumeric(type) && length != null) {
            scale = 0;
        }
        return new Normalized(type, unsigned, length, length, scale);
    }

    private boolean isExactNumeric(String type) {
        return "decimal".equals(type) || "number".equals(type);
    }

    private String applyAlias(Map<String, String> aliases, String type) {
        if (aliases == null || aliases.isEmpty()) {
            return type;
        }
        String current = type;
        for (int i = 0; i < 5; i++) {
            String next = null;
            for (Map.Entry<String, String> entry : aliases.entrySet()) {
                if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(current) && entry.getValue() != null) {
                    next = entry.getValue().toLowerCase(Locale.ROOT).trim();
                    break;
                }
            }
            if (next == null || next.equals(current)) {
                break;
            }
            current = next;
        }
        return current;
    }

    private Integer parseInt(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(text);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String resolveJavaType(JsonElement javaType, boolean nullable, boolean jsr310) {
        if (javaType == null || javaType.isJsonNull()) {
            return null;
        }
        String shortName;
        if (javaType.isJsonObject()) {
            JsonObject object = javaType.getAsJsonObject();
            String key = jsr310 ? "jsr310" : "legacy";
            if (object.has(key) && object.get(key).isJsonPrimitive()) {
                shortName = object.get(key).getAsString();
            } else if (object.has("jsr310") && object.get("jsr310").isJsonPrimitive()) {
                shortName = object.get("jsr310").getAsString();
            } else {
                return null;
            }
        } else if (javaType.isJsonPrimitive()) {
            shortName = javaType.getAsString();
        } else {
            return null;
        }
        if (shortName == null || shortName.isBlank()) {
            return null;
        }
        String resolved = JAVA_TYPES.getOrDefault(shortName, shortName);
        if (nullable) {
            String wrapper = PRIMITIVE_TO_WRAPPER.get(resolved);
            if (wrapper != null) {
                return wrapper;
            }
        }
        return resolved;
    }

    private record Normalized(String type, boolean unsigned, Integer length, Integer precision, Integer scale) {
    }
}
