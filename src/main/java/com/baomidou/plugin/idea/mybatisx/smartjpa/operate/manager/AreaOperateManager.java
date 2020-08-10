package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager;

import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxReturnDescriptor;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.MybatisXmlGenerator;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public interface AreaOperateManager {
    /**
     * 将字符串分割成 符号追加器
     *
     * @param splitParam
     * @return
     */
    @NotNull
    LinkedList<SyntaxAppender> splitAppenderByText(String splitParam);

    /**
     * 获取生成的内容
     *
     * @param splitList
     * @return
     */
    List<String> getCompletionContent(LinkedList<SyntaxAppender> splitList);

    List<String> getCompletionContent();

    List<TxParameter> getParameters(PsiClass entityClass, LinkedList<SyntaxAppender> jpaStringList);

    TxReturnDescriptor getReturnWrapper(String text, PsiClass entityClass, LinkedList<SyntaxAppender> linkedList);

    boolean support(String text);

    /**
     *
     *
     * @param id
     * @param jpaList 输入的文本
     * @param entityClass 类
     * @param psiMethod 方法
     * @param tableName 表名
     * @param mybatisXmlGenerator
     */
    void generateMapperXml(String id, LinkedList<SyntaxAppender> jpaList, PsiClass entityClass, PsiMethod psiMethod, String tableName, MybatisXmlGenerator mybatisXmlGenerator);

}
