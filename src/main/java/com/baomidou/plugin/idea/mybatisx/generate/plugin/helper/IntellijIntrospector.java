package com.baomidou.plugin.idea.mybatisx.generate.plugin.helper;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class IntellijIntrospector {
    private IntellijTableInfo intellijTableInfo;
    private JavaTypeResolver javaTypeResolver;
    private List<String> warnings;
    private Context context;
    private Log logger;

    public IntellijIntrospector(Context context, JavaTypeResolver javaTypeResolver, List<String> warnings, IntellijTableInfo tableInfo) {
        this.context = context;
        this.intellijTableInfo = tableInfo;
        this.javaTypeResolver = javaTypeResolver;
        this.warnings = warnings;
        this.logger = LogFactory.getLog(this.getClass());
    }

    private void calculatePrimaryKey(FullyQualifiedTable table, IntrospectedTable introspectedTable) {
        Map<Short, String> keyColumns = new TreeMap();
        List<IntellijColumnInfo> primaryKeyColumns = this.intellijTableInfo.getPrimaryKeyColumns();
        Iterator var5 = primaryKeyColumns.iterator();

        while(var5.hasNext()) {
            IntellijColumnInfo primaryKeyColumn = (IntellijColumnInfo)var5.next();
            String columnName = primaryKeyColumn.getName();
            short keySeq = primaryKeyColumn.getKeySeq();
            keyColumns.put(keySeq, columnName);
        }

        var5 = keyColumns.values().iterator();

        while(var5.hasNext()) {
            String columnName = (String)var5.next();
            introspectedTable.addPrimaryKeyColumn(columnName);
        }

    }

    private void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException var3) {
            }
        }

    }

    private void reportIntrospectionWarnings(IntrospectedTable introspectedTable, TableConfiguration tableConfiguration, FullyQualifiedTable table) {
        Iterator var4 = tableConfiguration.getColumnOverrides().iterator();

        while(var4.hasNext()) {
            ColumnOverride columnOverride = (ColumnOverride)var4.next();
            if (!introspectedTable.getColumn(columnOverride.getColumnName()).isPresent()) {
                this.warnings.add(Messages.getString("Warning.3", columnOverride.getColumnName(), table.toString()));
            }
        }

        var4 = tableConfiguration.getIgnoredColumnsInError().iterator();

        while(var4.hasNext()) {
            String string = (String)var4.next();
            this.warnings.add(Messages.getString("Warning.4", string, table.toString()));
        }

        GeneratedKey generatedKey = tableConfiguration.getGeneratedKey();
        if (generatedKey != null && !introspectedTable.getColumn(generatedKey.getColumn()).isPresent()) {
            if (generatedKey.isIdentity()) {
                this.warnings.add(Messages.getString("Warning.5", generatedKey.getColumn(), table.toString()));
            } else {
                this.warnings.add(Messages.getString("Warning.6", generatedKey.getColumn(), table.toString()));
            }
        }

        Iterator var9 = introspectedTable.getAllColumns().iterator();

        while(var9.hasNext()) {
            IntrospectedColumn ic = (IntrospectedColumn)var9.next();
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
            Iterator iter = introspectedTables.iterator();

            while(true) {
                while(iter.hasNext()) {
                    IntrospectedTable introspectedTable = (IntrospectedTable)iter.next();
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
    }

    private void removeIgnoredColumns(TableConfiguration tc, Map<ActualTableName, List<IntrospectedColumn>> columns) {
        Iterator var3 = columns.entrySet().iterator();

        while(var3.hasNext()) {
            Entry<ActualTableName, List<IntrospectedColumn>> entry = (Entry)var3.next();
            Iterator tableColumns = ((List)entry.getValue()).iterator();

            while(tableColumns.hasNext()) {
                IntrospectedColumn introspectedColumn = (IntrospectedColumn)tableColumns.next();
                if (tc.isColumnIgnored(introspectedColumn.getActualColumnName())) {
                    tableColumns.remove();
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug(Messages.getString("Tracing.3", introspectedColumn.getActualColumnName(), ((ActualTableName)entry.getKey()).toString()));
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

        Iterator var6 = columns.entrySet().iterator();

        while(var6.hasNext()) {
            Entry<ActualTableName, List<IntrospectedColumn>> entry = (Entry)var6.next();
            Iterator var8 = ((List)entry.getValue()).iterator();

            while(var8.hasNext()) {
                IntrospectedColumn introspectedColumn = (IntrospectedColumn)var8.next();
                String calculatedColumnName;
                if (pattern == null) {
                    calculatedColumnName = introspectedColumn.getActualColumnName();
                } else {
                    Matcher matcher = pattern.matcher(introspectedColumn.getActualColumnName());
                    calculatedColumnName = matcher.replaceAll(replaceString);
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
                    boolean warn = true;
                    if (tc.isColumnIgnored(introspectedColumn.getActualColumnName())) {
                        warn = false;
                    }

                    ColumnOverride co = tc.getColumnOverride(introspectedColumn.getActualColumnName());
                    if (co != null && StringUtility.stringHasValue(co.getJavaType())) {
                        warn = false;
                    }

                    if (warn) {
                        introspectedColumn.setFullyQualifiedJavaType(FullyQualifiedJavaType.getObjectInstance());
                        introspectedColumn.setJdbcTypeName("OTHER");
                        String warning = Messages.getString("Warning.14", Integer.toString(introspectedColumn.getJdbcType()), ((ActualTableName)entry.getKey()).toString(), introspectedColumn.getActualColumnName());
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
            Iterator var4 = columns.entrySet().iterator();

            label35:
            while(var4.hasNext()) {
                Entry<ActualTableName, List<IntrospectedColumn>> entry = (Entry)var4.next();
                Iterator var6 = ((List)entry.getValue()).iterator();

                while(true) {
                    while(true) {
                        IntrospectedColumn introspectedColumn;
                        do {
                            if (!var6.hasNext()) {
                                continue label35;
                            }

                            introspectedColumn = (IntrospectedColumn)var6.next();
                        } while(!this.isMatchedColumn(introspectedColumn, gk));

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
        Iterator var3 = columns.entrySet().iterator();

        while(var3.hasNext()) {
            Entry<ActualTableName, List<IntrospectedColumn>> entry = (Entry)var3.next();
            Iterator var5 = ((List)entry.getValue()).iterator();

            while(var5.hasNext()) {
                IntrospectedColumn introspectedColumn = (IntrospectedColumn)var5.next();
                ColumnOverride columnOverride = tc.getColumnOverride(introspectedColumn.getActualColumnName());
                if (columnOverride != null) {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug(Messages.getString("Tracing.4", introspectedColumn.getActualColumnName(), ((ActualTableName)entry.getKey()).toString()));
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

    private Map<ActualTableName, List<IntrospectedColumn>> getColumns(TableConfiguration tc) throws SQLException {
        boolean delimitIdentifiers = tc.isDelimitIdentifiers() || StringUtility.stringContainsSpace(tc.getCatalog()) || StringUtility.stringContainsSpace(tc.getSchema()) || StringUtility.stringContainsSpace(tc.getTableName());
        String localCatalog;
        String localSchema;
        String localTableName;
        if (delimitIdentifiers) {
            localCatalog = tc.getCatalog();
            localSchema = tc.getSchema();
            localTableName = tc.getTableName();
        } else {
            localCatalog = tc.getCatalog();
            localSchema = tc.getSchema();
            localTableName = tc.getTableName();
        }

        Map<ActualTableName, List<IntrospectedColumn>> answer = new HashMap();
        if (this.logger.isDebugEnabled()) {
            String fullTableName = StringUtility.composeFullyQualifiedTableName(localCatalog, localSchema, localTableName, '.');
            this.logger.debug(Messages.getString("Tracing.1", fullTableName));
        }

        boolean supportsIsAutoIncrement = false;
        boolean supportsIsGeneratedColumn = false;
        Iterator var9 = this.intellijTableInfo.getColumnInfos().iterator();

        IntellijColumnInfo intellijColumnInfo;
        while(var9.hasNext()) {
            intellijColumnInfo = (IntellijColumnInfo)var9.next();
            if (intellijColumnInfo.isAutoIncrement()) {
                supportsIsAutoIncrement = true;
            }

            if (intellijColumnInfo.isGeneratedColumn()) {
                supportsIsGeneratedColumn = true;
            }
        }

        var9 = this.intellijTableInfo.getColumnInfos().iterator();

        while(var9.hasNext()) {
            intellijColumnInfo = (IntellijColumnInfo)var9.next();
            IntrospectedColumn introspectedColumn = ObjectFactory.createIntrospectedColumn(this.context);
            introspectedColumn.setTableAlias(tc.getAlias());
            introspectedColumn.setJdbcType(intellijColumnInfo.getDataType());
            introspectedColumn.setLength(intellijColumnInfo.getSize());
            introspectedColumn.setActualColumnName(intellijColumnInfo.getName());
            introspectedColumn.setNullable(intellijColumnInfo.getNullable());
            introspectedColumn.setScale(intellijColumnInfo.getDecimalDigits());
            introspectedColumn.setRemarks(intellijColumnInfo.getRemarks());
            introspectedColumn.setDefaultValue(intellijColumnInfo.getColumnDefaultValue());
            if (supportsIsAutoIncrement) {
                introspectedColumn.setAutoIncrement(intellijColumnInfo.isAutoIncrement());
            }

            if (supportsIsGeneratedColumn) {
                introspectedColumn.setGeneratedColumn(intellijColumnInfo.isGeneratedColumn());
            }

            ActualTableName atn = new ActualTableName((String)null, (String)null, this.intellijTableInfo.getTableName());
            List<IntrospectedColumn> columns = (List)answer.get(atn);
            if (columns == null) {
                columns = new ArrayList();
                answer.put(atn, columns);
            }

            ((List)columns).add(introspectedColumn);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug(Messages.getString("Tracing.2", introspectedColumn.getActualColumnName(), Integer.toString(introspectedColumn.getJdbcType()), atn.toString()));
            }
        }

        if (answer.size() > 1 && !StringUtility.stringContainsSQLWildcard(localSchema) && !StringUtility.stringContainsSQLWildcard(localTableName)) {
            ActualTableName inputAtn = new ActualTableName(tc.getCatalog(), tc.getSchema(), tc.getTableName());
            StringBuilder sb = new StringBuilder();
            boolean comma = false;

            ActualTableName atn;
            for(Iterator var18 = answer.keySet().iterator(); var18.hasNext(); sb.append(atn.toString())) {
                atn = (ActualTableName)var18.next();
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
        List<IntrospectedTable> answer = new ArrayList();
        Iterator var5 = columns.entrySet().iterator();

        while(var5.hasNext()) {
            Entry<ActualTableName, List<IntrospectedColumn>> entry = (Entry)var5.next();
            ActualTableName atn = (ActualTableName)entry.getKey();
            FullyQualifiedTable table = new FullyQualifiedTable(StringUtility.stringHasValue(tc.getCatalog()) ? atn.getCatalog() : null, StringUtility.stringHasValue(tc.getSchema()) ? atn.getSchema() : null, atn.getTableName(), tc.getDomainObjectName(), tc.getAlias(), StringUtility.isTrue(tc.getProperty("ignoreQualifiersAtRuntime")), tc.getProperty("runtimeCatalog"), tc.getProperty("runtimeSchema"), tc.getProperty("runtimeTableName"), delimitIdentifiers, tc.getDomainObjectRenamingRule(), this.context);
            IntrospectedTable introspectedTable = ObjectFactory.createIntrospectedTable(tc, table, this.context);
            Iterator var10 = ((List)entry.getValue()).iterator();

            while(var10.hasNext()) {
                IntrospectedColumn introspectedColumn = (IntrospectedColumn)var10.next();
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

