package com.baomidou.plugin.idea.mybatisx.generate.classname;

public interface ClassNameStrategy {
    String getText();

    public enum ClassNameStrategyEnum{
        CAMEL("camel"),
        SAME("same as tablename");
        private String text;

        public String getText() {
            return text;
        }

        ClassNameStrategyEnum(String text) {
            this.text = text;
        }
    }
    String calculateClassName(String tableName, String ignorePrefix, String ignoreSuffix);
}
