package com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.changer;


import androidx.annotation.NonNull;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.MxParameterChanger;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest.ConditionFieldWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxParameter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * The type In parameter changer.
 */
@Slf4j
public class InParameterChanger implements MxParameterChanger {

    @Override
    public List<TxParameter> getParameter(TxParameter txParameter) {
        TxParameter collectionParameter = TxParameter.createCollectionByTxParameter(txParameter);
        return Collections.singletonList(collectionParameter);
    }

    @Override
    public String getTemplateText(String fieldName, @NonNull LinkedList<TxParameter> parameters, ConditionFieldWrapper conditionFieldWrapper) {
        final TxParameter parameter = parameters.poll();
        if (parameter == null) {
            log.info("parameter is null, can not getTemplateText");
            return "";
        }
        final String collectionName = parameter.getName();
        String itemName = "item";
        String itemContent = "#{" + itemName + "}";
        // 如果集合的泛型不是空的, 就给遍历的内容加入 jdbcType
        if (parameter.getItemContent(itemName) != null) {
            itemContent = parameter.getItemContent(itemName);
        }
        final String templateText = fieldName + " " + getIn() + "\n" +
            "<foreach collection=\"" +
            collectionName +
            "\" item=\"item\" open=\"(\" close=\")\" separator=\",\">" + "\n" +
            itemContent + "\n" +
            "</foreach>";
        return conditionFieldWrapper.wrapConditionText(fieldName, templateText);
    }

    /**
     * Gets in.
     *
     * @return the in
     */
    @NotNull
    protected String getIn() {
        return "in";
    }


}
