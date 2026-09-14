package com.baomidou.plugin.idea.mybatisx.smartjpa.db.adaptor;

import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.dialect.oracle.OracleGenerateUtil;
import com.intellij.database.model.DasTable;
import com.intellij.database.model.DasTableKey;
import com.intellij.database.util.DasUtil;
import lombok.Setter;

import java.util.Optional;

/**
 * The type Das table adaptor.
 */
@Setter
public class DasTableAdaptor implements MxDasTable {
    /**
     * -- SETTER --
     * Sets das table.
     *
     * @param dasTable the das table
     */
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

}
