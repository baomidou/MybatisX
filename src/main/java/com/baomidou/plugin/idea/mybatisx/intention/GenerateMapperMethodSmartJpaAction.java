package com.baomidou.plugin.idea.mybatisx.intention;

import com.baomidou.plugin.idea.mybatisx.dom.model.*;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TypeDescriptor;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping.EntityMappingResolver;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.CommonGenerator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.MybatisXmlGenerator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.PlatformGenerator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.Importer;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.EntityMappingResolverFactory;
import com.baomidou.plugin.idea.mybatisx.util.MapperUtils;
import com.google.common.base.Optional;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liushang
 */
public class GenerateMapperMethodSmartJpaAction extends PsiElementBaseIntentionAction implements IntentionAction {

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        try {
            PsiTypeElement statementElement = PsiTreeUtil.getParentOfType(element, PsiTypeElement.class);
            if (statementElement == null) {
                statementElement = PsiTreeUtil.getPrevSiblingOfType(element, PsiTypeElement.class);
            }
            PsiClass mapperClass = PsiTreeUtil.getParentOfType(statementElement, PsiClass.class);
            EntityMappingResolverFactory entityMappingResolverFactory = new EntityMappingResolverFactory(project, mapperClass);
            PsiClass entityClass = entityMappingResolverFactory.searchEntity();
            EntityMappingResolver entityMappingResolver = entityMappingResolverFactory.getEntityMappingResolver();
            if (entityClass == null) {
                logger.info("未找到实体类");
                return;
            }

            final String text = statementElement.getText();
            PlatformGenerator platformGenerator = CommonGenerator.createEditorAutoCompletion(entityClass, text,entityMappingResolver);
            // 不仅仅是参数的字符串拼接， 还需要导入的对象
            TypeDescriptor parameterDescriptor = platformGenerator.getParameter();

            // 插入到编辑器
            TypeDescriptor returnDescriptor = platformGenerator.getReturn();
            if (returnDescriptor == null) {
                logger.info("不支持的语法");
                return;
            }
            Document document = editor.getDocument();
            String newMethodString = returnDescriptor.getContent() + " " + statementElement.getText() + parameterDescriptor.getContent();
            TextRange textRange = statementElement.getTextRange();
            WriteCommandAction.runWriteCommandAction(project, () -> {
                document.replaceString(textRange.getStartOffset(), textRange.getEndOffset(), newMethodString);
            });

            // 导入对象
            PsiDocumentManager psiDocumentManager = PsiDocumentManager
                .getInstance(project);
            Importer importer = Importer.create(parameterDescriptor.getImportList());
            importer.addImportToFile(psiDocumentManager,
                (PsiJavaFile) element.getContainingFile(),
                document);
            Importer importerReturn = Importer.create(returnDescriptor.getImportList());
            importerReturn.addImportToFile(psiDocumentManager,
                (PsiJavaFile) element.getContainingFile(),
                document);


            Optional<Mapper> firstMapper = MapperUtils.findFirstMapper(project, mapperClass);
            if (firstMapper.isPresent()) {
                Mapper mapper = firstMapper.get();

                PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
                final PsiMethod psiMethod = factory.createMethodFromText(newMethodString, mapperClass);
                // 生成完整版的内容
                platformGenerator.generateMapperXml(psiMethod, new MybatisXmlGenerator(mapper));
            }


        } catch (Throwable e) {
            logger.error("聪明的JPA失败了", e);
        }
    }


    private static final Logger logger = LoggerFactory.getLogger(GenerateMapperMethodSmartJpaAction.class);


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
        return true;
    }


    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getText() {
        return "Generate Mybatis Sql";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "Statement Complete";
    }


}
