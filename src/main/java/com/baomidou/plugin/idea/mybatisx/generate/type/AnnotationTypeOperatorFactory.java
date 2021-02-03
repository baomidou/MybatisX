package com.baomidou.plugin.idea.mybatisx.generate.type;

import com.baomidou.plugin.idea.mybatisx.generate.dto.TemplateAnnotationType;


public class AnnotationTypeOperatorFactory {
    public static AnnotationTypeOperator findByType(String type) {
        TemplateAnnotationType templateAnnotationType = TemplateAnnotationType.valueOf(type);
        switch (templateAnnotationType) {
            case JPA:
                return new JpaAnnotationTypeOperator();
            case MYBATIS_PLUS3:
                return new MybatisPlus3AnnotationTypeOperator();
            case MYBATIS_PLUS2:
                return new MybatisPlus2AnnotationTypeOperator();
        }
        return new NoneAnnotationTypeOperator();
    }
}
