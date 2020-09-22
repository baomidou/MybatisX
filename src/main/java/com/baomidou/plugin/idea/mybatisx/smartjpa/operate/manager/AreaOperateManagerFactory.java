package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager;

import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.db.adaptor.DasTableAdaptor;
import com.baomidou.plugin.idea.mybatisx.smartjpa.db.adaptor.DbmsAdaptor;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.dialect.mysql.MysqlManager;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.dialect.oracle.OracleManager;
import com.intellij.psi.PsiClass;

import java.util.List;

/**
 * The type Area operate manager factory.
 */
public class AreaOperateManagerFactory {

    /**
     * Gets by dbms.
     *
     * @param dbms         the dbms
     * @param mappingField the mapping field
     * @param entityClass  the entity class
     * @param dasTable     the das table
     * @param tableName    the table name
     * @return the by dbms
     */
    public static AreaOperateManager getByDbms(DbmsAdaptor dbms,
                                               List<TxField> mappingField,
                                               PsiClass entityClass,
                                               DasTableAdaptor dasTable,
                                               String tableName) {
        if (dbms == DbmsAdaptor.ORACLE) {
            return new OracleManager(mappingField, entityClass, dasTable, tableName);
        }
        return new MysqlManager(mappingField, entityClass);
    }
}
