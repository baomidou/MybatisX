package com.baomidou.plugin.idea.mybatisx.smartjpa.db.adaptor;

import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.dialect.oracle.OracleGenerateUtil;
import com.intellij.database.model.DasTable;
import com.intellij.database.model.DasTableKey;
import com.intellij.database.util.DasUtil;

import java.util.Optional;

public class DasTableAdaptor implements MxDasTable{
    private DasTable dasTable;

    public Optional<String> findSequenceName(String tableName) {
        return OracleGenerateUtil.findSequenceName(dasTable, tableName);
    }

    public DasTableKey getPrimaryKey() {
        return DasUtil.getPrimaryKey(dasTable);
    }

    public void setDasTable(DasTable dasTable) {
        this.dasTable = dasTable;
    }
}
