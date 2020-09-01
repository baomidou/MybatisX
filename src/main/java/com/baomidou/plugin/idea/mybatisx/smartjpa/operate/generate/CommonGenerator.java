package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate;

import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxParameterDescriptor;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TypeDescriptor;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager.AreaOperateManager;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager.AreaOperateManagerFactory;
import com.intellij.database.Dbms;
import com.intellij.database.model.DasDataSource;
import com.intellij.database.model.DasTable;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.psi.DbPsiFacade;
import com.intellij.database.util.DasUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.sql.dialects.SqlLanguageDialect;
import com.intellij.sql.psi.SqlPsiFacade;
import com.intellij.util.containers.JBIterable;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * 常用的生成器
 */
public class CommonGenerator implements PlatformGenerator {
    private @NotNull LinkedList<SyntaxAppender> jpaList;
    private List<TxField> mappingField;
    private String tableName;
    private PsiClass entityClass;
    final AreaOperateManager appenderManager;
    private String text;


    private DasTable dasTable;

    private CommonGenerator(PsiClass entityClass, String text,
                            @NotNull Dbms dbms,
                            String defaultTableName,
                            List<TxField> fields,
                            @NotNull Project project) {
        this.entityClass = entityClass;
        this.text = text;
        mappingField = fields;

        DbPsiFacade dbPsiFacade = DbPsiFacade.getInstance(project);
        String tableName = getTableName(entityClass, dbPsiFacade.getDataSources(), defaultTableName);


        this.tableName = tableName;

        appenderManager = AreaOperateManagerFactory.getByDbms(dbms, mappingField, entityClass, dasTable, tableName);
        jpaList = appenderManager.splitAppenderByText(text);
    }

    /**
     * 加一层缓存
     *
     * @param entityClass
     * @param text
     * @param project
     * @param dbms
     * @param defaultTableName
     * @param fields
     * @return
     */
    public static CommonGenerator createEditorAutoCompletion(PsiClass entityClass, String text,
                                                             @NotNull Project project,
                                                             @NotNull Dbms dbms,
                                                             String defaultTableName,
                                                             List<TxField> fields) {


        return new CommonGenerator(entityClass, text, dbms, defaultTableName, fields, project);
    }

    /**
     * 遍历所有数据源的表名
     *
     * @param entityClass
     * @param dataSources
     * @param foundTableName
     * @return
     */
    protected String getTableName(PsiClass entityClass, @NotNull List<DbDataSource> dataSources, String foundTableName) {
        if (StringUtils.isNotBlank(foundTableName)) {
            return foundTableName;
        }
        // 如果有多个候选值, 就选择长度最长的
        PriorityQueue<String> priorityQueue = new PriorityQueue<>(Comparator.comparing(String::length, Comparator.reverseOrder()));
        if (dataSources.size() > 0) {
            for (DbDataSource dataSource : dataSources) {
                JBIterable<? extends DasTable> tables = DasUtil.getTables(dataSource);
                for (DasTable table : tables) {
                    String entityTableName = entityClass.getName();
                    String tableName = table.getName();
                    String guessTableName = tableName.replaceAll("_", "").toUpperCase();
                    // 完全相等的情况下就不用候选了
                    if (guessTableName.equalsIgnoreCase(entityTableName)) {
                        // 第一版的猜测数据源，只做绝对相等的情况
                        dasTable = table;
                        return tableName;
                    }
                }
            }
        }
        // 存在候选的情况下, 返回表名最长的
        if (priorityQueue.size() > 0) {
            return priorityQueue.peek();
        }
        return entityClass.getName().toUpperCase();
    }

    public TypeDescriptor getParameter() {
        List<TxParameter> parameters = appenderManager.getParameters(entityClass, new LinkedList<>(jpaList));
        return new TxParameterDescriptor(parameters);
    }


    public TypeDescriptor getReturn() {
        LinkedList<SyntaxAppender> linkedList = new LinkedList<>(jpaList);
        return appenderManager.getReturnWrapper(text, entityClass, linkedList);
    }

    @Override
    public void generateMapperXml(PsiMethod psiMethod, MybatisXmlGenerator mybatisXmlGenerator) {
        appenderManager.generateMapperXml(
            text,
            new LinkedList<>(jpaList),
            entityClass,
            psiMethod,
            tableName,
            mybatisXmlGenerator);

    }

}
