package com.baomidou.plugin.idea.mybatisx.generate.classname;

public class SameAsTableNameStrategy implements ClassNameStrategy {
    @Override
    public String getText() {
        return "same as tablename";
    }

    @Override
    public String calculateClassName(String tableName, String ignorePrefix, String ignoreSuffix) {
        return tableName;
    }
}
