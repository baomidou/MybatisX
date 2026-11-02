package com.baomidou.plugin.idea.mybatisx.dom.converter;

import org.jetbrains.annotations.NotNull;
import com.baomidou.plugin.idea.mybatisx.util.JavaUtils;
import com.baomidou.plugin.idea.mybatisx.util.MapperNamespaceNameUtils;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.JavaResolveResult;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.PsiClassConverter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * 加入命名空间检查, 当mapper.xml和mapper.class不匹配时,
 * 对mapper.xml的namespace标签标记红色下划线
 */
public class NamespaceConverter extends ConverterAdaptor<PsiClass> implements CustomReferenceConverter<PsiClass> {

    private final PsiClassConverter delegate = new PsiClassConverter();

    /**
     * 转换成类
     *
     * @param id
     * @param context
     * @return
     */
    @Nullable
    @Override
    public PsiClass fromString(@Nullable @NonNls String id, @NotNull ConvertContext context) {
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        Optional<PsiClass> clazz = JavaUtils.findClazz(context.getProject(), id);
        return clazz.orElse(null);
    }

    @Override
    public void bindReference(GenericDomValue<PsiClass> genericValue, ConvertContext context, PsiElement newTarget) {
        if (newTarget instanceof PsiClass psiClass) {
            final String qualifiedName = psiClass.getQualifiedName();
            genericValue.setStringValue(qualifiedName);
        }
    }

    /**
     * namespace 输入时的类名提示，优先通过当前 Mapper 文件名分词匹配到的接口排在前面
     */
    @NotNull
    @Override
    public PsiReference[] createReferences(GenericDomValue<PsiClass> value, PsiElement element, ConvertContext context) {
        PsiReference[] originalReferences = delegate.createReferences(value, element, context);
        if (originalReferences == null || originalReferences.length == 0) {
            return new PsiReference[]{new NamespaceReferenceWrapper(null, element, context, value)};
        }
        PsiReference[] wrapped = new PsiReference[originalReferences.length];
        for (int i = 0; i < originalReferences.length; i++) {
            wrapped[i] = new NamespaceReferenceWrapper(originalReferences[i], element, context, value);
        }
        return wrapped;
    }

    private static class NamespaceReferenceWrapper implements PsiReference, PsiJavaReference {
        @Nullable
        private final PsiReference delegate;
        @NotNull
        private final PsiElement element;
        @NotNull
        private final ConvertContext context;
        @NotNull
        private final GenericDomValue<PsiClass> value;

        public NamespaceReferenceWrapper(@Nullable PsiReference delegate,
                                         @NotNull PsiElement element,
                                         @NotNull ConvertContext context,
                                         @NotNull GenericDomValue<PsiClass> value) {
            this.delegate = delegate;
            this.element = element;
            this.context = context;
            this.value = value;
        }

        @NotNull
        @Override
        public PsiElement getElement() {
            return delegate != null ? delegate.getElement() : element;
        }

        @NotNull
        @Override
        public TextRange getRangeInElement() {
            if (delegate != null) {
                return delegate.getRangeInElement();
            }
            return ElementManipulators.getValueTextRange(element);
        }

        @Nullable
        @Override
        public PsiElement resolve() {
            return delegate != null ? delegate.resolve() : null;
        }

        @NotNull
        @Override
        public String getCanonicalText() {
            return delegate != null ? delegate.getCanonicalText() : "";
        }

        @Override
        public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
            return delegate != null ? delegate.handleElementRename(newElementName) : element;
        }

        @Override
        public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
            return delegate != null ? delegate.bindToElement(element) : this.element;
        }

        @Override
        public boolean isReferenceTo(@NotNull PsiElement element) {
            return delegate != null && delegate.isReferenceTo(element);
        }

        @Override
        public boolean isSoft() {
            return delegate == null || delegate.isSoft();
        }

        @NotNull
        @Override
        public JavaResolveResult advancedResolve(boolean incompleteCode) {
            if (delegate instanceof PsiJavaReference) {
                return ((PsiJavaReference) delegate).advancedResolve(incompleteCode);
            }
            return JavaResolveResult.EMPTY;
        }

        @NotNull
        @Override
        public JavaResolveResult[] multiResolve(boolean incompleteCode) {
            if (delegate instanceof PsiJavaReference) {
                return ((PsiJavaReference) delegate).multiResolve(incompleteCode);
            }
            return JavaResolveResult.EMPTY_ARRAY;
        }

        @Override
        public void processVariants(@NotNull PsiScopeProcessor processor) {
            if (delegate instanceof PsiJavaReference) {
                ((PsiJavaReference) delegate).processVariants(processor);
            }
        }

        @NotNull
        @Override
        public Object[] getVariants() {
            PsiElement currentElement = getElement();
            PsiFile file = currentElement.getContainingFile();
            if (file == null) {
                return delegate != null ? delegate.getVariants() : ArrayUtil.EMPTY_OBJECT_ARRAY;
            }

            Project project = currentElement.getProject();
            Module currentModule = ModuleUtilCore.findModuleForPsiElement(currentElement);
            String fileName = file.getName();

            Set<String> candidateNames = MapperNamespaceNameUtils.getCandidateClassNames(fileName);
            List<String> businessTokens = MapperNamespaceNameUtils.extractBusinessTokens(
                MapperNamespaceNameUtils.extractBaseName(fileName)
            );

            GlobalSearchScope projectScope = GlobalSearchScope.projectScope(project);
            PsiShortNamesCache cache = PsiShortNamesCache.getInstance(project);

            Set<PsiClass> matchedClasses = new LinkedHashSet<>();

            // 1. 优先检索预测的短接口名（高精度匹配）
            for (String candidateName : candidateNames) {
                PsiClass[] classes = cache.getClassesByName(candidateName, projectScope);
                for (PsiClass clazz : classes) {
                    if (MapperNamespaceNameUtils.isCandidateInterface(clazz)) {
                        matchedClasses.add(clazz);
                    }
                }
            }

            // 2. 根据业务分词检索其它相关接口
            if (!businessTokens.isEmpty()) {
                String[] allClassNames = cache.getAllClassNames();
                for (String className : allClassNames) {
                    if (candidateNames.contains(className)) {
                        continue;
                    }
                    String lower = className.toLowerCase(Locale.ROOT);
                    for (String token : businessTokens) {
                        if (lower.contains(token.toLowerCase(Locale.ROOT))) {
                            PsiClass[] classes = cache.getClassesByName(className, projectScope);
                            for (PsiClass clazz : classes) {
                                if (MapperNamespaceNameUtils.isCandidateInterface(clazz)) {
                                    matchedClasses.add(clazz);
                                }
                            }
                            break;
                        }
                    }
                }
            }

            List<LookupElement> resultList = new ArrayList<>();
            Set<String> addedQualifiedNames = new HashSet<>();

            // 3. 构建置顶全限定名提示项并加权
            for (PsiClass psiClass : matchedClasses) {
                String qualifiedName = psiClass.getQualifiedName();
                if (qualifiedName == null || !addedQualifiedNames.add(qualifiedName)) {
                    continue;
                }
                Module classModule = ModuleUtilCore.findModuleForPsiElement(psiClass);
                boolean inCurrentModule = (currentModule != null && currentModule.equals(classModule));
                double priority = MapperNamespaceNameUtils.calculatePriority(psiClass, fileName, inCurrentModule);
                if (priority < 0) {
                    continue;
                }

                LookupElementBuilder builder = LookupElementBuilder.create(psiClass, qualifiedName)
                    .withIcon(psiClass.getIcon(0))
                    .withLookupString(psiClass.getName() != null ? psiClass.getName() : qualifiedName)
                    .withLookupString(qualifiedName)
                    .withPresentableText(qualifiedName)
                    .withInsertHandler((insertionContext, item) -> {
                        PsiElement elt = insertionContext.getFile().findElementAt(insertionContext.getStartOffset());
                        XmlAttributeValue attrValue = PsiTreeUtil.getParentOfType(elt, XmlAttributeValue.class);
                        if (attrValue != null) {
                            TextRange range = ElementManipulators.getValueTextRange(attrValue);
                            TextRange absRange = range.shiftRight(attrValue.getTextRange().getStartOffset());
                            insertionContext.getDocument().replaceString(absRange.getStartOffset(), absRange.getEndOffset(), qualifiedName);
                            insertionContext.getEditor().getCaretModel().moveToOffset(absRange.getStartOffset() + qualifiedName.length());
                        }
                    });

                LookupElement prioritized = PrioritizedLookupElement.withPriority(builder, priority);
                resultList.add(prioritized);
            }

            // 4. 处理原委托引用自带的变体（过滤掉非接口的普通类，保留包名和其它合法接口）
            if (delegate != null) {
                Object[] delegateVariants = delegate.getVariants();
                if (delegateVariants != null) {
                    for (Object var : delegateVariants) {
                        if (var instanceof LookupElement) {
                            LookupElement le = (LookupElement) var;
                            Object obj = le.getObject();
                            if (obj instanceof PsiClass) {
                                PsiClass pc = (PsiClass) obj;
                                if (!MapperNamespaceNameUtils.isCandidateInterface(pc)) {
                                    continue; // 严格过滤非接口类
                                }
                                if (pc.getQualifiedName() != null && addedQualifiedNames.contains(pc.getQualifiedName())) {
                                    continue; // 已作为高优先级加入
                                }
                            }
                            resultList.add(le);
                        } else if (var instanceof PsiClass) {
                            PsiClass pc = (PsiClass) var;
                            if (!MapperNamespaceNameUtils.isCandidateInterface(pc)) {
                                continue; // 严格过滤非接口类
                            }
                            if (pc.getQualifiedName() != null && addedQualifiedNames.contains(pc.getQualifiedName())) {
                                continue;
                            }
                            resultList.add(LookupElementBuilder.create(pc));
                        } else {
                            resultList.add(LookupElementBuilder.create(var.toString()));
                        }
                    }
                }
            }

            return resultList.toArray();
        }
    }
}
