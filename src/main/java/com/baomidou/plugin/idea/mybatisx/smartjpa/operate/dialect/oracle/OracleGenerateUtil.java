package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.dialect.oracle;

import com.intellij.database.model.DasObject;
import com.intellij.database.model.DasTable;
import com.intellij.database.model.ObjectKind;
import com.intellij.util.containers.JBIterable;

import java.util.Comparator;
import java.util.Optional;
import java.util.PriorityQueue;

/**
 * oracle 生成代码工具
 */
public class OracleGenerateUtil {

    public static Optional<String> findSequenceName(DasTable dasTable, String tableName) {
        String foundSequenceName = null;
        if (dasTable != null) {
            DasObject schema = dasTable.getDasParent();
            if (schema == null) {
                return Optional.empty();
            }
            PriorityQueue<String> sequenceQueue = new PriorityQueue<>(Comparator.comparing(String::length, Comparator.reverseOrder()));

            JBIterable<? extends DasObject> sequences = schema.getDasChildren(ObjectKind.SEQUENCE);
            for (DasObject sequence : sequences) {
                String sequenceName = sequence.getName();
                if (sequenceName.contains(tableName)) {
                    sequenceQueue.add(sequenceName);
                }
            }
            if (sequenceQueue.size() > 0) {
                foundSequenceName = sequenceQueue.peek();
            }

        }
        return Optional.ofNullable(foundSequenceName);
    }

}
