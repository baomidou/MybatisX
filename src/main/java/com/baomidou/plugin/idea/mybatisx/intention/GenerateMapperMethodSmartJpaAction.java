package com.baomidou.plugin.idea.mybatisx.intention;

import com.baomidou.plugin.idea.mybatisx.dom.model.*;
import com.baomidou.plugin.idea.mybatisx.smartjpa.completion.parameter.MxParameterManager;
import com.baomidou.plugin.idea.mybatisx.smartjpa.completion.res.ReturnWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.ui.EditorAutoCompletion;
import com.baomidou.plugin.idea.mybatisx.smartjpa.ui.MapperTagInfo;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.Importer;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.MapperSearch;
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
            MapperSearch mapperSearch = new MapperSearch();
            PsiClass mapperClass = mapperSearch.searchMapper(statementElement);
            PsiClass entityClass = mapperSearch.searchEntity(project, mapperClass);
            if (entityClass == null) {
                logger.info("未找到实体类");
                return;
            }

            final String text = statementElement.getText();
            EditorAutoCompletion editorAutoCompletion = EditorAutoCompletion.createEditorAutoCompletion(entityClass,text);
            // 不仅仅是参数的字符串拼接， 还需要导入的对象
            MxParameterManager parameterManager = editorAutoCompletion.getParameter();

            // 插入到编辑器
            ReturnWrapper returnWrapper = editorAutoCompletion.getReturn();
            Document document = editor.getDocument();
            String newMethodString = returnWrapper.getSimpleName() + " " + statementElement.getText() + parameterManager.getContent();
            TextRange textRange = statementElement.getTextRange();
            WriteCommandAction.runWriteCommandAction(project, () -> {
                document.replaceString(textRange.getStartOffset(), textRange.getEndOffset(), newMethodString);
            });

            // 导入对象
            PsiDocumentManager psiDocumentManager = PsiDocumentManager
                .getInstance(project);
            Importer importer = Importer.create(parameterManager.getImports());
            importer.addImportToFile(psiDocumentManager,
                (PsiJavaFile) element.getContainingFile(),
                document);


            Optional<Mapper> firstMapper = MapperUtils.findFirstMapper(project, mapperClass);
            if (firstMapper.isPresent()) {
                Mapper mapper = firstMapper.get();

                PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
                final PsiMethod psiMethod = factory.createMethodFromText(newMethodString, mapperClass);
                MapperTagInfo processor = editorAutoCompletion.generateMapperXml(psiMethod);
                // 赶时间, 后续优化
                if ("select".equals(processor.getTagType())) {
                    Select select = mapper.addSelect();
                    select.getId().setStringValue(processor.getId());
                    select.setValue(processor.getMapperXml());
                } else if ("insert".equals(processor.getTagType())) {
                    Insert insert = mapper.addInsert();
                    insert.getId().setStringValue(processor.getId());
                    insert.setValue(processor.getMapperXml());
                } else if ("update".equals(processor.getTagType())) {
                    Update update = mapper.addUpdate();
                    update.getId().setStringValue(processor.getId());
                    update.setValue(processor.getMapperXml());
                } else if ("delete".equals(processor.getTagType())) {
                    Delete delete = mapper.addDelete();
                    delete.getId().setStringValue(processor.getId());
                    delete.setValue(processor.getMapperXml());
                }

                logger.info("生成的mapper内容: {}", processor.getMapperXml());
            }
//            Messages.showMultilineInputDialog(project, "复制下面的内容", "生成的xml内容", xmlContent, null, null);


        } catch (Throwable e) {
            logger.error("聪明的JPA失败了, 真笨", e);
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
