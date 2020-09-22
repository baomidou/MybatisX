package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.model;

import java.util.Arrays;
import java.util.List;

/**
 * The enum Append type enum.
 */
public enum AppendTypeEnum {
    /**
     * 无前缀
     */
    EMPTY(Arrays.asList("AREA")),


    /**
     * 区域
     * field: selectId
     * area: selectBy
     */
    AREA(Arrays.asList("FIELD", "AREA")),

    /**
     * 字段
     */
    FIELD(Arrays.asList("JOIN", "SUFFIX", "AREA")),

    /**
     * 连接符
     */
    JOIN(Arrays.asList("FIELD")),

    /**
     * 后缀
     */
    SUFFIX(Arrays.asList("FIELD", "JOIN", "AREA"));

    private final List<String> allowedAfterList;

    AppendTypeEnum(final List<String> allowedAfterList) {
        this.allowedAfterList = allowedAfterList;
    }

    /**
     * Gets allowed after list.
     *
     * @return the allowed after list
     */
    public List<String> getAllowedAfterList() {
        return allowedAfterList;
    }

    /**
     * Check after boolean.
     *
     * @param appendType the append type
     * @return the boolean
     */
    public boolean checkAfter(final AppendTypeEnum appendType) {
        return this.allowedAfterList.contains(appendType.name());
    }
}
