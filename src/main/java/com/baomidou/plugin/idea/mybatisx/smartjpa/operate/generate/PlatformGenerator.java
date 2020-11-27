package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate;

import com.baomidou.plugin.idea.mybatisx.smartjpa.common.MapperClassGenerateFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest.ConditionFieldWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TypeDescriptor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.List;

/**
 * 平台生成器, 为后续生成 注解, springjpa注解等方式预留
 */
public interface PlatformGenerator {
    String getDefaultDateWord();

    /**
     * 获取参数
     *
     * @return parameter
     */
    TypeDescriptor getParameter();

    /**
     * 返回值描述符
     *
     * @return return
     */
    TypeDescriptor getReturn();

    /**
     * 生成mapper方法
     * @param mapperClassGenerateFactory             PSI 方法描述
     * @param psiMethod
     * @param conditionFieldWrapper the condition field wrapper
     * @param resultFields
     */
    void generateMapperXml(MapperClassGenerateFactory mapperClassGenerateFactory,
                           PsiMethod psiMethod,
                           ConditionFieldWrapper conditionFieldWrapper,
                           List<TxField> resultFields);

    /**
     * Gets condition fields.
     *
     * @return the condition fields
     */
    List<String> getConditionFields();

    /**
     * Gets all fields.
     *
     * @return the all fields
     */
    List<TxField> getAllFields();

    /**
     * Gets entity class.
     *
     * @return the entity class
     */
    PsiClass getEntityClass();

    List<String>  getResultFields();
}
