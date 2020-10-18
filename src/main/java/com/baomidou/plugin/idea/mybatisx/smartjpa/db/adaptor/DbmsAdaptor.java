package com.baomidou.plugin.idea.mybatisx.smartjpa.db.adaptor;

import com.intellij.database.Dbms;

/**
 * The enum Dbms adaptor.
 */
public enum DbmsAdaptor {
    /**
     * Mysql dbms adaptor.
     */
    MYSQL("NOW()"),
    /**
     * Oracle dbms adaptor.
     */
    ORACLE("SYSDATE");

    private final String defaultDate;

    DbmsAdaptor(String defaultDate) {
        this.defaultDate = defaultDate;
    }

    /**
     * Cast of dbms adaptor.
     *
     * @param dbms the dbms
     * @return the dbms adaptor
     */
    public static DbmsAdaptor castOf(Dbms dbms) {
        if (dbms == Dbms.ORACLE) {
            return ORACLE;
        }
        return MYSQL;
    }

    public String getDefaultDateWord() {
        return defaultDate;
    }
}
