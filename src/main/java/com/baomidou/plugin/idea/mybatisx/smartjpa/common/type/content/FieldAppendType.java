package com.baomidou.plugin.idea.mybatisx.smartjpa.common.type.content;


import com.baomidou.plugin.idea.mybatisx.smartjpa.common.type.AppendType;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.model.AppendTypeEnum;

import java.util.List;

/**
 * 字段
 */
public class FieldAppendType implements AppendType {
    @Override
    public String getName() {
        return AppendTypeEnum.FIELD.name();
    }

    /**
     * 允许所有区域
     *
     * @return
     */
    @Override
    public List<String> getAllowAfter() {
        return AppendTypeEnum.FIELD.getAllowedAfterList();
    }

}
