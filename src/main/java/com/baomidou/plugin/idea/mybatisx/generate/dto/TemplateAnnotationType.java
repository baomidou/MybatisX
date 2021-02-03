package com.baomidou.plugin.idea.mybatisx.generate.dto;

/**
 * 模板注解类型
 */
public enum TemplateAnnotationType {
    /**
     * 实体类没有注解
     */
    NONE,
    /**
     *  实体类加入 MYBATIS_PLUS3 注解
     */
    MYBATIS_PLUS3,
    /**
     *  实体类加入 MYBATIS_PLUS2 注解
     */
    MYBATIS_PLUS2,
    /**
     * 实体类加入 JPA 注解
     */
    JPA;
}
