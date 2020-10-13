package com.baomidou.plugin.idea.mybatisx.intention;

import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest.ConditionFieldWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest.ConditionIfTestWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TypeDescriptor;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping.EntityMappingResolver;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping.EntityMappingResolverFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.MybatisXmlGenerator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.PlatformGenerator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.PlatformDbGenerator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.PlatformSimpleGenerator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.Importer;
import com.baomidou.plugin.idea.mybatisx.ui.JpaAdvanceDialog;
import com.baomidou.plugin.idea.mybatisx.util.MapperUtils;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.database.psi.DbTable;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;

/**
 * 在mapper类中通过名字生成方法和xml内容
 *
 * @author ls9527
 */
public class GenerateMapperMethodSmartJpaAdvanceAction extends PsiElementBaseIntentionAction implements IntentionAction {

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        try {

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

            boolean hasDatabaseComponent = findDatabaseComponent();
            String text = statementElement.getText();

            PlatformSimpleGenerator platformSimpleGenerator = new PlatformSimpleGenerator();
            if (hasDatabaseComponent) {
                platformSimpleGenerator = new PlatformDbGenerator();
            }
            PlatformGenerator platformGenerator = platformSimpleGenerator.getPlatformGenerator(project, element, entityClass, entityMappingResolver, text);
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
            logger.error("generate error ", e);
        }
    }

    private boolean findDatabaseComponent() {
        try {
            Class.forName("com.intellij.database.psi.DbTable");
        } catch (NoClassDefFoundError | ClassNotFoundException ex) {
            return false;
        }
        return true;
    }

    /**
     * Generate and get method str string.
     *
     * @param project             the project
     * @param editor              the editor
     * @param element             the element
     * @param statementElement    the statement element
     * @param parameterDescriptor the parameter descriptor
     * @param returnDescriptor    the return descriptor
     * @return the string
     */
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

    /**
     * Gets condition field wrapper.
     *
     * @param project           the project
     * @param platformGenerator the platform generator
     * @return the condition field wrapper
     */
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
        ConditionIfTestWrapper conditionIfTestWrapper = new ConditionIfTestWrapper(selectedFields,platformGenerator.getAllFields());

        conditionIfTestWrapper.setAllFields(jpaAdvanceDialog.getAllFieldsStr());

        conditionIfTestWrapper.setResultMap(jpaAdvanceDialog.getResultMap());
        conditionIfTestWrapper.setResultTypeClass(jpaAdvanceDialog.getResultTypeClass());
        conditionIfTestWrapper.setResultType(jpaAdvanceDialog.isResultType());


        return Optional.of(conditionIfTestWrapper);
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
