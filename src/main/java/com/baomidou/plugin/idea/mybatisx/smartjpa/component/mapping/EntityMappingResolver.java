package com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping;

import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.intellij.psi.PsiClass;

import java.util.List;
import java.util.Optional;

/**
 * 实体映射解析器
 */
public interface EntityMappingResolver {

    /**
     * 获取所有字段
     *
     * @param mapperClass
     * @param entityClass
     * @return fields
     */
    List<TxField> findFields(PsiClass mapperClass, PsiClass entityClass);

    /**
     * 支持 mapper 类
     *
     * @param mapperClass the mapper class
     * @return optional
     */
    Optional<PsiClass> findEntity(PsiClass mapperClass);

    /**
     * @param entityClass
     * @return
     */
    Optional<String> findTableName(PsiClass entityClass);


}
