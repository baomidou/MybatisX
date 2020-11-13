package com.baomidou.plugin.idea.mybatisx.definitionsearch;

import com.baomidou.plugin.idea.mybatisx.service.JavaService;
import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiTypeParameterListOwner;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.Processor;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;

/**
 * <p>
 * 定义 Mapper 搜索
 * </p>
 *
 * @author yanglin
 * @since 2018 -08-05
 */
public class MapperDefinitionSearch extends QueryExecutorBase<XmlElement, PsiElement> {

    /**
     * Instantiates a new Mapper definition search.
     */
    public MapperDefinitionSearch() {
        super(true);
    }

    @Override
    public void processQuery(@NotNull PsiElement queryParameters, @NotNull Processor<? super XmlElement> consumer) {
        if (!(queryParameters instanceof PsiTypeParameterListOwner)) {
            return;
        }
        Processor<DomElement> processor = domElement -> consumer.process(domElement.getXmlElement());
        JavaService.getInstance(queryParameters.getProject()).process(queryParameters, processor);
    }
}
