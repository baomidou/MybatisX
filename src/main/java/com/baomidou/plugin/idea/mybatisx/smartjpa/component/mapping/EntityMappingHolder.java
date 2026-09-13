package com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping;

import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.intellij.psi.PsiClass;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @authorls9527
 */
@Setter
@Getter
public class EntityMappingHolder {
    private PsiClass entityClass;

    private String tableName;

    private List<TxField> fields;

}
