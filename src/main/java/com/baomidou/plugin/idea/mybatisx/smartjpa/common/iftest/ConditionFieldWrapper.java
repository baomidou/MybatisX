package com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest;

import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.MapperClassGenerateFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.Generator;

import java.util.List;

/**
 * The interface Condition field wrapper.
 */
public interface ConditionFieldWrapper {
    /**
     * Wrapper condition text string.
     *
     * @param fieldName    the field name
     * @param templateText the template text
     * @return the string
     */
    String wrapConditionText(String fieldName, String templateText);

    /**
     * Wrapper where string.
     *
     * @param content the content
     * @return the string
     */
    String wrapWhere(String content);

    /**
     * Gets all fields.
     *
     * @return the all fields
     */
    String getAllFields();

    /**
     * Gets result map.
     *
     * @return the result map
     */
    String getResultMap();

    /**
     * Gets result type.
     *
     * @return the result type
     */
    String getResultType();

    Boolean isResultType();

    Generator getGenerator(MapperClassGenerateFactory mapperClassGenerateFactory);

    void setMapper(Mapper mapper);

    /**
     * 包装默认的字段值，
     * 如果字段名是默认字段, 返回默认日期关键字。
     * 如果不是默认字段,任然返回原先的字段值
     * 例如对 create_time,update_time 字段改为数据库的默认时间
     * oracle的默认日期: SYSDATE
     *
     * @param columnName 字段名
     * @param fieldValue 字段的实际值
     * @return 字段值
     */
    String wrapDefaultDateIfNecessary(String columnName, String fieldValue);

    List<String> getDefaultDateList();

    List<TxField> getResultTxFields();

    /**
     * 默认换行数量
     * @return
     */
    int getNewline();

}
