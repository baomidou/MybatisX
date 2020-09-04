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


    private CommonGenerator(PsiClass entityClass, String text,
                            @NotNull Dbms dbms,
                            DasTable dasTable,
                            String defaultTableName,
                            List<TxField> fields) {
        this.entityClass = entityClass;
        this.text = text;
        mappingField = fields;

        this.tableName = defaultTableName;

        appenderManager = AreaOperateManagerFactory.getByDbms(dbms, mappingField, entityClass, dasTable, tableName);
        jpaList = appenderManager.splitAppenderByText(text);
    }

    /**
     *
     * @param entityClass
     * @param text
     * @param dbms
     * @param tableName
     * @param fields
     * @return
     */
    public static CommonGenerator createEditorAutoCompletion(PsiClass entityClass, String text,
                                                             @NotNull Dbms dbms,
                                                             DasTable dasTable,
                                                             String tableName,
                                                             List<TxField> fields) {
        return new CommonGenerator(entityClass, text, dbms,dasTable, tableName, fields);
    }


    @Override
    public TypeDescriptor getParameter() {
        List<TxParameter> parameters = appenderManager.getParameters(entityClass, new LinkedList<>(jpaList));
        return new TxParameterDescriptor(parameters);
    }


    @Override
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
