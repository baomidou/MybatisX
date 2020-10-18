package com.baomidou.plugin.idea.mybatisx.smartjpa.common;

import com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest.ConditionFieldWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TypeDescriptor;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.Importer;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapperClassGenerateFactory {
    private final Project project;
    private final Editor editor;
    private final PsiElement element;
    private final PsiTypeElement statementElement;
    private final TypeDescriptor parameterDescriptor;
    private ConditionFieldWrapper conditionFieldWrapper;
    private final TypeDescriptor returnDescriptor;

    public MapperClassGenerateFactory(Project project,
                                      Editor editor,
                                      PsiElement element,
                                      PsiTypeElement statementElement,
                                      TypeDescriptor parameterDescriptor,
                                      ConditionFieldWrapper conditionFieldWrapper,
                                      TypeDescriptor returnDescriptor) {

        this.project = project;
        this.editor = editor;
        this.element = element;
        this.statementElement = statementElement;
        this.parameterDescriptor = parameterDescriptor;
        this.conditionFieldWrapper = conditionFieldWrapper;
        this.returnDescriptor = returnDescriptor;
    }

    public String generateMethodStr() {
        return returnDescriptor.getContent(conditionFieldWrapper.getDefaultDateList())
            + " "
            + statementElement.getText()
            + parameterDescriptor.getContent(conditionFieldWrapper.getDefaultDateList());

    }

    public void generateMethod() {
        generateMethod(null, Collections.emptyList());
    }

    /**
     * Generate and get method str string.
     */
    public void generateMethod(String prefixParam, List<String> importListParam) {
        String prefix = "";
        if (!StringUtils.isEmpty(prefixParam)) {
            prefix = prefixParam;
        }
        Document document = editor.getDocument();
        String newMethodString = prefix + generateMethodStr();
        TextRange textRange = statementElement.getTextRange();
        PsiFile containingFile = element.getContainingFile();

        // 导入对象
        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        Importer importer = Importer.create(parameterDescriptor.getImportList());
        importer.addImportToFile(psiDocumentManager,
            (PsiJavaFile) containingFile,
            document);
        List<String> importList = new ArrayList<>();
        importList.addAll(importListParam);
        importList.addAll(returnDescriptor.getImportList());
        Importer importerReturn = Importer.create(importList);

        document.replaceString(textRange.getStartOffset(), textRange.getEndOffset(), newMethodString);
        CodeStyleManager instance = CodeStyleManager.getInstance(project);
        instance.reformatText(containingFile, textRange.getStartOffset(), textRange.getStartOffset() + newMethodString.length());

        importerReturn.addImportToFile(psiDocumentManager,
            (PsiJavaFile) containingFile,
            document);


    }

}
