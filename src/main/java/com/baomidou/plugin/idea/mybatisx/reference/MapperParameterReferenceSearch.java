package com.baomidou.plugin.idea.mybatisx.reference;

import com.baomidou.plugin.idea.mybatisx.annotation.Annotation;
import com.baomidou.plugin.idea.mybatisx.injection.MapperAnnotationInjector;
import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiConstantEvaluationHelper;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.PsiSearchScopeUtil;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Enables reverse navigation (Find Usages / Ctrl+Click) from a Mapper {@link PsiParameter}
 * (including {@code @Param("...")}) to {@code #{param}} and {@code ${param}} placeholders inside
 * {@code @Select}, {@code @Insert}, {@code @Update}, and {@code @Delete} annotations.
 */
public class MapperParameterReferenceSearch extends QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters> {

    public MapperParameterReferenceSearch() {
        super(true);
    }

    @Override
    public void processQuery(@NotNull ReferencesSearch.SearchParameters queryParameters,
                             @NotNull Processor<? super PsiReference> consumer) {
        if (!(queryParameters.getElementToSearch() instanceof PsiParameter psiParameter)) {
            return;
        }
        PsiMethod method = PsiTreeUtil.getParentOfType(psiParameter, PsiMethod.class);
        if (method == null) {
            return;
        }

        String paramName = resolveParameterName(psiParameter);
        if (paramName == null || paramName.isEmpty()) {
            return;
        }

        for (PsiAnnotation annotation : method.getAnnotations()) {
            if (!MapperAnnotationInjector.isStatementAnnotation(annotation)) {
                continue;
            }
            Collection<PsiLiteralExpression> literals = PsiTreeUtil.findChildrenOfType(annotation, PsiLiteralExpression.class);
            for (PsiLiteralExpression literal : literals) {
                if (!PsiSearchScopeUtil.isInScope(queryParameters.getEffectiveSearchScope(), literal)) {
                    continue;
                }
                if (!collectHashMarkReferences(literal, psiParameter, paramName, consumer)) {
                    return;
                }
            }
        }
    }

    private @Nullable String resolveParameterName(@NotNull PsiParameter psiParameter) {
        PsiAnnotation paramAnnotation = psiParameter.getAnnotation(Annotation.PARAM.getQualifiedName());
        if (paramAnnotation != null) {
            PsiAnnotationMemberValue value = paramAnnotation.findAttributeValue("value");
            if (value != null) {
                PsiConstantEvaluationHelper helper = JavaPsiFacade.getInstance(psiParameter.getProject())
                    .getConstantEvaluationHelper();
                Object evaluated = helper.computeConstantExpression(value);
                if (evaluated instanceof String str && !str.isEmpty()) {
                    return str;
                }
            }
        }
        return psiParameter.getName();
    }

    private boolean collectHashMarkReferences(@NotNull PsiLiteralExpression literal,
                                              @NotNull PsiParameter psiParameter,
                                              @NotNull String paramName,
                                              @NotNull Processor<? super PsiReference> consumer) {
        String rawText = literal.getText();
        int length = rawText.length();
        int searchFrom = 0;
        while (searchFrom < length) {
            int hashIdx = rawText.indexOf("#{", searchFrom);
            int dollarIdx = rawText.indexOf("${", searchFrom);
            int openIdx = chooseEarliest(hashIdx, dollarIdx);
            if (openIdx == -1) {
                break;
            }
            int closeIdx = rawText.indexOf('}', openIdx + 2);
            if (closeIdx == -1) {
                break;
            }
            int exprStart = skipLeadingSpaces(rawText, openIdx + 2, closeIdx);
            if (matchesParamPrefix(rawText, exprStart, closeIdx, paramName)) {
                TextRange range = new TextRange(exprStart, exprStart + paramName.length());
                PsiReference ref = new PsiReferenceBase<>(literal, range, true) {
                    @Override
                    public @Nullable PsiElement resolve() {
                        return psiParameter;
                    }
                };
                if (!consumer.process(ref)) {
                    return false;
                }
            }
            searchFrom = closeIdx + 1;
        }
        return true;
    }

    private int chooseEarliest(int a, int b) {
        if (a == -1) {
            return b;
        }
        if (b == -1) {
            return a;
        }
        return Math.min(a, b);
    }

    private int skipLeadingSpaces(@NotNull String text, int start, int limit) {
        int i = start;
        while (i < limit && Character.isWhitespace(text.charAt(i))) {
            i++;
        }
        return i;
    }

    private boolean matchesParamPrefix(@NotNull String text, int exprStart, int closeIdx, @NotNull String paramName) {
        int end = exprStart + paramName.length();
        if (end > closeIdx) {
            return false;
        }
        if (!text.regionMatches(exprStart, paramName, 0, paramName.length())) {
            return false;
        }
        if (end == closeIdx) {
            return true;
        }
        char next = text.charAt(end);
        return next == '.' || next == ',' || Character.isWhitespace(next);
    }
}
