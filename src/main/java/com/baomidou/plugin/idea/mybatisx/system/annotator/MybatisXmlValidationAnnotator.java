package com.baomidou.plugin.idea.mybatisx.system.annotator;

import com.baomidou.plugin.idea.mybatisx.util.OgnlUtils;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MybatisXmlValidationAnnotator implements Annotator {

    private static final PsiElementPattern.Capture<XmlAttributeValue> TEST_PATTERN = PlatformPatterns.psiElement(XmlAttributeValue.class)
        .withParent(PlatformPatterns.psiElement(XmlAttribute.class)
            .withName("test")
            .withParent(PlatformPatterns.psiElement(XmlTag.class)
                .withName(StandardPatterns.string().oneOf("if", "when"))));

    private static final PsiElementPattern.Capture<XmlAttributeValue> COLLECTION_PATTERN = PlatformPatterns.psiElement(XmlAttributeValue.class)
        .withParent(PlatformPatterns.psiElement(XmlAttribute.class)
            .withName("collection")
            .withParent(PlatformPatterns.psiElement(XmlTag.class)
                .withName("foreach")));

    private static final Set<String> BOOLEAN_OPERATORS = new HashSet<>(Arrays.asList(
        "==", "!=", ">", "<", ">=", "<=", " and ", " or ", "not ", "!", "instanceof", "?", ":", "new "
    ));

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof XmlAttributeValue)) {
            return;
        }
        XmlAttributeValue attributeValue = (XmlAttributeValue) element;

        if (TEST_PATTERN.accepts(element)) {
            validateBooleanExpression(attributeValue, holder);
        } else if (COLLECTION_PATTERN.accepts(element)) {
            validateCollectionExpression(attributeValue, holder);
        }
    }

    private void validateBooleanExpression(XmlAttributeValue attributeValue, AnnotationHolder holder) {
        String text = attributeValue.getValue(); // Gets value without quotes usually, relying on getBytes? No, getValue is string.
        // XmlAttributeValue.getValue() returns unquoted value.
        if (text == null || text.trim().isEmpty()) return;

        // 0. Pre-processing: remove strings for syntax checks
        String noStr = text.replaceAll("(['\"])(?:\\\\.|[^\\\\])*?\\1", "");

        // 1. Check for ===
        if (noStr.contains("===")) {
             holder.newAnnotation(HighlightSeverity.ERROR, "Unsupported operator '==='; use '==' or 'eq'")
                    .range(attributeValue)
                    .create();
             return;
        }

        // 2. Check for assignment = 
        // We look for = that is NOT ==, NOT !=, NOT >=, NOT <=.
        if (noStr.matches(".*(?<![=!<>])=(?![=]).*")) {
             holder.newAnnotation(HighlightSeverity.ERROR, "Assignment operator '=' is not allowed; use '=='")
                    .range(attributeValue)
                    .create();
             return;
        }

        // 3. Check for missing operator (Word Space Word)
        if (hasMissingOperator(noStr)) {
            holder.newAnnotation(HighlightSeverity.ERROR, "test里面必须是一个条件表达式")
                .range(attributeValue)
                .create();
            return;
        }

        // If it looks like an expression with operators, we assume it evaluates to boolean (or at least valid OGNL logic)
        // We only validate if it looks like a single variable reference that resolved to non-boolean.
        if (containsBooleanOperators(text)) {
            return;
        }

        // Try to resolve the text as a path
        PsiElement resolved = OgnlUtils.resolveExpression(attributeValue, text);
        if (resolved != null) {
            PsiType type = OgnlUtils.getType(resolved);
            if (type != null && !isBooleanType(type)) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Expression must verify boolean (found: " + type.getPresentableText() + ")")
                    .range(attributeValue)
                    .create();
            }
        }
    }

    // List of OGNL keywords that can validly appear adjacent to other tokens as operators
    private static final Set<String> OGNL_KEYWORDS = new HashSet<>(Arrays.asList(
        "and", "or", "eq", "neq", "lt", "gt", "lte", "gte", "not", "new", "instanceof",
        "mod", "div", "band", "bor", "xor", "shl", "shr", "ushr"
    ));

    private boolean hasMissingOperator(String text) {
        // 2. Remove balanced content in () [] {}
        String masked = maskBalanced(text);
        
        // 3. Scan for "Token Space Token" pattern
        // Pattern matches:
        // Group 1: Preceding token (word or closing bracket/paren)
        // Group 2: Following token (word starting with alphanum)
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("([\\w\\])]+)\\s+([\\w]+)").matcher(masked);
        
        while (matcher.find()) {
            String left = matcher.group(1);
            String right = matcher.group(2);
            
            // If left ends with bracket/paren, we just check the right side.
            // e.g. "size() 1" -> left=")", right="1". 
            // If right is not an operator, it's missing an operator.
            // e.g. "size() and" -> right="and" (OK).
            
            boolean leftIsOp = false;
            if (left.matches("[\\w]+")) {
                leftIsOp = OGNL_KEYWORDS.contains(left);
            } else {
                // left is ")" or "]"
                leftIsOp = false; 
            }
            
            boolean rightIsOp = OGNL_KEYWORDS.contains(right);

            // If neither is an operator keyword, we have two adjacent values/identifiers -> Error
            if (!leftIsOp && !rightIsOp) {
                return true;
            }
        }
        return false;
    }

    private String maskBalanced(String text) {
        StringBuilder sb = new StringBuilder();
        int depth = 0;
        for (char c : text.toCharArray()) {
            if (c == '(' || c == '[' || c == '{') {
                depth++;
                sb.append(c);
            } else if (c == ')' || c == ']' || c == '}') {
                depth--;
                sb.append(c);
            } else {
                if (depth == 0) {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    private void validateCollectionExpression(XmlAttributeValue attributeValue, AnnotationHolder holder) {
        String text = attributeValue.getValue();
        if (text == null || text.trim().isEmpty()) return;

        PsiElement resolved = OgnlUtils.resolveExpression(attributeValue, text);
        if (resolved != null) {
            PsiType type = OgnlUtils.getType(resolved);
            if (type != null && !isCollectionOrArray(type, attributeValue.getProject())) {
                 holder.newAnnotation(HighlightSeverity.ERROR, "Expression must be Collection or Array (found: " + type.getPresentableText() + ")")
                    .range(attributeValue)
                    .create();
            }
        }
    }

    private boolean containsBooleanOperators(String text) {
        for (String op : BOOLEAN_OPERATORS) {
            if (text.contains(op)) return true;
        }
        return false;
    }

    private boolean isBooleanType(PsiType type) {
        return PsiType.BOOLEAN.equals(type) || "java.lang.Boolean".equals(type.getCanonicalText());
    }

    private boolean isCollectionOrArray(PsiType type, com.intellij.openapi.project.Project project) {
        if (type instanceof PsiArrayType) return true;
        
        PsiClass collectionClass = JavaPsiFacade.getInstance(project).findClass("java.util.Collection", GlobalSearchScope.allScope(project));
        PsiClass mapClass = JavaPsiFacade.getInstance(project).findClass("java.util.Map", GlobalSearchScope.allScope(project));
        
        if (type instanceof PsiClassType) {
            PsiClass psiClass = ((PsiClassType) type).resolve();
            if (psiClass != null) {
                if (collectionClass != null && (collectionClass.isEquivalentTo(psiClass) || psiClass.isInheritor(collectionClass, true))) {
                    return true;
                }
                if (mapClass != null && (mapClass.isEquivalentTo(psiClass) || psiClass.isInheritor(mapClass, true))) {
                    return true;
                }
            }
        }
        return false;
    }
}
