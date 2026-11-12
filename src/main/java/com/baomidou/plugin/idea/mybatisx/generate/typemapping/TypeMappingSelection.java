package com.baomidou.plugin.idea.mybatisx.generate.typemapping;

import java.util.List;

/**
 * 在已扫描到的映射文件中决定下拉框选中项。
 */
public final class TypeMappingSelection {
    public static final String DEFAULT_FILE = "mysql.json";

    private TypeMappingSelection() {
    }

    public static TypeMappingOption choose(List<TypeMappingOption> options, String savedFileName) {
        if (options == null || options.isEmpty()) {
            return null;
        }
        if (savedFileName != null) {
            for (TypeMappingOption option : options) {
                if (savedFileName.equals(option.getFileName())) {
                    return option;
                }
            }
        }
        for (TypeMappingOption option : options) {
            if (DEFAULT_FILE.equals(option.getFileName())) {
                return option;
            }
        }
        return options.get(0);
    }
}
