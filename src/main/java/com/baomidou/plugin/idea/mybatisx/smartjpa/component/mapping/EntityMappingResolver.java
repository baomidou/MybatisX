package com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping;

import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;

import java.util.List;

/**
 * 实体映射解析器
 */
public interface EntityMappingResolver {

    /**
     * 获取所有字段
     *
     * @return
     */
    List<TxField> getFields();

    /**
     * 获取所有表名
     *
     * @return
     */
    String getTableName();
}
