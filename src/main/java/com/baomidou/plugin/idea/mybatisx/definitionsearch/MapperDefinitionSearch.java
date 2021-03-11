package com.baomidou.plugin.idea.mybatisx.definitionsearch;

import com.baomidou.plugin.idea.mybatisx.dom.model.IdDomElement;
import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.util.MapperUtils;
import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
        if (!(queryParameters instanceof PsiMethod)) {
            return;
        }
        PsiMethod psiMethod = (PsiMethod) queryParameters;
        PsiClass psiClass = psiMethod.getContainingClass();
        if (null == psiClass) {
            return;
        }
        String id = psiClass.getQualifiedName() + "." + psiMethod.getName();
        Collection<Mapper> mappers = MapperUtils.findMappers(psiMethod.getProject());

        final List<IdDomElement> elementList = mappers.stream()
            .flatMap(mapper -> mapper.getDaoElements().stream())
            .filter(idDom -> MapperUtils.getIdSignature(idDom).equals(id))
            .collect(Collectors.toList());

        for (IdDomElement idDomElement : elementList) {
            consumer.process(idDomElement.getXmlElement());
        }
    }
}
