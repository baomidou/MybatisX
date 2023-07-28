package com.baomidou.plugin.idea.mybatisx.generate.classname;

import com.baomidou.plugin.idea.mybatisx.util.StringUtils;

public class CamelStrategy implements ClassNameStrategy {

    @Override
    public String getText() {
        return "camel";
    }

    @Override
    public String calculateClassName(String tableName, String ignorePrefix, String ignoreSuffix) {
        String fName = tableName;
        final String EMPTY = "";
        if (!StringUtils.isEmpty(ignorePrefix)) {
            String[] prefixItems = ignorePrefix.split(",");
            for (String prefixItem : prefixItems) {
                fName = fName.replaceAll("^" + prefixItem, EMPTY);
            }
        }
        if (!StringUtils.isEmpty(ignoreSuffix)) {
            String[] suffixItems = ignoreSuffix.split(",");
            for (String suffixItem : suffixItems) {
                fName = fName.replaceFirst(suffixItem + "$", EMPTY);
            }
        }
        return StringUtils.dbStringToCamelStyle(fName);
    }
}
