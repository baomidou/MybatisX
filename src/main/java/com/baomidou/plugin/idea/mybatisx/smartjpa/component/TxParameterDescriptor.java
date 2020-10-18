package com.baomidou.plugin.idea.mybatisx.smartjpa.component;

import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.AreaSequence;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.intellij.psi.PsiClass;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 参数描述符
 */
public class TxParameterDescriptor implements TypeDescriptor {

    /**
     * The constant SPACE.
     */
    public static final String SPACE = " ";
    /**
     * The Parameter list.
     */
    List<TxParameter> parameterList = new ArrayList<>();
    private final Map<String, TxField> fieldColumnNameMapping;

    /**
     * Instantiates a new Tx parameter descriptor.
     *
     * @param parameterList the parameter list
     * @param mappingField
     */
    public TxParameterDescriptor(final List<TxParameter> parameterList, List<TxField> mappingField) {
        this.parameterList = parameterList;
        fieldColumnNameMapping = mappingField.stream().collect(Collectors.toMap(TxField::getFieldName, x -> x));
    }

    /**
     * Add boolean.
     *
     * @param txParameter the tx parameter
     * @return the boolean
     */
    public boolean add(TxParameter txParameter) {
        return parameterList.add(txParameter);
    }

//    @Override
//    public List<TxParameter> getParameters(PsiClass entityClass, LinkedList< SyntaxAppender > jpaStringList) {
//        List<TxParameter> parameters = super.getParameters(entityClass, jpaStringList);
//        Set<String> collection = new HashSet<>();
//        for (TxParameter parameter : parameters) {
//            String oldParamName = parameter.getName();
//            if (!collection.add(oldParamName)) {
//                String newName = "old" + StringUtils.upperCaseFirstChar(oldParamName);
//                parameter.setName(newName);
//            }
//        }
//        return parameters;
//    }
    /**
     * 参数字符串
     * TODO 关于 updateUpdateTimeByUpdateTime 这种情况会导致两个参数都无法传， 事实上可能需要第一个不需要传，第二个需要传
     * @return
     * @param defaultDateList
     */
    public String getContent(List<String> defaultDateList) {
        Set<String> addedParamNames = new HashSet<>();
        return parameterList.stream()
            .filter(parameter -> {
                String fieldType = parameter.getTypeText();
                // 能通过字段名找到列名， 并且列名不是默认日期字段的，就可以加入到参数中
                TxField txField = fieldColumnNameMapping.get(parameter.getName());
                // 字段类型不为空 and 不是默认日期字段
                return fieldType != null
                    && (txField == null
                || !defaultDateList.contains(txField.getColumnName())
                || parameter.getAreaSequence() != AreaSequence.RESULT);
            })
            .map(param -> this.getParameterName(param,addedParamNames))
            .collect(Collectors.joining(",","(",");"));
    }

    /**
     * 根据是否需要生成注解字段, 生成注解字段
     * @param txParameter
     * @param addedParamNames
     * @return
     */
    private String getParameterName(TxParameter txParameter, Set<String> addedParamNames) {
        String paramName = txParameter.getName();
        if (!addedParamNames.add(paramName)) {
            paramName = "old" + paramName;
        }
        String defineAnnotation = "@Param(\"" + paramName + "\")";
        String defineParam = txParameter.getTypeText() + SPACE + paramName;
        return txParameter.isParamAnnotation() ? defineAnnotation + defineParam : defineParam;
    }

    /**
     * 要导入的类型列表
     *
     * @return
     */
    public List<String> getImportList() {
        List<String> collect = parameterList.stream()
            .filter(x -> x.getCanonicalTypeText() != null)
            .map(TxParameter::getCanonicalTypeText)
            .collect(Collectors.toList());
        if (collect.size() > 0) {
            collect.add("org.apache.ibatis.annotations.Param");
        }
        return collect;
    }


}
