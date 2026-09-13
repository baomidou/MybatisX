package com.baomidou.plugin.idea.mybatisx.system.refactoring;

import com.baomidou.plugin.idea.mybatisx.dom.model.IdDomElement;
import com.baomidou.plugin.idea.mybatisx.util.JavaUtils;
import com.baomidou.plugin.idea.mybatisx.util.MapperUtils;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.refactoring.safeDelete.NonCodeUsageSearchInfo;
import com.intellij.refactoring.safeDelete.SafeDeleteProcessorDelegateBase;
import com.intellij.refactoring.safeDelete.usageInfo.SafeDeleteReferenceSimpleDeleteUsageInfo;
import com.intellij.refactoring.safeDelete.usageInfo.SafeDeleteReferenceUsageInfo;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author : liushang@zsyjr.com
 * @date : 2021/8/11
 */
public class MapperMethodSafeDeleteProcessor extends SafeDeleteProcessorDelegateBase {


    @Override
    public boolean handlesElement(PsiElement element) {
        // SafeDeleteProcessorDelegate 在 IntelliJ 平台中是独占式匹配（首个 handlesElement 返回 true 的 Delegate 即 break）。
        // 如果此处返回 true，会导致 JavaSafeDeleteProcessor 被完全跳过（Java 方法的调用方、引用与冲突检查失效）。
        // 因此返回 false，交由 Java 处理器完成引用查找，我们在 preprocessUsages 链中注入自定义 UsageInfo 连带删除 XML 标签。
        return false;
    }

    @Override
    public @Nullable NonCodeUsageSearchInfo findUsages(@NotNull PsiElement psiElement, PsiElement @NotNull [] psiElements, @NotNull List<? super UsageInfo> list) {
        return null;
    }

    @Override
    public @Nullable Collection<PsiElement> getAdditionalElementsToDelete(@NotNull PsiElement psiElement, @NotNull Collection<? extends PsiElement> collection, boolean b) {
        return List.of();
    }


    @Override
    public void prepareForDeletion(@NotNull PsiElement element) throws IncorrectOperationException {
        if (element instanceof PsiMethod psiMethod) {
            final XmlTag tag = MapperUtils.findTag(psiMethod.getProject(), psiMethod);
            if (tag != null && tag.isValid()) {
                tag.delete();
            }
        }
    }


    @Nullable
    @Override
    public Collection<String> findConflicts(@NotNull PsiElement element, PsiElement @NotNull [] allElementsToDelete) {
        return null;
    }

    @Nullable
    @Override
    public UsageInfo[] preprocessUsages(@NotNull Project project, UsageInfo @NotNull [] usages) {
        List<UsageInfo> result = new ArrayList<>();
        Set<XmlTag> processedTags = new HashSet<>();
        Set<PsiMethod> mapperMethods = new HashSet<>();

        for (UsageInfo usage : usages) {
            if (usage instanceof SafeDeleteReferenceUsageInfo refUsage) {
                PsiElement referenced = refUsage.getReferencedElement();
                if (referenced instanceof PsiMethod psiMethod) {
                    PsiClass containingClass = psiMethod.getContainingClass();
                    if (containingClass != null && JavaUtils.isElementWithinInterface(containingClass)) {
                        mapperMethods.add(psiMethod);
                    }
                }
            }

            PsiElement usageElement = usage.getElement();
            if (usageElement != null && MapperUtils.isElementWithinMybatisFile(usageElement)) {
                Optional<IdDomElement> idDomElement = MapperUtils.findParentIdDomElement(usageElement);
                if (idDomElement.isPresent()) {
                    XmlTag tag = idDomElement.get().getXmlTag();
                    if (tag != null) {
                        PsiMethod targetMethod = null;
                        if (usage instanceof SafeDeleteReferenceUsageInfo refUsage
                            && refUsage.getReferencedElement() instanceof PsiMethod m) {
                            targetMethod = m;
                        } else {
                            Object idValue = idDomElement.get().getId().getValue();
                            if (idValue instanceof PsiMethod m) {
                                targetMethod = m;
                            }
                        }

                        if (targetMethod != null) {
                            mapperMethods.add(targetMethod);
                            if (processedTags.add(tag)) {
                                result.add(new SafeDeleteXmlTagUsageInfo(tag, targetMethod));
                            }
                            // 移除 XML 引用原本的 Usage（如 SafeDeleteReferenceJavaDeleteUsageInfo），避免被报告为不安全引用冲突
                            continue;
                        }
                    }
                }
            }

            result.add(usage);
        }

        // 兜底补全：针对已识别的被删除 Mapper 方法，补充可能尚未包含在 usages 中的对应 XML 标签
        for (PsiMethod psiMethod : mapperMethods) {
            Collection<XmlElement> tags = MapperUtils.findTags(project, psiMethod);
            for (XmlElement xmlElement : tags) {
                if (xmlElement instanceof XmlTag tag && processedTags.add(tag)) {
                    result.add(new SafeDeleteXmlTagUsageInfo(tag, psiMethod));
                }
            }
        }

        return result.toArray(new UsageInfo[0]);
    }


    @Override
    public boolean isToSearchInComments(PsiElement element) {
        return false;
    }

    @Override
    public void setToSearchInComments(PsiElement element, boolean enabled) {

    }

    @Override
    public boolean isToSearchForTextOccurrences(PsiElement element) {
        return false;
    }

    @Override
    public void setToSearchForTextOccurrences(PsiElement element, boolean enabled) {

    }


    @Override
    public @Nullable Collection<? extends PsiElement> getElementsToSearch(@NotNull PsiElement psiElement, @Nullable Module module, @NotNull Collection<? extends PsiElement> collection) {
        return List.of();
    }

    /**
     * 自定义 XML 标签安全删除 UsageInfo
     */
    public static class SafeDeleteXmlTagUsageInfo extends SafeDeleteReferenceSimpleDeleteUsageInfo {

        public SafeDeleteXmlTagUsageInfo(@NotNull XmlTag tag, @NotNull PsiMethod method) {
            super(tag, method, true);
        }

        @Override
        public void deleteElement() throws IncorrectOperationException {
            if (isSafeDelete()) {
                PsiElement element = getElement();
                if (element != null && element.isValid()) {
                    element.delete();
                }
            }
        }
    }
}
