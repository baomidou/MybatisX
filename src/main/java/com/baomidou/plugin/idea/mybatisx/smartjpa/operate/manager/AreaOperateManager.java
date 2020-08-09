package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager;

import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.completion.parameter.MxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.completion.res.ReturnWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.ui.MapperTagInfo;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public interface AreaOperateManager {
    /**
     * 将字符串分割成 符号追加器
     *
     * @param splitParam
     * @return
     */
    @NotNull
    LinkedList<SyntaxAppender> getJpaList(String splitParam);

    /**
     * 获取生成的内容
     *
     * @param splitList
     * @return
     */
    Set<String> getCompletionContent(LinkedList<SyntaxAppender> splitList);

    List<MxParameter> getParameters(PsiClass entityClass, LinkedList<SyntaxAppender> jpaStringList);

    ReturnWrapper getReturnWrapper(String text, PsiClass entityClass, LinkedList<SyntaxAppender> linkedList);

    boolean support(String text);

    /**
     *
     * @param jpaList 输入的文本
     * @param entityClass 类
     * @param psiMethod 方法
     * @param tableName 表名
     * @return
     */
    MapperTagInfo generateMapperXml(LinkedList<SyntaxAppender> jpaList, PsiClass entityClass, PsiMethod psiMethod, String tableName);

}
