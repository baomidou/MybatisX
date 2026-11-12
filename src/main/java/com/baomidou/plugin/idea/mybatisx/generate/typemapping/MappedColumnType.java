package com.baomidou.plugin.idea.mybatisx.generate.typemapping;

/**
 * 一条规则命中后的 Java 类型和 JDBC 类型名。
 */
public record MappedColumnType(String javaType, String jdbcType) {
}
