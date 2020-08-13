package com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping;


import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 自定义的BaseMapper上面, 定义了一个实体. 实体上有jpa的@Table注解
 *
 * 例如: public interface BlogCustomBaseMapper extends CustomBaseMapper<JpaBlog> {}
 * BlogCustomBaseMapper 有 mapper 文件, 但是什么也没配置
 */
public class JpaAnnotationMappingResolver extends JpaMappingResolver implements EntityMappingResolver {


    @Override
    public List<TxField> getFields() {
        return fieldList;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public Optional<PsiClass> findEntity(PsiClass mapperClass) {
        Optional<PsiClass> entityClassOption = findEntityClassByMapperClass(mapperClass);
        if (entityClassOption.isPresent()) {
            PsiClass entityClass = entityClassOption.get();
            fieldList = initDataByCamel(entityClass);
            tableName = getTableNameByJpaOrCamel(entityClass);
            return Optional.of(entityClass);
        }
        return Optional.empty();
    }

    /**
     * 字段列表
     */
    private List<TxField> fieldList;
    /**
     * 表名
     */
    private String tableName;




}
