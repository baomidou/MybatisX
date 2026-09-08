package com.baomidou.plugin.idea.mybatisx.generate.type;

import com.baomidou.plugin.idea.mybatisx.generate.dto.TemplateAnnotationType;


public class AnnotationTypeOperatorFactory {
    public static AnnotationTypeOperator findByType(String type) {
        TemplateAnnotationType templateAnnotationType = TemplateAnnotationType.valueOf(type);
        return switch (templateAnnotationType) {
            case JPA -> new JpaAnnotationTypeOperator();
            case MYBATIS_PLUS3 -> new MybatisPlus3AnnotationTypeOperator();
            case MYBATIS_PLUS2 -> new MybatisPlus2AnnotationTypeOperator();
            default -> new NoneAnnotationTypeOperator();
        };
    }
}
