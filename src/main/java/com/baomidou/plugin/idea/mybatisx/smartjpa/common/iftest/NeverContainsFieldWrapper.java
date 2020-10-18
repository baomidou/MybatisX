package com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest;

import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.MapperClassGenerateFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.EmptyGenerator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.Generator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.MybatisXmlGenerator;
import com.intellij.openapi.project.Project;

import java.util.Collections;
import java.util.List;

/**
 * The type Never contains field wrapper.
 *
 * @author ls9527
 */
public class NeverContainsFieldWrapper implements ConditionFieldWrapper {
    private Project project;
    private Mapper mapper;

    public NeverContainsFieldWrapper(Project project) {
        this.project = project;
    }

    @Override
    public String wrapConditionText(String fieldName, String templateText) {
        return templateText;
    }

    @Override
    public String wrapWhere(String content) {
        return "where \n" + content;
    }

    /**
     * 默认的 查询所有字段的方式
     *
     * @return
     */
    @Override
    public String getAllFields() {
        return "<include refid=\"Base_Column_List\" />";
    }

    @Override
    public String getResultMap() {
        return "BaseResultMap";
    }

    @Override
    public String getResultType() {
        return null;
    }

    @Override
    public Generator getGenerator(MapperClassGenerateFactory mapperClassGenerateFactory) {
        if (mapper == null) {
            return new EmptyGenerator();
        }
        return new MybatisXmlGenerator(mapperClassGenerateFactory, mapper, project);
    }

    @Override
    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String wrapDefaultDateIfNecessary(String columnName, String fieldValue) {
        return fieldValue;
    }

    @Override
    public List<String> getDefaultDateList() {
        return Collections.emptyList();
    }
}
