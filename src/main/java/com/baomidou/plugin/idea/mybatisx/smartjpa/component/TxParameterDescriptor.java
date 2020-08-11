package com.baomidou.plugin.idea.mybatisx.smartjpa.component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 参数描述符
 */
public class TxParameterDescriptor implements TypeDescriptor {

    public static final String SPACE = " ";
    List<TxParameter> parameterList = new ArrayList<>();

    public TxParameterDescriptor(final List<TxParameter> parameterList) {
        this.parameterList = parameterList;
    }

    public boolean add(TxParameter txParameter) {
        return parameterList.add(txParameter);
    }

    /**
     * 参数字符串
     *
     * @return
     */
    public String getContent() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        String paramString = parameterList.stream()
            .filter(x -> x.getTypeText() != null)
            .map(x -> getParameterName(x))
            .collect(Collectors.joining(","));
        stringBuilder.append(paramString);
        stringBuilder.append(");");
        return stringBuilder.toString();
    }

    /**
     * 根据是否需要生成注解字段, 生成注解字段
     * @param x
     * @return
     */
    private String getParameterName(TxParameter x) {
        String defineAnnotation = "@Param(\"" + x.getName() + "\")";
        String defineParam = x.getTypeText() + SPACE + x.getName();
        return x.isParamAnnotation() ? defineAnnotation + defineParam : defineParam;
    }

    /**
     * 要导入的类型列表
     *
     * @return
     */
    public List<String> getImportList() {
        List<String> collect = parameterList.stream()
            .filter(x -> x.getCanonicalTypeText() != null)
            .map(x -> x.getCanonicalTypeText())
            .collect(Collectors.toList());
        if (collect.size() > 0) {
            collect.add("org.apache.ibatis.annotations.Param");
        }
        return collect;
    }


}
