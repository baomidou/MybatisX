package com.baomidou.plugin.idea.mybatisx.injection;

import com.baomidou.plugin.idea.mybatisx.annotation.Annotation;
import com.intellij.lang.Language;
import com.intellij.lang.injection.ConcatenationAwareInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiArrayInitializerMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Injects MyBatis Mapper XML (for {@code <script>} blocks) or SQL into MyBatis Mapper statement annotations
 * ({@code @Select}, {@code @Insert}, {@code @Update}, {@code @Delete}), supporting single-line strings,
 * multiline text blocks, string concatenation, and string arrays.
 */
public class MapperAnnotationInjector implements ConcatenationAwareInjector {

    private static final String SCRIPT_OPEN_TAG = "<script>";
    private static final String SCRIPT_CLOSE_TAG = "</script>";

    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, PsiElement @NotNull ... operands) {
        if (operands.length == 0) {
            return;
        }

        PsiElement first = operands[0];
        PsiElement parent = first.getParent();
        PsiElement targetRoot = parent instanceof PsiArrayInitializerMemberValue ? parent : first;

        PsiNameValuePair pair = findTargetNameValuePair(targetRoot);
        if (pair == null) {
            return;
        }

        PsiAnnotation annotation = PsiTreeUtil.getParentOfType(pair, PsiAnnotation.class, true);
        if (annotation == null) {
            return;
        }

        List<HostFragment> fragments = collectFragments(targetRoot, operands);
        if (fragments.isEmpty()) {
            return;
        }

        if (isScriptContent(fragments)) {
            injectMapperXml(registrar, annotation, fragments);
        } else {
            Language sqlLanguage = Language.findLanguageByID("SQL");
            if (sqlLanguage != null) {
                injectFragments(registrar, sqlLanguage, null, null, fragments);
            }
        }
    }

    private void injectMapperXml(@NotNull MultiHostRegistrar registrar,
                                 @NotNull PsiAnnotation annotation,
                                 @NotNull List<HostFragment> fragments) {
        List<HostFragment> stripped = stripScriptTags(fragments);
        if (stripped.isEmpty()) {
            return;
        }

        PsiMethod method = PsiTreeUtil.getParentOfType(annotation, PsiMethod.class);
        PsiClass mapperClass = method != null ? method.getContainingClass() : null;
        String namespace = mapperClass != null && mapperClass.getQualifiedName() != null
            ? mapperClass.getQualifiedName()
            : "";
        String methodName = method != null ? method.getName() : "statement";
        String tagName = resolveStatementTagName(annotation);

        String prefix = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
            "<mapper namespace=\"" + namespace + "\"><" + tagName + " id=\"" + methodName + "\">";
        String suffix = "</" + tagName + "></mapper>";

        injectFragments(registrar, XMLLanguage.INSTANCE, prefix, suffix, stripped);
    }

    private void injectFragments(@NotNull MultiHostRegistrar registrar,
                                 @NotNull Language language,
                                 @Nullable String prefix,
                                 @Nullable String suffix,
                                 @NotNull List<HostFragment> fragments) {
        registrar.startInjecting(language);
        for (int i = 0; i < fragments.size(); i++) {
            HostFragment fragment = fragments.get(i);
            String placePrefix = (i == 0) ? prefix : null;
            String placeSuffix;
            if (i == fragments.size() - 1) {
                placeSuffix = suffix;
            } else {
                placeSuffix = fragment.joinSuffix;
            }
            registrar.addPlace(placePrefix, placeSuffix, fragment.host, fragment.range);
        }
        registrar.doneInjecting();
    }

    private @NotNull List<HostFragment> stripScriptTags(@NotNull List<HostFragment> fragments) {
        List<HostFragment> result = new ArrayList<>(fragments.size());
        boolean openStripped = false;
        for (int i = 0; i < fragments.size(); i++) {
            HostFragment fragment = fragments.get(i);
            String hostText = fragment.host.getText();
            int start = fragment.range.getStartOffset();
            int end = fragment.range.getEndOffset();

            if (!openStripped) {
                int openIdx = hostText.indexOf(SCRIPT_OPEN_TAG, start);
                if (openIdx >= start && openIdx + SCRIPT_OPEN_TAG.length() <= end) {
                    start = openIdx + SCRIPT_OPEN_TAG.length();
                    openStripped = true;
                }
            }

            if (i == fragments.size() - 1) {
                int closeIdx = hostText.lastIndexOf(SCRIPT_CLOSE_TAG, end);
                if (closeIdx >= start && closeIdx + SCRIPT_CLOSE_TAG.length() <= end) {
                    end = closeIdx;
                }
            }

            if (start < end) {
                TextRange newRange = new TextRange(start, end);
                result.add(new HostFragment(fragment.host, newRange, fragment.text, fragment.joinSuffix));
            }
        }
        return result;
    }

    private @NotNull String resolveStatementTagName(@NotNull PsiAnnotation annotation) {
        String qualifiedName = annotation.getQualifiedName();
        if (Annotation.INSERT.getQualifiedName().equals(qualifiedName)) {
            return "insert";
        }
        if (Annotation.UPDATE.getQualifiedName().equals(qualifiedName)) {
            return "update";
        }
        if (Annotation.DELETE.getQualifiedName().equals(qualifiedName)) {
            return "delete";
        }
        return "select";
    }

    private @Nullable PsiNameValuePair findTargetNameValuePair(@NotNull PsiElement targetRoot) {
        PsiElement container = targetRoot.getParent();
        // When targetRoot is a PsiLiteralExpression inside PsiPolyadicExpression, walk up to PsiNameValuePair.
        if (!(container instanceof PsiNameValuePair) && container != null) {
            container = container.getParent();
        }
        if (!(container instanceof PsiNameValuePair pair)) {
            return null;
        }
        String attrName = pair.getName();
        if (attrName != null && !"value".equals(attrName)) {
            return null;
        }
        PsiAnnotation annotation = PsiTreeUtil.getParentOfType(pair, PsiAnnotation.class, true);
        return isStatementAnnotation(annotation) ? pair : null;
    }

    /**
     * Checks whether the given annotation is one of MyBatis statement annotations
     * ({@code @Select}, {@code @Insert}, {@code @Update}, {@code @Delete}).
     */
    public static boolean isStatementAnnotation(@Nullable PsiAnnotation annotation) {
        if (annotation == null) {
            return false;
        }
        String qualifiedName = annotation.getQualifiedName();
        if (qualifiedName == null) {
            return false;
        }
        for (Annotation stmt : Annotation.STATEMENT_SYMMETRIES) {
            if (stmt.getQualifiedName().equals(qualifiedName)) {
                return true;
            }
        }
        return false;
    }

    private @NotNull List<HostFragment> collectFragments(@NotNull PsiElement targetRoot, PsiElement @NotNull [] operands) {
        List<HostFragment> fragments = new ArrayList<>();
        if (targetRoot instanceof PsiArrayInitializerMemberValue arrayValue) {
            for (PsiAnnotationMemberValue initializer : arrayValue.getInitializers()) {
                if (initializer instanceof PsiLiteralExpression literal) {
                    addLiteralFragment(literal, "\n", fragments);
                }
            }
        } else {
            for (PsiElement operand : operands) {
                if (operand instanceof PsiLiteralExpression literal) {
                    addLiteralFragment(literal, "", fragments);
                }
            }
        }
        return fragments;
    }

    private void addLiteralFragment(@NotNull PsiLiteralExpression literal,
                                    @NotNull String joinSuffix,
                                    @NotNull List<HostFragment> out) {
        if (!(literal.getValue() instanceof String text)) {
            return;
        }
        if (!(literal instanceof PsiLanguageInjectionHost host) || !host.isValidHost()) {
            return;
        }
        TextRange range = ElementManipulators.getValueTextRange(literal);
        if (range.isEmpty()) {
            return;
        }
        out.add(new HostFragment(host, range, text, joinSuffix));
    }

    private boolean isScriptContent(@NotNull List<HostFragment> fragments) {
        StringBuilder combined = new StringBuilder();
        for (HostFragment fragment : fragments) {
            combined.append(fragment.text);
        }
        return combined.toString().trim().startsWith(SCRIPT_OPEN_TAG);
    }

    private static final class HostFragment {
        private final PsiLanguageInjectionHost host;
        private final TextRange range;
        private final String text;
        private final String joinSuffix;

        private HostFragment(PsiLanguageInjectionHost host, TextRange range, String text, String joinSuffix) {
            this.host = host;
            this.range = range;
            this.text = text;
            this.joinSuffix = joinSuffix;
        }
    }
}
