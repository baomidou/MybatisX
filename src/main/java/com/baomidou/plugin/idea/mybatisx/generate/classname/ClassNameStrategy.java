package com.baomidou.plugin.idea.mybatisx.generate.classname;

import lombok.Getter;

public interface ClassNameStrategy {
    String getText();

    String calculateClassName(String tableName, String ignorePrefix, String ignoreSuffix);

    @Getter
    enum ClassNameStrategyEnum {
        CAMEL("camel"),
        SAME("same as tablename");
        private final String text;

        ClassNameStrategyEnum(String text) {
            this.text = text;
        }

    }
}
