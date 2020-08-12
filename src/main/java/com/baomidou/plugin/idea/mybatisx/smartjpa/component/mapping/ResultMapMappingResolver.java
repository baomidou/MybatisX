package com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping;

import com.baomidou.plugin.idea.mybatisx.dom.model.Id;
import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.dom.model.Result;
import com.baomidou.plugin.idea.mybatisx.dom.model.ResultMap;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.StringUtils;
import com.baomidou.plugin.idea.mybatisx.util.MapperUtils;
import com.google.common.base.Optional;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.util.xml.GenericAttributeValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 根据xml文件的 BaseResultMap 获取字段对应的 列名
 */
public class ResultMapMappingResolver implements EntityMappingResolver {

    @Override
    public List<TxField> getFields() {
        return fieldList;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    private String tableName;

    private List<TxField> fieldList;

    @Override
    public java.util.Optional<PsiClass> findEntity(PsiClass mapperClass) {
        Optional<Mapper> firstMapper = MapperUtils.findFirstMapper(mapperClass.getProject(), mapperClass);
        if (firstMapper.isPresent()) {
            Mapper mapper = firstMapper.get();
            Optional<ResultMap> resultMapOpt = findResultMap(mapper.getResultMaps());
            if (resultMapOpt.isPresent()) {
                ResultMap resultMap = resultMapOpt.get();
                GenericAttributeValue<PsiClass> type = resultMap.getType();
                // 实体类的名字
                PsiClass entityClass = type.getValue();
                tableName = entityClass.getQualifiedName();

                List<TxField> txFields = new ArrayList<>();
                txFields.addAll(determineIds(resultMap.getIds(), entityClass));
                txFields.addAll(determineResults(resultMap.getResults(), entityClass));
                this.fieldList = txFields;
                return java.util.Optional.of(entityClass);
            }
        }
        return java.util.Optional.empty();
    }

    private Collection<? extends TxField> determineResults(List<Result> results, PsiClass mapperClass) {
        return results.stream().map(result -> {
            String column = result.getColumn().getStringValue();
            String property = result.getProperty().getStringValue();
            PsiField field = mapperClass.findFieldByName(property, true);
            if (field != null) {
                TxField txField = new TxField();
                txField.setColumnName(column);
                txField.setFieldName(field.getName());
                txField.setFieldType(field.getType().getCanonicalText());
                txField.setTipName(StringUtils.upperCaseFirstChar(field.getName()));
                return txField;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private Collection<? extends TxField> determineIds(List<Id> ids, PsiClass mapperClass) {
        return ids.stream().map(id -> {
            String column = id.getColumn().getStringValue();
            String property = id.getProperty().getStringValue();
            PsiField field = mapperClass.findFieldByName(property, true);
            if (field != null) {
                TxField txField = new TxField();
                txField.setColumnName(column);
                txField.setFieldName(field.getName());
                txField.setFieldType(field.getType().getCanonicalText());
                txField.setTipName(StringUtils.upperCaseFirstChar(field.getName()));
                return txField;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 找到名字最短的 ResultMap
     *
     * @param resultMaps
     * @return
     */
    private Optional<ResultMap> findResultMap(List<ResultMap> resultMaps) {
        if (resultMaps.size() == 1) {
            return Optional.fromNullable(resultMaps.get(0));
        }
        ResultMap mostShortResultMap = null;
        for (ResultMap resultMap : resultMaps) {
            String currentMapId = resultMap.getId().getStringValue();
            if (mostShortResultMap == null) {
                mostShortResultMap = resultMap;
            } else {
                String shortName = mostShortResultMap.getId().getStringValue();
                if (shortName.length() >= currentMapId.length()) {
                    mostShortResultMap = resultMap;
                }
            }
        }
        return Optional.fromNullable(mostShortResultMap);
    }
}
