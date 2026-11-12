package com.baomidou.plugin.idea.mybatisx.generate.typemapping;

import java.util.Objects;

/**
 * 下拉框中的一份类型映射文件。
 */
public final class TypeMappingOption {
    private final String fileName;
    private final String displayName;

    public TypeMappingOption(String fileName, String displayName) {
        this.fileName = fileName;
        this.displayName = displayName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TypeMappingOption option)) {
            return false;
        }
        return Objects.equals(fileName, option.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fileName);
    }
}
