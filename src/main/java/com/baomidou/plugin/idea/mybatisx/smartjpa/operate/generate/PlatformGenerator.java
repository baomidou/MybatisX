package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate;

import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TypeDescriptor;
import com.intellij.psi.PsiMethod;

/**
 * 平台生成器, 为后续生成 注解, springjpa注解等方式预留
 */
public interface PlatformGenerator {
    /**
     * 获取参数
     *
     * @return
     */
    TypeDescriptor getParameter();

    /**
     * 返回值描述符
     *
     * @return
     */
    TypeDescriptor getReturn();

    /**
     * 生成mapper方法
     *
     * @param psiMethod       PSI 方法描述
     * @param methodGenerator 方法生成操作,  自定义实现可以生成 spring jpa, mybatis xml, mybatis 注解
     */
    void generateMapperXml(PsiMethod psiMethod, MybatisXmlGenerator methodGenerator);
}
