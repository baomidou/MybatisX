package com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping;


import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.intellij.psi.PsiClass;

import java.util.List;
import java.util.Optional;

/**
 * 自定义的BaseMapper上面, 定义了一个实体. 实体上有jpa的@Table注解
 * <p>
 * 例如: public interface BlogCustomBaseMapper extends CustomBaseMapper<JpaBlog> {}
 * BlogCustomBaseMapper 有 mapper 文件, 但是什么也没配置
 */
public class JpaAnnotationMappingResolver extends JpaMappingResolver implements EntityMappingResolver {


    @Override
    public List<TxField> findFields(PsiClass mapperClass, PsiClass entityClass) {
        return initDataByCamel(entityClass);
    }

    @Override
    public Optional<PsiClass> findEntity(PsiClass mapperClass) {
        return findEntityClassByMapperClass(mapperClass);
    }

    @Override
    public Optional<String> findTableName(PsiClass entityClass) {
        return getTableNameByJpa(entityClass);
    }




}
