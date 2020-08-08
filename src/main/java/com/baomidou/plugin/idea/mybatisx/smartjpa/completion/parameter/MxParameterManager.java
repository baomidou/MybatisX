package com.baomidou.plugin.idea.mybatisx.smartjpa.completion.parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 参数管理器
 */
public class MxParameterManager {

    public static final String SPACE = " ";
    List<MxParameter> parameterList = new ArrayList<>();

    public MxParameterManager(final List<MxParameter> parameterList) {
        this.parameterList = parameterList;
    }

    public boolean add(MxParameter mxParameter) {
        return parameterList.add(mxParameter);
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
                .map(x -> "@Param(\"" + x.getName() + "\")" + x.getTypeText() + SPACE + x.getName())
                .collect(Collectors.joining(","));
        stringBuilder.append(paramString);
        stringBuilder.append(");");
        return stringBuilder.toString();
    }

    /**
     * 要导入的类型列表
     *
     * @return
     */
    public List<String> getImports() {
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
