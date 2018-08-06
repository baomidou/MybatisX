package com.baomidou.plugin.idea.mybatisx.definitionsearch;

import org.jetbrains.annotations.NotNull;

import com.baomidou.plugin.idea.mybatisx.service.JavaService;
import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiTypeParameterListOwner;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.Processor;
import com.intellij.util.xml.DomElement;

/**
 * <p>
 * 定义 Mapper 搜索
 * </p>
 *
 * @author yanglin
 * @since 2018-08-05
 */
public class MapperDefinitionSearch extends QueryExecutorBase<XmlElement, PsiElement> {

    public MapperDefinitionSearch() {
        super(true);
    }

    @Override
    public void processQuery(@NotNull PsiElement element, @NotNull final Processor<XmlElement> consumer) {

        if (!(element instanceof PsiTypeParameterListOwner)) {
            return;
        }

        Processor<DomElement> processor = domElement -> consumer.process(domElement.getXmlElement());
        JavaService.getInstance(element.getProject()).process(element, processor);
    }
}
