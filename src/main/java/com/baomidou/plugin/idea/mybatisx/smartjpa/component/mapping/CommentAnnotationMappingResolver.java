package com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping;


import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * 在接口上加入注释, 注释内部加入注解:  @Entity com.xx.xx.User
 */
public class CommentAnnotationMappingResolver extends JpaMappingResolver implements EntityMappingResolver {


    /**
     * The constant TABLE_ENTITY.
     */
    public static final String TABLE_ENTITY = "Entity";

    @Override
    public List<TxField> findFields(PsiClass mapperClass, PsiClass entityClass) {
        return initDataByCamel(entityClass);
    }

    @Override
    public Optional<PsiClass> findEntity(PsiClass mapperClass) {
        PsiDocComment docComment = mapperClass.getDocComment();
        if (docComment != null) {
            PsiDocTag tableEntity = docComment.findTagByName(TABLE_ENTITY);
            if (tableEntity != null) {
                String entityText = tableEntity.getValueElement().getText();
                if (StringUtils.isNotBlank(entityText)) {
                    JavaPsiFacade instance = JavaPsiFacade.getInstance(mapperClass.getProject());
                    PsiClass entityClass = instance.findClass(entityText, mapperClass.getResolveScope());
                    if (entityClass != null) {
                        return Optional.of(entityClass);
                    }
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<String> findTableName(PsiClass entityClass) {
        return  getTableNameByJpa(entityClass);
    }




}
