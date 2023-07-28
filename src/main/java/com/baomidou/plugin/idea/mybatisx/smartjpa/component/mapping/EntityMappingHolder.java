package com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping;

import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.intellij.psi.PsiClass;
import lombok.Getter;

import java.util.List;

/**
 * @authorls9527
 */
@Getter
public class EntityMappingHolder {
    private PsiClass entityClass;

    private String tableName;

    private List<TxField> fields;

    public void setEntityClass(PsiClass entityClass) {
        this.entityClass = entityClass;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setFields(List<TxField> fields) {
        this.fields = fields;
    }
}
