package com.baomidou.plugin.idea.mybatisx.smartjpa.common.type.content;




import com.baomidou.plugin.idea.mybatisx.smartjpa.common.type.AppendType;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.model.AppendTypeEnum;

import java.util.List;

/**
 * 排序区
 */
public class SuffixAppendType implements AppendType {
    @Override
    public String getName() {
        return AppendTypeEnum.SUFFIX.name();
    }

    /**
     * 允许所有字段
     *
     * @return
     */
    @Override
    public List<String> getAllowAfter() {
        return AppendTypeEnum.SUFFIX.getAllowedAfterList();
    }
}
