package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.model;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The enum Append type enum.
 */
@Getter
public enum AppendTypeEnum {
    /**
     * 无前缀
     */
    EMPTY(Collections.singletonList("AREA")),


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
    JOIN(Collections.singletonList("FIELD")),

    /**
     * 后缀
     */
    SUFFIX(Arrays.asList("FIELD", "JOIN", "AREA"));

    /**
     * -- GETTER --
     *  Gets allowed after list.
     *
     * @return the allowed after list
     */
    private final List<String> allowedAfterList;

    AppendTypeEnum(final List<String> allowedAfterList) {
        this.allowedAfterList = allowedAfterList;
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
