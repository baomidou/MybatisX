package com.baomidou.plugin.idea.mybatisx.util;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomService;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public final class DomUtils {

    private DomUtils() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @NonNls
    public static <T extends DomElement> Collection<T> findDomElements(@NotNull Project project, Class<T> clazz) {
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        List<DomFileElement<T>> elements = DomService.getInstance().getFileElements(clazz, project, scope);
        return Collections2.transform(elements, new Function<DomFileElement<T>, T>() {
            @Override
            public T apply(DomFileElement<T> input) {
                return input.getRootElement();
            }
        });
    }

    public static boolean isMybatisFile(@Nullable PsiFile file) {
        if (!isXmlFile(file)) {
            return false;
        }
        XmlTag rootTag = ((XmlFile) file).getRootTag();
        return null != rootTag && rootTag.getName().equals("mapper");
    }

    public static boolean isMybatisConfigurationFile(@NotNull PsiFile file) {
        if (!isXmlFile(file)) {
            return false;
        }
        XmlTag rootTag = ((XmlFile) file).getRootTag();
        return null != rootTag && rootTag.getName().equals("configuration");
    }

    public static boolean isBeansFile(@NotNull PsiFile file) {
        if (!isXmlFile(file)) {
            return false;
        }
        XmlTag rootTag = ((XmlFile) file).getRootTag();
        return null != rootTag && rootTag.getName().equals("beans");
    }

    static boolean isXmlFile(@NotNull PsiFile file) {
        return file instanceof XmlFile;
    }

}