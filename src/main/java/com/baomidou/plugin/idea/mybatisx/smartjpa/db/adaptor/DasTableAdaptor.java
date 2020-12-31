package com.baomidou.plugin.idea.mybatisx.smartjpa.db.adaptor;

import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.dialect.oracle.OracleGenerateUtil;
import com.intellij.database.model.DasTable;
import com.intellij.database.model.DasTableKey;
import com.intellij.database.util.DasUtil;

import java.util.Optional;

/**
 * The type Das table adaptor.
 */
public class DasTableAdaptor implements MxDasTable {
    private DasTable dasTable;

    /**
     * Find sequence name optional.
     *
     * @param tableName the table name
     * @return the optional
     */
    public Optional<String> findSequenceName(String tableName) {
        return OracleGenerateUtil.findSequenceName(dasTable, tableName);
    }

    /**
     * Gets primary key.
     *
     * @return the primary key
     */
    public DasTableKey getPrimaryKey() {
        return DasUtil.getPrimaryKey(dasTable);
    }

    /**
     * Sets das table.
     *
     * @param dasTable the das table
     */
    public void setDasTable(DasTable dasTable) {
        this.dasTable = dasTable;
    }
}
