package com.baomidou.plugin.idea.mybatisx.smartjpa.common.type.content;


import com.baomidou.plugin.idea.mybatisx.smartjpa.common.type.AppendType;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.model.AppendTypeEnum;

import java.util.List;

/**
 * 连接
 */
public class JoinAppendType implements AppendType {
    @Override
    public String getName() {
        return AppendTypeEnum.JOIN.name();
    }

    /**
     * 允许所有字段
     *
     * @return
     */
    @Override
    public List<String> getAllowAfter() {
        return AppendTypeEnum.JOIN.getAllowedAfterList();
    }

}
