package com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest;

import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.MapperClassGenerateFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.EmptyGenerator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.Generator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.MybatisAnnotationGenerator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.MybatisXmlGenerator;
import com.baomidou.plugin.idea.mybatisx.ui.SmartJpaAdvanceUI;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The type Condition if test wrapper.
 *
 * @author ls9527
 */
public class ConditionIfTestWrapper implements ConditionFieldWrapper {
    private Project project;
    private Set<String> selectedWrapFields;
    private String allFieldsStr;
    private String resultMap;
    private boolean resultType;
    private String resultTypeClass;
    private Map<String, TxField> txFieldMap;
    /**
     * 默认字段的关键字：  oracle: SYSDATE, mysql: NOW()
     */
    private String defaultDateWord;
    private SmartJpaAdvanceUI.GeneratorEnum generatorType;
    private Mapper mapper;
    private List<String> defaultDateList;

    /**
     * Instantiates a new Condition if test wrapper.
     *  @param project
     * @param selectedWrapFields the wrapper fields
     * @param allFields
     * @param defaultDateWord
     */
    public ConditionIfTestWrapper(@NotNull Project project,
                                  Set<String> selectedWrapFields,
                                  List<TxField> allFields,
                                  String defaultDateWord) {
        this.project = project;
        this.selectedWrapFields = selectedWrapFields;
        txFieldMap = allFields.stream().collect(Collectors.toMap(TxField::getFieldName, x -> x));
        this.defaultDateWord = defaultDateWord;
    }

    @Override
    public String wrapConditionText(String fieldName, String templateText) {
        if (selectedWrapFields.contains(fieldName)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<if test=\"").append(getConditionField(fieldName)).append("\">");
            stringBuilder.append("\n").append(templateText);
            stringBuilder.append("\n").append("</if>");
            templateText = stringBuilder.toString();
        }
        return templateText;
    }

    @Override
    public String wrapWhere(String content) {
        return "<where>\n" + content + "\n</where>";
    }

    @Override
    public String getAllFields() {
        return allFieldsStr;
    }

    @Override
    public String getResultMap() {
        return resultType ? null : resultMap;
    }

    @Override
    public String getResultType() {
        return resultType ? resultTypeClass : null;
    }

    @Override
    public Generator getGenerator(MapperClassGenerateFactory mapperClassGenerateFactory) {
        if (this.generatorType == SmartJpaAdvanceUI.GeneratorEnum.MYBATIS_ANNOTATION) {
            return new MybatisAnnotationGenerator(mapperClassGenerateFactory, mapper, project);
        } else if (this.generatorType == SmartJpaAdvanceUI.GeneratorEnum.MYBATIS_XML
            && mapper != null) {
            return new MybatisXmlGenerator(mapperClassGenerateFactory, mapper, project);
        }
        return new EmptyGenerator();
    }


    private String getConditionField(String fieldName) {
        TxField txField = txFieldMap.get(fieldName);
        String appender = "";
        if (Objects.equals(txField.getFieldType(), "java.lang.String")) {
            appender = " and " + fieldName + " != ''";
        }
        return fieldName + " != null" + appender;
    }

    /**
     * Sets all fields.
     *
     * @param allFieldsStr the all fields str
     */
    public void setAllFields(String allFieldsStr) {
        this.allFieldsStr = allFieldsStr;
    }

    /**
     * Sets result map.
     *
     * @param resultMap the result map
     */
    public void setResultMap(String resultMap) {
        this.resultMap = resultMap;
    }

    /**
     * Sets result type.
     *
     * @param resultType the result type
     */
    public void setResultType(boolean resultType) {
        this.resultType = resultType;
    }

    /**
     * Sets result type class.
     *
     * @param resultTypeClass the result type class
     */
    public void setResultTypeClass(String resultTypeClass) {
        this.resultTypeClass = resultTypeClass;
    }

    public void setGeneratorType(SmartJpaAdvanceUI.GeneratorEnum generatorType) {
        this.generatorType = generatorType;
    }

    @Override
    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 对于默认值 create_time,update_time, 在 更新和插入的时候替换为数据库默认值的关键字
     * MYSQL默认时间: NOW()
     * ORACLE默认时间: SYSDATE
     * @param columnName 字段名
     * @param fieldValue
     * @return
     */
    @Override
    public String wrapDefaultDateIfNecessary(String columnName, String fieldValue) {
        if (defaultDateList.contains(columnName)) {
            return defaultDateWord;
        }
        return fieldValue;
    }

    @Override
    public List<String> getDefaultDateList() {
        return defaultDateList;
    }

    public void setDefaultDateList(List<String> defaultDateList) {
        this.defaultDateList = defaultDateList;
    }


}
