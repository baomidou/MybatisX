package com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest;

import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;

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
    private Set<String> wrapperFields;
    private String allFieldsStr;
    private String resultMap;
    private boolean resultType;
    private String resultTypeClass;
    private Map<String, TxField> txFieldMap;

    /**
     * Instantiates a new Condition if test wrapper.
     *
     * @param wrapperFields the wrapper fields
     * @param allFields
     */
    public ConditionIfTestWrapper(Set<String> wrapperFields, List<TxField> allFields) {
        this.wrapperFields = wrapperFields;
        txFieldMap = allFields.stream().collect(Collectors.toMap(TxField::getFieldName, x -> x));
    }

    @Override
    public String wrapperConditionText(String fieldName, String templateText) {
        if (wrapperFields.contains(fieldName)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<if test=\"").append(getConditionField(fieldName)).append("\">");
            stringBuilder.append("\n").append(templateText);
            stringBuilder.append("\n").append("</if>");
            templateText = stringBuilder.toString();
        }
        return templateText;
    }

    @Override
    public String wrapperWhere(String content) {
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


    private String getConditionField(String fieldName) {
        TxField txField = txFieldMap.get(fieldName);
        String appender = "";
        if(Objects.equals(txField.getFieldType(), "java.lang.String")){
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
}
