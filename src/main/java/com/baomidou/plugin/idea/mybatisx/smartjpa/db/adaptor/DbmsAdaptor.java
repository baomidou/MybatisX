package com.baomidou.plugin.idea.mybatisx.smartjpa.db.adaptor;

import com.intellij.database.Dbms;

public enum DbmsAdaptor {
    MYSQL,
    ORACLE;

    public static DbmsAdaptor castOf(Dbms dbms) {
        if (dbms == Dbms.ORACLE) {
            return ORACLE;
        }
        return MYSQL;
    }
}
