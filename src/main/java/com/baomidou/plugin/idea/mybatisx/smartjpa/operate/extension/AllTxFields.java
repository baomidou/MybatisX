package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.extension;

/**
 * @author liushang@zsyjr.com
 */

import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 扩展 AllTxField
 */
public class AllTxFields extends TxField {

    private List<TxField> mappingField;
    private PsiClass entityClass;

    public AllTxFields(List<TxField> mappingField, PsiClass entityClass) {
        super();
        this.mappingField = mappingField;
        this.entityClass = entityClass;
    }

    @Override
    public String getTipName() {
        return "ALLFields";
    }

    @Override
    public String getFieldName() {
        return mappingField.stream().map(TxField::getFieldName).collect(Collectors.joining(","));
    }

    @Override
    public @NotNull
    String getColumnName() {
        return mappingField.stream().map(TxField::getColumnName).collect(Collectors.joining(","));
    }

    @Override
    public String getFieldType() {
        return entityClass.getQualifiedName();
    }


}
