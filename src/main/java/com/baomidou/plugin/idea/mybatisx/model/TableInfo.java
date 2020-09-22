package com.baomidou.plugin.idea.mybatisx.model;

import com.intellij.database.model.DasColumn;
import com.intellij.database.psi.DbTable;
import com.intellij.database.util.DasUtil;
import com.intellij.util.containers.JBIterable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The type Table info.
 */
public class TableInfo {

    /**
     * The Table element.
     */
    public final DbTable tableElement;

    private List<DasColumn> columns;

    private List<String> primaryKeys = new ArrayList<>();


    /**
     * Instantiates a new Table info.
     *
     * @param tableElement the table element
     */
    public TableInfo(DbTable tableElement) {
        this.tableElement = tableElement;
        List<DasColumn> columns = new ArrayList<>();

        JBIterable<? extends DasColumn> columnsIter = DasUtil.getColumns(tableElement);
        List<? extends DasColumn> dasColumns = columnsIter.toList();
        for (DasColumn dasColumn : dasColumns) {
            columns.add(dasColumn);

            if (DasUtil.isPrimary(dasColumn)) {
                primaryKeys.add(dasColumn.getName());
            }

        }

        this.columns = columns;
    }

    /**
     * Gets table name.
     *
     * @return the table name
     */
    public String getTableName() {
        return tableElement.getName();
    }

    /**
     * Gets columns.
     *
     * @return the columns
     */
    public List<DasColumn> getColumns() {
        return columns;
    }

    /**
     * Gets columns name.
     *
     * @return the columns name
     */
    public List<String> getColumnsName() {
        List<String> columnsName = new ArrayList<>();
        for (DasColumn column : columns) {
            columnsName.add(column.getName());
        }
        return columnsName;
    }

    /**
     * Gets primary keys.
     *
     * @return the primary keys
     */
    public List<String> getPrimaryKeys() {
        return this.primaryKeys;
    }

    /**
     * Gets non primary columns.
     *
     * @return the non primary columns
     */
    public List<DasColumn> getNonPrimaryColumns() {
        Set<String> pKNameSet = new HashSet<String>();
        for (String pkName : getPrimaryKeys()) {
            pKNameSet.add(pkName);
        }

        List<DasColumn> ret = new ArrayList<DasColumn>();
        for (DasColumn column : columns) {
            if (!pKNameSet.contains(column.getName())) {
                ret.add(column);
            }
        }

        return ret;
    }
}
