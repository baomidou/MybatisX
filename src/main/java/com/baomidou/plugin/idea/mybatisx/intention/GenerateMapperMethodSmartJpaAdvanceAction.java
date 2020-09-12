package com.baomidou.plugin.idea.mybatisx.intention;

import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest.ConditionFieldWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest.ConditionIfTestWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TypeDescriptor;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping.EntityMappingResolver;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping.EntityMappingResolverFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.CommonGenerator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.MybatisXmlGenerator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.PlatformGenerator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.Importer;
import com.baomidou.plugin.idea.mybatisx.ui.JpaAdvanceDialog;
import com.baomidou.plugin.idea.mybatisx.ui.SmartJpaAdvanceUI;
import com.baomidou.plugin.idea.mybatisx.util.MapperUtils;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.database.Dbms;
import com.intellij.database.model.DasTable;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.psi.DbPsiFacade;
import com.intellij.database.util.DasUtil;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.sql.dialects.SqlLanguageDialect;
import com.intellij.sql.psi.SqlPsiFacade;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.JBIterable;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 在mapper类中通过名字生成方法和xml内容
 *
 * @author ls9527
 */
public class GenerateMapperMethodSmartJpaAdvanceAction extends PsiElementBaseIntentionAction implements IntentionAction {

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {


        PsiTypeElement statementElement = PsiTreeUtil.getParentOfType(element, PsiTypeElement.class);
        if (statementElement == null) {
            statementElement = PsiTreeUtil.getPrevSiblingOfType(element, PsiTypeElement.class);
        }
        PsiClass mapperClass = PsiTreeUtil.getParentOfType(statementElement, PsiClass.class);
        if (mapperClass == null) {
            logger.info("未找到mapper类");
            return;
        }
        EntityMappingResolverFactory entityMappingResolverFactory = new EntityMappingResolverFactory(project, mapperClass);
        PsiClass entityClass = entityMappingResolverFactory.searchEntity();
        EntityMappingResolver entityMappingResolver = entityMappingResolverFactory.getEntityMappingResolver();
        if (entityClass == null) {
            logger.info("未找到实体类");
            return;
        }


        String text = statementElement.getText();

        try {
            PlatformGenerator platformGenerator = getPlatformGenerator(project, element, entityClass, entityMappingResolver, text);
            // 不仅仅是参数的字符串拼接， 还需要导入的对象
            TypeDescriptor parameterDescriptor = platformGenerator.getParameter();

            // 插入到编辑器
            TypeDescriptor returnDescriptor = platformGenerator.getReturn();
            if (returnDescriptor == null) {
                logger.info("不支持的语法");
                return;
            }

            Optional<ConditionFieldWrapper> conditionFieldWrapperOptional = getConditionFieldWrapper(project, platformGenerator);
            if (!conditionFieldWrapperOptional.isPresent()) {
                // 不希望生成任何内容
                return;
            }
            String newMethodString = generateAndGetMethodStr(project, editor, element, statementElement, parameterDescriptor, returnDescriptor);

            // 生成xml 内容,写入xml 文件
            Optional<Mapper> firstMapper = MapperUtils.findFirstMapper(project, mapperClass);
            if (firstMapper.isPresent()) {
                Mapper mapper = firstMapper.get();

                PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
                final PsiMethod psiMethod = factory.createMethodFromText(newMethodString, mapperClass);
                // 生成完整版的内容

                platformGenerator.generateMapperXml(psiMethod, new MybatisXmlGenerator(mapper, project), conditionFieldWrapperOptional.get());
            }


        } catch (Throwable e) {
            logger.error("generate error, text: {}", text, e);
        }
    }

    @NotNull
    protected String generateAndGetMethodStr(@NotNull Project project, Editor editor, @NotNull PsiElement element, PsiTypeElement statementElement, TypeDescriptor parameterDescriptor, TypeDescriptor returnDescriptor) {
        Document document = editor.getDocument();
        String newMethodString = returnDescriptor.getContent() + " " + statementElement.getText() + parameterDescriptor.getContent();
        TextRange textRange = statementElement.getTextRange();
        WriteCommandAction.runWriteCommandAction(project, () -> {
            document.replaceString(textRange.getStartOffset(), textRange.getEndOffset(), newMethodString);
        });

        // 导入对象
        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        Importer importer = Importer.create(parameterDescriptor.getImportList());
        importer.addImportToFile(psiDocumentManager,
            (PsiJavaFile) element.getContainingFile(),
            document);
        Importer importerReturn = Importer.create(returnDescriptor.getImportList());
        importerReturn.addImportToFile(psiDocumentManager,
            (PsiJavaFile) element.getContainingFile(),
            document);
        return newMethodString;
    }

    protected Optional<ConditionFieldWrapper> getConditionFieldWrapper(@NotNull Project project, PlatformGenerator platformGenerator) {
        // 弹出模态窗口
        JpaAdvanceDialog jpaAdvanceDialog = new JpaAdvanceDialog(project);
        jpaAdvanceDialog.initFields(platformGenerator.getConditionFields(),
            platformGenerator.getAllFields(),
            platformGenerator.getEntityClass());
        jpaAdvanceDialog.show();
        // 模态窗口选择 OK, 生成相关代码
        if (jpaAdvanceDialog.getExitCode() != Messages.YES) {
            return Optional.empty();
        }
        Set<String> selectedFields = jpaAdvanceDialog.getSelectedFields();
        ConditionIfTestWrapper conditionIfTestWrapper = new ConditionIfTestWrapper(selectedFields);

        conditionIfTestWrapper.setAllFields(jpaAdvanceDialog.getAllFieldsStr());

        conditionIfTestWrapper.setResultMap(jpaAdvanceDialog.getResultMap());
        conditionIfTestWrapper.setResultTypeClass(jpaAdvanceDialog.getResultTypeClass());
        conditionIfTestWrapper.setResultType(jpaAdvanceDialog.isResultType());


        return Optional.of(conditionIfTestWrapper);
    }

    @NotNull
    protected PlatformGenerator getPlatformGenerator(@NotNull Project project, @NotNull PsiElement element, PsiClass entityClass, EntityMappingResolver entityMappingResolver, String text) {
        Dbms dbms = Dbms.MYSQL;
        try {
            SqlPsiFacade instance = SqlPsiFacade.getInstance(project);
            SqlLanguageDialect dialectMapping = instance.getDialectMapping(element.getContainingFile().getVirtualFile());
            dbms = dialectMapping.getDbms();
        } catch (Exception ignore) {
        }
        // 名字默认是从实体上面解析到的
        String tableName = entityMappingResolver.getTableName();
        DasTableHolder dasTableHolder = new DasTableHolder();
        try {
            DbPsiFacade dbPsiFacade = DbPsiFacade.getInstance(project);
            // 名字可能会找到合适的表名
            tableName = getTableName(entityClass, dbPsiFacade.getDataSources(), entityMappingResolver.getTableName(), dasTableHolder);
        } catch (Exception ignore) {
        }
        return CommonGenerator.createEditorAutoCompletion(entityClass,
            text,
            dbms,
            dasTableHolder.getDasTable(),
            tableName,
            entityMappingResolver.getFields());
    }


    /**
     * 遍历所有数据源的表名
     *
     * @param entityClass
     * @param dataSources
     * @param foundTableName
     * @return
     */
    protected String getTableName(PsiClass entityClass, @NotNull List<DbDataSource> dataSources, String foundTableName, DasTableHolder dasTableHolder) {
        if (StringUtils.isNotBlank(foundTableName)) {
            return foundTableName;
        }
        // 如果有多个候选值, 就选择长度最长的
        PriorityQueue<String> priorityQueue = new PriorityQueue<>(Comparator.comparing(String::length, Comparator.reverseOrder()));
        if (dataSources.size() > 0) {
            for (DbDataSource dataSource : dataSources) {
                JBIterable<? extends DasTable> tables = DasUtil.getTables(dataSource);
                for (DasTable table : tables) {
                    String entityTableName = entityClass.getName().toLowerCase();
                    String tableName = table.getName();
                    String guessTableName = tableName.replaceAll("_", "").toUpperCase();
                    // 完全相等的情况下就不用候选了
                    if (guessTableName.equalsIgnoreCase(entityTableName)) {
                        // 第一版的猜测数据源，只做绝对相等的情况
                        dasTableHolder.setDasTable(table);
                        return tableName;
                    }
                    // 加入候选
                    if (guessTableName.contains(entityTableName.toUpperCase())) {
                        priorityQueue.add(guessTableName);
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

    private class DasTableHolder {
        DasTable dasTable = null;

        public DasTable getDasTable() {
            return dasTable;
        }

        public void setDasTable(DasTable dasTable) {
            this.dasTable = dasTable;
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(GenerateMapperMethodSmartJpaAdvanceAction.class);

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        String name = element.getContainingFile().getFileType().getName();
        if (!JavaFileType.INSTANCE.getName().equals(name)) {
            return false;
        }
        PsiMethod parentMethodOfType = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        if (parentMethodOfType != null) {
            return false;
        }
        PsiClass parentClassOfType = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        if (parentClassOfType == null || (!parentClassOfType.isInterface())) {
            return false;
        }

        // 查找最近的有效节点
        PsiTypeElement statementElement = PsiTreeUtil.getParentOfType(element, PsiTypeElement.class);
        if (statementElement == null) {
            statementElement = PsiTreeUtil.getPrevSiblingOfType(element, PsiTypeElement.class);
        }
        // 当前节点的父类不是mapper类就返回
        PsiClass mapperClass = PsiTreeUtil.getParentOfType(statementElement, PsiClass.class);
        if (mapperClass == null) {
            return false;
        }
        return true;
    }


    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getText() {
        return "Generate Mybatis Sql for Advance";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "Statement Complete";
    }


}
