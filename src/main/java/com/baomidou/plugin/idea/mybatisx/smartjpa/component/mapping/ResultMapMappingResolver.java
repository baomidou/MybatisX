package com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping;

import com.baomidou.plugin.idea.mybatisx.dom.model.Id;
import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.dom.model.Result;
import com.baomidou.plugin.idea.mybatisx.dom.model.ResultMap;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.JdbcTypeUtils;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.util.MapperUtils;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

/**
 * 根据xml文件的 BaseResultMap 获取字段对应的 列名
 */
public class ResultMapMappingResolver extends JpaMappingResolver implements EntityMappingResolver {

    //
    private static final String RESULT_MAP_WITH_BLOBS = "ResultMapWithBLOBs";
    //
    private static final String BASE_RESULT_MAP = "BaseResultMap";
    private Project project;

    public ResultMapMappingResolver(Project project) {
        super();
        this.project = project;
    }

    @Override
    public List<TxField> findFields(PsiClass mapperClass, PsiClass entityClassParam) {
        Optional<Mapper> firstMapper = MapperUtils.findFirstMapper(project, mapperClass);
        if (firstMapper.isPresent()) {
            Mapper mapper = firstMapper.get();
            Optional<ResultMap> resultMapOpt = findResultMap(mapper.getResultMaps());
            if (resultMapOpt.isPresent()) {
                ResultMap resultMap = resultMapOpt.get();


                GenericAttributeValue<PsiClass> type = resultMap.getType();
                // 实体类的名字
                PsiClass entityClass = type.getValue();
                if (entityClass == null) {
                    entityClass = entityClassParam;
                }

                List<TxField> txFields = new ArrayList<>();
                txFields.addAll(determineIds(entityClass, resultMap.getIds()));
                txFields.addAll(determineResults(resultMap.getResults(), entityClass));
                addExtends(txFields, resultMap, entityClass, mapper.getResultMaps());
                return txFields;
            }
        }
        return Collections.emptyList();
    }


    @Override
    public Optional<PsiClass> findEntity(PsiClass mapperClass) {
        Optional<Mapper> firstMapper = MapperUtils.findFirstMapper(project, mapperClass);
        if (firstMapper.isPresent()) {
            Mapper mapper = firstMapper.get();
            Optional<ResultMap> resultMapOpt = findResultMap(mapper.getResultMaps());
            if (resultMapOpt.isPresent()) {
                ResultMap resultMap = resultMapOpt.get();
                GenericAttributeValue<PsiClass> type = resultMap.getType();
                // 实体类的名字
                return Optional.ofNullable(type.getValue());
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> findTableName(PsiClass entityClass) {
        return Optional.empty();
    }


    /**
     * 添加所有父类的 ResultMap
     *
     * @param txFields
     * @param currentResultMap
     * @param entityClass
     * @param resultMaps
     */
    private void addExtends(List<TxField> txFields, ResultMap currentResultMap, PsiClass entityClass, List<ResultMap> resultMaps) {
        GenericAttributeValue<XmlAttributeValue> anExtends = currentResultMap.getExtends();
        String stringValue = anExtends.getStringValue();
        if (StringUtils.isEmpty(stringValue)) {
            return;
        }
        ResultMap foundResultMap = null;
        for (ResultMap resultMap : resultMaps) {
            if (resultMap.getId().getStringValue().equals(stringValue)) {
                foundResultMap = resultMap;
                break;
            }
        }
        if (foundResultMap != null) {
            txFields.addAll(determineIds(entityClass, foundResultMap.getIds()));
            txFields.addAll(determineResults(foundResultMap.getResults(), entityClass));
            addExtends(txFields, foundResultMap, entityClass, resultMaps);
        }
    }

    private Collection<? extends TxField> determineResults(List<Result> results, PsiClass mapperClass) {
        return results.stream().map(result -> determineField(mapperClass, result.getProperty(), result.getXmlTag())).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Nullable
    private TxField determineField(PsiClass entityClass, GenericAttributeValue<XmlAttributeValue> property, final XmlTag xmlTag) {
        String propertyValue = property.getStringValue();
        PsiField field = entityClass.findFieldByName(propertyValue, true);

        String column = null;
        if (xmlTag != null) {
            XmlAttribute columnAttr = xmlTag.getAttribute("column");
            if (columnAttr != null) {
                column = columnAttr.getValue();
            }
        }
        if (field != null) {
            if (column == null) {
                column = getColumnNameByJpaOrCamel(field);
            }
            TxField txField = new TxField();
            txField.setColumnName(column);
            txField.setFieldName(field.getName());
            txField.setFieldType(field.getType().getCanonicalText());
            txField.setTipName(StringUtils.upperCaseFirstChar(field.getName()));
            txField.setClassName(field.getContainingClass().getQualifiedName());
            Optional<String> jdbcTypeByJavaType = JdbcTypeUtils.findJdbcTypeByJavaType(field.getType().getCanonicalText());
            jdbcTypeByJavaType.ifPresent(txField::setJdbcType);
            return txField;
        }
        return null;
    }

    /**
     * resultMap 的子标签<id/>
     *
     * @param mapperClass
     * @param ids
     * @return
     */
    private Collection<? extends TxField> determineIds(PsiClass mapperClass, List<Id> ids) {
        return ids.stream().map(id -> getTxField(mapperClass, id)).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * resultMap 的 id
     *
     * @param mapperClass
     * @param id
     * @return
     */
    @Nullable
    private TxField getTxField(PsiClass mapperClass, Id id) {
        return determineField(mapperClass, id.getProperty(), id.getXmlTag());
    }

    /**
     * 找到名字最短的 ResultMap
     *
     * @param resultMaps
     * @return
     */
    private Optional<ResultMap> findResultMap(List<ResultMap> resultMaps) {
        if (resultMaps.size() == 1) {
            return Optional.ofNullable(resultMaps.get(0));
        }
        // 字符串长度维度比较
        Map<String, ResultMap> allResultMaps = resultMaps.stream().
            collect(Collectors.toMap(k -> {
                    String stringValue = k.getId().getStringValue();
                    return stringValue == null ? "" : stringValue.toUpperCase();
                },
                v -> v,
                BinaryOperator.maxBy(Comparator.comparing(k -> k.getId().getStringValue()))));
        // find ResultMapWithBLOBs
        ResultMap resultMap = allResultMaps.get(RESULT_MAP_WITH_BLOBS.toUpperCase());
        // find BaseResultMap
        if (resultMap == null) {
            resultMap = allResultMaps.get(BASE_RESULT_MAP.toUpperCase());
        }
        // 短名称优先
        if (resultMap == null) {
            for (ResultMap resultMapItem : resultMaps) {
                String currentMapId = resultMapItem.getId().getStringValue();
                if (resultMap == null) {
                    resultMap = resultMapItem;
                } else {
                    String shortName = resultMap.getId().getStringValue();
                    assert shortName != null;
                    assert currentMapId != null;
                    if (shortName.length() >= currentMapId.length()) {
                        resultMap = resultMapItem;
                    }
                }
            }
        }
        return Optional.ofNullable(resultMap);
    }
}
