package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.dialect.mysql;

import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.DeleteOperator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.InsertOperator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.SelectOperator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.UpdateOperator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.dialect.BaseDialectManager;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.dialect.CustomStatement;
import com.intellij.psi.PsiClass;

import java.util.List;

/**
 * mysql 方言
 */
public class MysqlManager extends BaseDialectManager {
    public MysqlManager(List<TxField> mappingField, PsiClass entityClass) {
        super(mappingField, entityClass);
    }

    @Override
    protected void init(List<TxField> mappingField, PsiClass entityClass) {
        this.registerManagers(new SelectOperator(mappingField, entityClass));

        this.registerManagers(new InsertOperator(mappingField) {
            @Override
            protected void initCustomArea(String areaName, List<TxField> mappingField) {
                super.initCustomArea(areaName, mappingField);
                CustomStatement customStatement = new MysqlInsertBatch(areaName, mappingField);
                this.registerStatementBlock(customStatement.getStatementBlock());
                this.addOperatorName(customStatement.operatorName());
            }
        });

        this.registerManagers(new UpdateOperator(mappingField));
        this.registerManagers(new DeleteOperator(mappingField));
    }
}
