package com.baomidou.plugin.idea.mybatisx.smartjpa.common.type;

import java.util.List;

public interface AppendType {

    String getName();

    List<String> getAllowAfter();

    default boolean checkAfter(AppendType appendType) {
        return getAllowAfter().contains(appendType.getName());
    }
}
