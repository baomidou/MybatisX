package com.baomidou.plugin.idea.mybatisx.smartjpa.util;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiImportStatement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiPackageStatement;
import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * The type Importer.
 */
public class Importer {

    private Set<String> newImportList;

    /**
     * Create importer.
     *
     * @param strings the strings
     * @return the importer
     */
    public static Importer create(final Set<String> strings) {
        Importer importer = new Importer();
        importer.newImportList = strings;
        return importer;
    }

    /**
     * Create importer.
     *
     * @param imports the imports
     * @return the importer
     */
    public static Importer create(List<String> imports) {
        Importer importer = new Importer();
        importer.newImportList = new HashSet<>(imports);
        return importer;
    }

    /**
     * Add import to file.
     *
     * @param psiDocumentManager the psi document manager
     * @param containingFile     the containing file
     * @param document           the document
     */
    public void addImportToFile(PsiDocumentManager psiDocumentManager,
                                PsiJavaFile containingFile,
                                Document document) {
        if (newImportList.size() > 0) {
            Iterator<String> iterator = newImportList.iterator();
            while (iterator.hasNext()) {
                String u = iterator.next();
                if (u == null || u.startsWith("java.lang")) {
                    iterator.remove();
                }
            }
        }

        if (newImportList.size() > 0) {
            PsiImportStatement[] importStatements = containingFile.getImportList().getImportStatements();
            Set<String> containedSet = new HashSet<>();
            for (PsiImportStatement s : importStatements) {
                containedSet.add(s.getQualifiedName());
            }
            StringBuilder newImportText = new StringBuilder();
            for (String newImport : newImportList) {
                if (!containedSet.contains(newImport)) {
                    newImportText.append("\nimport ").append(newImport).append(";");
                }
            }
            PsiPackageStatement packageStatement = containingFile.getPackageStatement();
            int start = 0;
            if (packageStatement != null) {
                start = packageStatement.getTextLength() + packageStatement.getTextOffset();
            }
            String insertText = newImportText.toString();
            if (StringUtils.isNotBlank(insertText)) {
                document.insertString(start, insertText);
                commitAndSaveDocument(psiDocumentManager, document);
            }
        }
    }

    /**
     * Commit and save document.
     *
     * @param psiDocumentManager the psi document manager
     * @param document           the document
     */
    public static void commitAndSaveDocument(PsiDocumentManager psiDocumentManager, Document document) {
        if (document != null) {
            psiDocumentManager.doPostponedOperationsAndUnblockDocument(document);
            psiDocumentManager.commitDocument(document);
            FileDocumentManager.getInstance().saveDocument(document);

        }
    }
}
