package com.baomidou.plugin.idea.mybatisx.dom.converter;

import com.baomidou.plugin.idea.mybatisx.dom.model.IdDomElement;
import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.dom.model.ResultMap;
import com.baomidou.plugin.idea.mybatisx.util.MapperUtils;
import com.google.common.collect.Collections2;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * The type Result map converter.
 *
 * @author yanglin
 */
public class ResultMapConverter extends IdBasedTagConverter {

    @NotNull
    @Override
    public Collection<? extends IdDomElement> getComparisons(@Nullable Mapper mapper, ConvertContext context) {
        if (mapper==null){
            return Collections.emptyList();
        }
        DomElement invocationElement = context.getInvocationElement();
        if (isContextElementOfResultMap(mapper, invocationElement)) {
            return doFilterResultMapItself(mapper, (ResultMap) invocationElement.getParent());
        } else {
            return mapper.getResultMaps();
        }
    }

    private boolean isContextElementOfResultMap(Mapper mapper, DomElement invocationElement) {
        return MapperUtils.isMapperWithSameNamespace(MapperUtils.getMapper(invocationElement), mapper)
            && invocationElement.getParent() instanceof ResultMap;
    }

    private Collection<? extends IdDomElement> doFilterResultMapItself(Mapper mapper, final ResultMap resultMap) {
        return Collections2.filter(mapper.getResultMaps(), input -> !Objects.equals(MapperUtils.getId(input), MapperUtils.getId(resultMap)));
    }
}
