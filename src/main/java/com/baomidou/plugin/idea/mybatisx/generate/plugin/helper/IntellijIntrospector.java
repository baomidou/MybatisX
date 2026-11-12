package com.baomidou.plugin.idea.mybatisx.generate.plugin.helper;


import com.baomidou.plugin.idea.mybatisx.generate.typemapping.TypeMappingEngine;
import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.JavaTypeResolver;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaReservedWords;
import org.mybatis.generator.config.ColumnOverride;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.internal.ObjectFactory;
import org.mybatis.generator.internal.db.ActualTableName;
import org.mybatis.generator.internal.db.SqlReservedWords;
import org.mybatis.generator.internal.util.JavaBeansUtil;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.internal.util.messages.Messages;
import org.mybatis.generator.logging.Log;
import org.mybatis.generator.logging.LogFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntellijIntrospector {
    private final IntellijTableInfo intellijTableInfo;
    private final JavaTypeResolver javaTypeResolver;
    private final List<String> warnings;
    private final Context context;
    private final Log logger;

    public IntellijIntrospector(Context context, JavaTypeResolver javaTypeResolver, List<String> warnings, IntellijTableInfo tableInfo) {
        this.context = context;
        this.intellijTableInfo = tableInfo;
        this.javaTypeResolver = javaTypeResolver;
        this.warnings = warnings;
        this.logger = LogFactory.getLog(this.getClass());
    }

    private void calculatePrimaryKey(FullyQualifiedTable table, IntrospectedTable introspectedTable) {
        Map<Short, String> keyColumns = new TreeMap<>();
        List<IntellijColumnInfo> primaryKeyColumns = this.intellijTableInfo.getPrimaryKeyColumns();

        for (IntellijColumnInfo primaryKeyColumn : primaryKeyColumns) {
            String columnName = primaryKeyColumn.getName();
            short keySeq = primaryKeyColumn.getKeySeq();
            keyColumns.put(keySeq, columnName);
        }

        for (String columnName : keyColumns.values()) {
            introspectedTable.addPrimaryKeyColumn(columnName);
        }

    }

    private void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ignored) {
            }
        }

    }

    private void reportIntrospectionWarnings(IntrospectedTable introspectedTable, TableConfiguration tableConfiguration, FullyQualifiedTable table) {

        for (ColumnOverride columnOverride : tableConfiguration.getColumnOverrides()) {
            if (introspectedTable.getColumn(columnOverride.getColumnName()).isEmpty()) {
                this.warnings.add(Messages.getString("Warning.3", columnOverride.getColumnName(), table.toString()));
            }
        }

        for (String string : tableConfiguration.getIgnoredColumnsInError()) {
            this.warnings.add(Messages.getString("Warning.4", string, table.toString()));
        }

        GeneratedKey generatedKey = tableConfiguration.getGeneratedKey();
        if (generatedKey != null && introspectedTable.getColumn(generatedKey.getColumn()).isEmpty()) {
            if (generatedKey.isIdentity()) {
                this.warnings.add(Messages.getString("Warning.5", generatedKey.getColumn(), table.toString()));
            } else {
                this.warnings.add(Messages.getString("Warning.6", generatedKey.getColumn(), table.toString()));
            }
        }

        for (IntrospectedColumn ic : introspectedTable.getAllColumns()) {
            if (JavaReservedWords.containsWord(ic.getJavaProperty())) {
                this.warnings.add(Messages.getString("Warning.26", ic.getActualColumnName(), table.toString()));
            }
        }

    }

    public List<IntrospectedTable> introspectTables(TableConfiguration tc) throws SQLException {
        Map<ActualTableName, List<IntrospectedColumn>> columns = this.getColumns(tc);
        if (columns.isEmpty()) {
            this.warnings.add(Messages.getString("Warning.19", tc.getCatalog(), tc.getSchema(), tc.getTableName()));
            return Collections.emptyList();
        } else {
            this.removeIgnoredColumns(tc, columns);
            this.calculateExtraColumnInformation(tc, columns);
            this.applyColumnOverrides(tc, columns);
            this.calculateIdentityColumns(tc, columns);
            List<IntrospectedTable> introspectedTables = this.calculateIntrospectedTables(tc, columns);
            Iterator<IntrospectedTable> iter = introspectedTables.iterator();

            while (iter.hasNext()) {
                IntrospectedTable introspectedTable = iter.next();
                String warning;
                if (!introspectedTable.hasAnyColumns()) {
                    warning = Messages.getString("Warning.1", introspectedTable.getFullyQualifiedTable().toString());
                    this.warnings.add(warning);
                    iter.remove();
                } else if (!introspectedTable.hasPrimaryKeyColumns() && !introspectedTable.hasBaseColumns()) {
                    warning = Messages.getString("Warning.18", introspectedTable.getFullyQualifiedTable().toString());
                    this.warnings.add(warning);
                    iter.remove();
                } else {
                    this.reportIntrospectionWarnings(introspectedTable, tc, introspectedTable.getFullyQualifiedTable());
                }
            }

            return introspectedTables;
        }
    }

    private void removeIgnoredColumns(TableConfiguration tc, Map<ActualTableName, List<IntrospectedColumn>> columns) {

        for (Entry<ActualTableName, List<IntrospectedColumn>> actualTableNameListEntry : columns.entrySet()) {
            Iterator<IntrospectedColumn> tableColumns = actualTableNameListEntry.getValue().iterator();

            while (tableColumns.hasNext()) {
                IntrospectedColumn introspectedColumn = tableColumns.next();
                if (tc.isColumnIgnored(introspectedColumn.getActualColumnName())) {
                    tableColumns.remove();
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug(Messages.getString("Tracing.3", introspectedColumn.getActualColumnName(), actualTableNameListEntry.getKey().toString()));
                    }
                }
            }
        }

    }

    private void calculateExtraColumnInformation(TableConfiguration tc, Map<ActualTableName, List<IntrospectedColumn>> columns) {
        StringBuilder sb = new StringBuilder();
        Pattern pattern = null;
        String replaceString = null;
        if (tc.getColumnRenamingRule() != null) {
            pattern = Pattern.compile(tc.getColumnRenamingRule().getSearchString());
            replaceString = tc.getColumnRenamingRule().getReplaceString();
            replaceString = replaceString == null ? "" : replaceString;
        }

        for (Entry<ActualTableName, List<IntrospectedColumn>> actualTableNameListEntry : columns.entrySet()) {
            for (IntrospectedColumn introspectedColumn : actualTableNameListEntry.getValue()) {
                String calculatedColumnName;
                if (pattern == null) {
                    calculatedColumnName = introspectedColumn.getActualColumnName();
                } else {
                    Matcher matcher = pattern.matcher(introspectedColumn.getActualColumnName());
                    calculatedColumnName = matcher.replaceAll(replaceString);
                }

                if (StringUtility.isTrue(tc.getProperty("useActualColumnAnnotationInject"))) {
                    String rawTypeName = introspectedColumn.getProperties() == null
                        ? null
                        : introspectedColumn.getProperties().getProperty(TypeMappingEngine.RAW_TYPE_NAME);
                    Properties properties = new Properties();
                    properties.setProperty("useActualColumnAnnotationInject", "true");
                    if (rawTypeName != null) {
                        properties.setProperty(TypeMappingEngine.RAW_TYPE_NAME, rawTypeName);
                    }
                    introspectedColumn.setProperties(properties);
                }
                if (StringUtility.isTrue(tc.getProperty("useActualColumnNames"))) {
                    introspectedColumn.setJavaProperty(JavaBeansUtil.getValidPropertyName(calculatedColumnName));
                } else if (StringUtility.isTrue(tc.getProperty("useCompoundPropertyNames"))) {
                    sb.setLength(0);
                    sb.append(calculatedColumnName);
                    sb.append('_');
                    sb.append(JavaBeansUtil.getCamelCaseString(introspectedColumn.getRemarks(), true));
                    introspectedColumn.setJavaProperty(JavaBeansUtil.getValidPropertyName(sb.toString()));
                } else {
                    introspectedColumn.setJavaProperty(JavaBeansUtil.getCamelCaseString(calculatedColumnName, false));
                }

                FullyQualifiedJavaType fullyQualifiedJavaType = this.javaTypeResolver.calculateJavaType(introspectedColumn);
                if (fullyQualifiedJavaType != null) {
                    introspectedColumn.setFullyQualifiedJavaType(fullyQualifiedJavaType);
                    introspectedColumn.setJdbcTypeName(this.javaTypeResolver.calculateJdbcTypeName(introspectedColumn));
                } else {
                    boolean warn = !tc.isColumnIgnored(introspectedColumn.getActualColumnName());

                    ColumnOverride co = tc.getColumnOverride(introspectedColumn.getActualColumnName());
                    if (co != null && StringUtility.stringHasValue(co.getJavaType())) {
                        warn = false;
                    }

                    if (warn) {
                        introspectedColumn.setFullyQualifiedJavaType(FullyQualifiedJavaType.getObjectInstance());
                        introspectedColumn.setJdbcTypeName("OTHER");
                        String warning = Messages.getString("Warning.14", Integer.toString(introspectedColumn.getJdbcType()), actualTableNameListEntry.getKey().toString(), introspectedColumn.getActualColumnName());
                        this.warnings.add(warning);
                    }
                }

                if (this.context.autoDelimitKeywords() && SqlReservedWords.containsWord(introspectedColumn.getActualColumnName())) {
                    introspectedColumn.setColumnNameDelimited(true);
                }

                if (tc.isAllColumnDelimitingEnabled()) {
                    introspectedColumn.setColumnNameDelimited(true);
                }
            }
        }

    }

    private void calculateIdentityColumns(TableConfiguration tc, Map<ActualTableName, List<IntrospectedColumn>> columns) {
        GeneratedKey gk = tc.getGeneratedKey();
        if (gk != null) {
            Iterator<Entry<ActualTableName, List<IntrospectedColumn>>> var4 = columns.entrySet().iterator();

            label35:
            while (var4.hasNext()) {
                Entry<ActualTableName, List<IntrospectedColumn>> entry = var4.next();
                Iterator<IntrospectedColumn> var6 = entry.getValue().iterator();

                while (true) {
                    while (true) {
                        IntrospectedColumn introspectedColumn;
                        do {
                            if (!var6.hasNext()) {
                                continue label35;
                            }

                            introspectedColumn = var6.next();
                        } while (!this.isMatchedColumn(introspectedColumn, gk));

                        if (!gk.isIdentity() && !gk.isJdbcStandard()) {
                            introspectedColumn.setIdentity(false);
                            introspectedColumn.setSequenceColumn(true);
                        } else {
                            introspectedColumn.setIdentity(true);
                            introspectedColumn.setSequenceColumn(false);
                        }
                    }
                }
            }
        }

    }

    private boolean isMatchedColumn(IntrospectedColumn introspectedColumn, GeneratedKey gk) {
        return introspectedColumn.isColumnNameDelimited() ? introspectedColumn.getActualColumnName().equals(gk.getColumn()) : introspectedColumn.getActualColumnName().equalsIgnoreCase(gk.getColumn());
    }

    private void applyColumnOverrides(TableConfiguration tc, Map<ActualTableName, List<IntrospectedColumn>> columns) {

        for (Entry<ActualTableName, List<IntrospectedColumn>> actualTableNameListEntry : columns.entrySet()) {

            for (IntrospectedColumn introspectedColumn : actualTableNameListEntry.getValue()) {
                ColumnOverride columnOverride = tc.getColumnOverride(introspectedColumn.getActualColumnName());
                if (columnOverride != null) {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug(Messages.getString("Tracing.4", introspectedColumn.getActualColumnName(), actualTableNameListEntry.getKey().toString()));
                    }

                    if (StringUtility.stringHasValue(columnOverride.getJavaProperty())) {
                        introspectedColumn.setJavaProperty(columnOverride.getJavaProperty());
                    }

                    if (StringUtility.stringHasValue(columnOverride.getJavaType())) {
                        introspectedColumn.setFullyQualifiedJavaType(new FullyQualifiedJavaType(columnOverride.getJavaType()));
                    }

                    if (StringUtility.stringHasValue(columnOverride.getJdbcType())) {
                        introspectedColumn.setJdbcTypeName(columnOverride.getJdbcType());
                    }

                    if (StringUtility.stringHasValue(columnOverride.getTypeHandler())) {
                        introspectedColumn.setTypeHandler(columnOverride.getTypeHandler());
                    }

                    if (columnOverride.isColumnNameDelimited()) {
                        introspectedColumn.setColumnNameDelimited(true);
                    }

                    introspectedColumn.setGeneratedAlways(columnOverride.isGeneratedAlways());
                    introspectedColumn.setProperties(columnOverride.getProperties());
                }
            }
        }

    }

    private Map<ActualTableName, List<IntrospectedColumn>> getColumns(TableConfiguration tc) {
        String localCatalog = tc.getCatalog();
        String localSchema = tc.getSchema();
        String localTableName = tc.getTableName();

        Map<ActualTableName, List<IntrospectedColumn>> answer = new HashMap<>();
        if (this.logger.isDebugEnabled()) {
            String fullTableName = StringUtility.composeFullyQualifiedTableName(localCatalog, localSchema, localTableName, '.');
            this.logger.debug(Messages.getString("Tracing.1", fullTableName));
        }

        for (IntellijColumnInfo intellijColumnInfo : this.intellijTableInfo.getColumnInfos()) {
            IntrospectedColumn introspectedColumn = ObjectFactory.createIntrospectedColumn(this.context);
            introspectedColumn.setTableAlias(tc.getAlias());
            introspectedColumn.setJdbcType(intellijColumnInfo.getDataType());
            introspectedColumn.setLength(intellijColumnInfo.getSize());
            introspectedColumn.setActualColumnName(intellijColumnInfo.getName());
            introspectedColumn.setNullable(intellijColumnInfo.getNullable());
            introspectedColumn.setScale(intellijColumnInfo.getDecimalDigits());
            introspectedColumn.setRemarks(intellijColumnInfo.getRemarks());
            introspectedColumn.setDefaultValue(intellijColumnInfo.getColumnDefaultValue());
            introspectedColumn.setAutoIncrement(intellijColumnInfo.isAutoIncrement());
            introspectedColumn.setGeneratedColumn(intellijColumnInfo.isGeneratedColumn());
            if (intellijColumnInfo.getTypeName() != null) {
                Properties columnProperties = introspectedColumn.getProperties();
                if (columnProperties == null) {
                    columnProperties = new Properties();
                    introspectedColumn.setProperties(columnProperties);
                }
                columnProperties.setProperty(TypeMappingEngine.RAW_TYPE_NAME, intellijColumnInfo.getTypeName());
            }
            ActualTableName atn = new ActualTableName(null, null, this.intellijTableInfo.getTableName());
            List<IntrospectedColumn> columns = answer.computeIfAbsent(atn, k -> new ArrayList<>());

            columns.add(introspectedColumn);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug(Messages.getString("Tracing.2", introspectedColumn.getActualColumnName(), Integer.toString(introspectedColumn.getJdbcType()), atn.toString()));
            }
        }

        if (answer.size() > 1 && !StringUtility.stringContainsSQLWildcard(localSchema) && !StringUtility.stringContainsSQLWildcard(localTableName)) {
            ActualTableName inputAtn = new ActualTableName(tc.getCatalog(), tc.getSchema(), tc.getTableName());
            StringBuilder sb = new StringBuilder();
            boolean comma = false;
            for (ActualTableName actualTableName : answer.keySet()) {
                sb.append(actualTableName.toString());
                if (comma) {
                    sb.append(',');
                } else {
                    comma = true;
                }
            }
            this.warnings.add(Messages.getString("Warning.25", inputAtn.toString(), sb.toString()));
        }

        return answer;
    }

    private List<IntrospectedTable> calculateIntrospectedTables(TableConfiguration tc, Map<ActualTableName, List<IntrospectedColumn>> columns) {
        boolean delimitIdentifiers = tc.isDelimitIdentifiers() || StringUtility.stringContainsSpace(tc.getCatalog()) || StringUtility.stringContainsSpace(tc.getSchema()) || StringUtility.stringContainsSpace(tc.getTableName());
        List<IntrospectedTable> answer = new ArrayList<>();

        for (Entry<ActualTableName, List<IntrospectedColumn>> actualTableNameListEntry : columns.entrySet()) {
            ActualTableName atn = actualTableNameListEntry.getKey();
            FullyQualifiedTable table = new FullyQualifiedTable(StringUtility.stringHasValue(tc.getCatalog()) ? atn.getCatalog() : null, StringUtility.stringHasValue(tc.getSchema()) ? atn.getSchema() : null, atn.getTableName(), tc.getDomainObjectName(), tc.getAlias(), StringUtility.isTrue(tc.getProperty("ignoreQualifiersAtRuntime")), tc.getProperty("runtimeCatalog"), tc.getProperty("runtimeSchema"), tc.getProperty("runtimeTableName"), delimitIdentifiers, tc.getDomainObjectRenamingRule(), this.context);
            IntrospectedTable introspectedTable = ObjectFactory.createIntrospectedTable(tc, table, this.context);
            for (IntrospectedColumn introspectedColumn : actualTableNameListEntry.getValue()) {
                introspectedTable.addColumn(introspectedColumn);
            }

            this.calculatePrimaryKey(table, introspectedTable);
            this.enhanceIntrospectedTable(introspectedTable);
            answer.add(introspectedTable);
        }

        return answer;
    }

    private void enhanceIntrospectedTable(IntrospectedTable introspectedTable) {
        String remarks = this.intellijTableInfo.getTableRemark();
        String tableType = this.intellijTableInfo.getTableType();
        introspectedTable.setRemarks(remarks);
        introspectedTable.setTableType(tableType);
    }
}
