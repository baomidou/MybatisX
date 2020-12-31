package com.baomidou.plugin.idea.mybatisx.tip.hashmark;

import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * tip jdbc type
 *
 * @author ls9527
 */
public class JdbcTypeHashMarkTip implements HashMarkTip {
    @Override
    public String getName() {
        return "jdbcType";
    }

    @Override
    public void tipValue(CompletionResultSet completionResultSet, Mapper mapper) {
        CompletionResultSet insensitiveResultSet = completionResultSet.caseInsensitive();
        for (JdbcType value : JdbcType.values()) {
            insensitiveResultSet.addElement(LookupElementBuilder.create(value.name()));
        }
    }

    /**
     * 为什么要复制过来呢，因为写起来方便 <br/>
     * copy from org.apache.ibatis.type.JdbcType
     */
    enum JdbcType {
        /*
         * This is added to enable basic support for the
         * ARRAY data type - but a custom type handler is still required
         */
        ARRAY(Types.ARRAY),
        BIT(Types.BIT),
        TINYINT(Types.TINYINT),
        SMALLINT(Types.SMALLINT),
        INTEGER(Types.INTEGER),
        BIGINT(Types.BIGINT),
        FLOAT(Types.FLOAT),
        REAL(Types.REAL),
        DOUBLE(Types.DOUBLE),
        NUMERIC(Types.NUMERIC),
        DECIMAL(Types.DECIMAL),
        CHAR(Types.CHAR),
        VARCHAR(Types.VARCHAR),
        LONGVARCHAR(Types.LONGVARCHAR),
        DATE(Types.DATE),
        TIME(Types.TIME),
        TIMESTAMP(Types.TIMESTAMP),
        BINARY(Types.BINARY),
        VARBINARY(Types.VARBINARY),
        LONGVARBINARY(Types.LONGVARBINARY),
        NULL(Types.NULL),
        OTHER(Types.OTHER),
        BLOB(Types.BLOB),
        CLOB(Types.CLOB),
        BOOLEAN(Types.BOOLEAN),
        CURSOR(-10), // Oracle
        UNDEFINED(Integer.MIN_VALUE + 1000),
        NVARCHAR(Types.NVARCHAR), // JDK6
        NCHAR(Types.NCHAR), // JDK6
        NCLOB(Types.NCLOB), // JDK6
        STRUCT(Types.STRUCT),
        JAVA_OBJECT(Types.JAVA_OBJECT),
        DISTINCT(Types.DISTINCT),
        REF(Types.REF),
        DATALINK(Types.DATALINK),
        ROWID(Types.ROWID), // JDK6
        LONGNVARCHAR(Types.LONGNVARCHAR), // JDK6
        SQLXML(Types.SQLXML), // JDK6
        DATETIMEOFFSET(-155), // SQL Server 2008
        TIME_WITH_TIMEZONE(Types.TIME_WITH_TIMEZONE), // JDBC 4.2 JDK8
        TIMESTAMP_WITH_TIMEZONE(Types.TIMESTAMP_WITH_TIMEZONE); // JDBC 4.2 JDK8

        public final int TYPE_CODE;
        private static Map<Integer, JdbcType> codeLookup = new HashMap<>();

        static {
            for (JdbcType type : JdbcType.values()) {
                codeLookup.put(type.TYPE_CODE, type);
            }
        }

        JdbcType(int code) {
            this.TYPE_CODE = code;
        }

        public static JdbcType forCode(int code) {
            return codeLookup.get(code);
        }

    }
}
