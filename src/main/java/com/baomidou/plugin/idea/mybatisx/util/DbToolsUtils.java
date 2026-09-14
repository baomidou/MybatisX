package com.baomidou.plugin.idea.mybatisx.util;

import com.baomidou.plugin.idea.mybatisx.generate.plugin.helper.IntellijColumnInfo;
import com.baomidou.plugin.idea.mybatisx.generate.plugin.helper.IntellijTableInfo;
import com.intellij.database.model.DasColumn;
import com.intellij.database.model.DasTableKey;
import com.intellij.database.model.DasTypedObject;
import com.intellij.database.model.DataType;
import com.intellij.database.model.MultiRef;
import com.intellij.database.psi.DbTable;
import com.intellij.database.util.DasUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.containers.JBIterable;
import com.rits.cloning.Cloner;
import org.jetbrains.annotations.NotNull;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The type Db tools utils.
 */
public class DbToolsUtils {
    private static final Cloner MY_CLONER = new Cloner();

    /**
     * Build intellij table info intellij table info.
     *
     * @param currentTable the current table
     * @return the intellij table info
     */
    public static IntellijTableInfo buildIntellijTableInfo(@NotNull DbTable currentTable) {
        IntellijTableInfo tableInfo = new IntellijTableInfo();
        tableInfo.setTableName(currentTable.getName());
        tableInfo.setDatabaseType(extractDatabaseTypeFromUrl(currentTable));
        tableInfo.setTableRemark(currentTable.getComment());
        tableInfo.setTableType(currentTable.getTypeName());

        // 1. 转换所有列信息
        List<IntellijColumnInfo> allColumns = convertAllColumns(currentTable, tableInfo.getDatabaseType());
        tableInfo.setColumnInfos(allColumns);

        // 2. 构建主键列信息（使用 Map 提高查找效率）
        List<IntellijColumnInfo> primaryColumns = buildPrimaryKeyColumns(currentTable, allColumns);
        tableInfo.setPrimaryKeyColumns(primaryColumns);

        return tableInfo;
    }

    private static List<IntellijColumnInfo> buildPrimaryKeyColumns(@NotNull DbTable currentTable, List<IntellijColumnInfo> allColumns) {
        DasTableKey primaryKey = DasUtil.getPrimaryKey(currentTable);
        if (primaryKey == null) {
            return Collections.emptyList();
        }

        Map<String, IntellijColumnInfo> columnNameMap = allColumns.stream()
            .collect(Collectors.toMap(
                IntellijColumnInfo::getName,
                Function.identity(),
                (existing, replacement) -> existing // 重名留存第一个
            ));

        MultiRef<? extends DasTypedObject> columnsRef = primaryKey.getColumnsRef();
        MultiRef.It<? extends DasTypedObject> iterate = columnsRef.iterate();

        List<IntellijColumnInfo> primaryColumnInfos = new ArrayList<>();
        short seq = 0;
        while (iterate.hasNext()) {
            String columnName = iterate.next();
            IntellijColumnInfo columnInfo = columnNameMap.get(columnName);
            if (columnInfo != null) {
                IntellijColumnInfo info = MY_CLONER.deepClone(columnInfo);
                info.setKeySeq(seq++);
                primaryColumnInfos.add(info);
            }
        }

        return primaryColumnInfos;
    }

    @NotNull
    private static List<IntellijColumnInfo> convertAllColumns(@NotNull DbTable currentTable, String databaseType) {
        JBIterable<? extends DasColumn> columns = DasUtil.getColumns(currentTable);
        if (columns == null || columns.isEmpty()) {
            return Collections.emptyList();
        }
        List<IntellijColumnInfo> result = new ArrayList<>();
        for (DasColumn column : columns) {
            IntellijColumnInfo columnInfo = convertColumnToIntellijColumnInfo(column, databaseType);
            result.add(columnInfo);
        }

        return result;
    }


    private static String extractDatabaseTypeFromUrl(DbTable currentTable) {
        String url = currentTable.getDataSource().getConnectionConfig().getUrl();
        return extractDatabaseTypeFromUrl(url);
    }

    /**
     * Extract database type from url string.
     *
     * @param url the url
     * @return the string
     */
    public static String extractDatabaseTypeFromUrl(String url) {
        if (url != null) {
            url = url.toLowerCase();
            if (url.contains(":mysql")) {
                return "MySql";
            }
            if (url.contains(":oracle")) {
                return "Oracle";
            }
            if (url.contains(":postgresql")) {
                return "PostgreSQL";
            }
            if (url.contains(":sqlserver")) {
                return "SqlServer";
            }
            if (url.contains(":sqlite")) {
                return "Sqlite";
            }
        }
        return "";
    }

    /**
     * Convert column to intellij column info intellij column info.
     *
     * @param column       the column
     * @param databaseType the database type
     * @return the intellij column info
     */
    @NotNull
    public static IntellijColumnInfo convertColumnToIntellijColumnInfo(DasColumn column, String databaseType) {
        IntellijColumnInfo columnInfo = new IntellijColumnInfo();
        columnInfo.setName(column.getName());
        DataType dataType = column.getDasType().toDataType();
        columnInfo.setDataType(convertTypeNameToJdbcType(dataType.typeName, databaseType));
        if (DasUtil.isAutoGenerated(column)) {
            columnInfo.setGeneratedColumn(true);
        }

        if (DasUtil.isAutoGenerated(column)) {
            columnInfo.setAutoIncrement(true);
        }

        columnInfo.setSize(dataType.getLength());
        columnInfo.setDecimalDigits(dataType.getScale());
        columnInfo.setRemarks(column.getComment());
        columnInfo.setColumnDefaultValue(column.getDefault());
        columnInfo.setNullable(!column.isNotNull());
        columnInfo.setKeySeq(column.getPosition());

        return columnInfo;
    }

    /**
     * 根据mapper jdbcType 字面量转换 java.sql.Types 常量
     *
     * @param jdbcTypeName jdbcType 字面量
     * @param databaseType 数据库类型
     * @return java.sql.Types 常量
     */
    public static int convertTypeNameToJdbcType(String jdbcTypeName, String databaseType) {
        if (StringUtil.isEmpty(jdbcTypeName)) {
            return Types.OTHER;
        }

        String fixed = jdbcTypeName.toUpperCase();
        boolean isOracle = "Oracle".equalsIgnoreCase(databaseType);

        // 优先精确匹配（避免误判，如 "INT" 被 "BIGINT" 先捕获）
        if (fixed.equals("CHAR")) {
            return Types.CHAR;
        }

        // 按优先级顺序匹配（越特殊越靠前）
        if (fixed.contains("BIGINT")) return Types.BIGINT;
        if (fixed.contains("TINYINT")) return Types.TINYINT;
        if (fixed.contains("SMALLINT")) return Types.SMALLINT;
        if (fixed.contains("INT")) return Types.INTEGER;  // 放在 BIGINT/TINYINT/SMALLINT 之后

        if (fixed.contains("LONGVARBINARY")) return Types.LONGVARBINARY;
        if (fixed.contains("VARBINARY")) return Types.VARBINARY;
        if (fixed.contains("BINARY")) return Types.VARBINARY;

        if (fixed.contains("LONGVARCHAR")) return Types.LONGVARCHAR;
        if (fixed.contains("VARCHAR")) return Types.VARCHAR;

        if (fixed.contains("DATETIME")) return Types.TIMESTAMP;
        if (isOracle && fixed.contains("DATE")) return Types.TIMESTAMP;  // Oracle DATE 含时间
        if (fixed.contains("TIMESTAMP")) return Types.TIMESTAMP;
        if (fixed.contains("DATE")) return Types.DATE;
        if (fixed.contains("TIME")) return Types.TIME;

        if (fixed.contains("DECIMAL")) return Types.DECIMAL;
        if (fixed.contains("NUMERIC")) return Types.NUMERIC;
        if (fixed.contains("NUMBER")) return Types.DECIMAL;  // Oracle/DB2 通用

        if (fixed.contains("FLOAT")) return Types.FLOAT;
        if (fixed.contains("DOUBLE")) return Types.DOUBLE;
        if (fixed.contains("REAL")) return Types.REAL;

        if (fixed.contains("BOOLEAN") || fixed.contains("BOOL")) return Types.BOOLEAN;
        if (fixed.contains("BIT")) return Types.BIT;

        if (fixed.contains("BLOB")) return Types.BLOB;
        if (fixed.contains("CLOB")) return Types.CLOB;

        if (fixed.contains("REFERENCE")) return Types.REF;

        // 兜底：如果包含 CHAR/TEXT 但未被上述捕获（如 NCHAR, NTEXT 等），归为 VARCHAR
        if (fixed.contains("CHAR") || fixed.contains("TEXT")) {
            return Types.VARCHAR;
        }

        return Types.OTHER;
    }
}
