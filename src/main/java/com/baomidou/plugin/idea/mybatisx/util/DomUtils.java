package com.baomidou.plugin.idea.mybatisx.util;

import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

/**
 * The type Dom utils.
 */
public final class DomUtils {

    private DomUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Find dom elements collection.
     *
     * @param <T>     the type parameter
     * @param project the project
     * @param clazz   the clazz
     * @return the collection
     */
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

    /**
     * <p>
     * 判断是否为 Mybatis XML 文件
     * </p>
     *
     * @param file 判断文件
     * @return boolean
     */
    public static boolean isMybatisFile(@Nullable PsiFile file) {
        if(file == null){
            return false;
        }
        if (!isXmlFile(file)) {
            return false;
        }
        XmlTag rootTag = ((XmlFile) file).getRootTag();
        if(rootTag == null){
            return false;
        }
        if(!rootTag.getName().equals("mapper")){
            return false;
        }
        return true;
    }

    /**
     * Is mybatis configuration file boolean.
     *
     * @param file the file
     * @return the boolean
     */
    public static boolean isMybatisConfigurationFile(@NotNull PsiFile file) {
        if (!isXmlFile(file)) {
            return false;
        }
        XmlTag rootTag = ((XmlFile) file).getRootTag();
        return null != rootTag && rootTag.getName().equals("configuration");
    }

    /**
     * Is beans file boolean.
     *
     * @param file the file
     * @return the boolean
     */
    public static boolean isBeansFile(@NotNull PsiFile file) {
        if (!isXmlFile(file)) {
            return false;
        }
        XmlTag rootTag = ((XmlFile) file).getRootTag();
        return null != rootTag && rootTag.getName().equals("beans");
    }

    /**
     * Is xml file boolean.
     *
     * @param file the file
     * @return the boolean
     */
    static boolean isXmlFile(@NotNull PsiFile file) {
        return file instanceof XmlFile;
    }

}
