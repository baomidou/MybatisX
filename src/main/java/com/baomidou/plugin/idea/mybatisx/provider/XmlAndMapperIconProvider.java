package com.baomidou.plugin.idea.mybatisx.provider;

import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.setting.MybatisXSettings;
import com.baomidou.plugin.idea.mybatisx.util.Icons;
import com.baomidou.plugin.idea.mybatisx.util.MapperUtils;
import com.intellij.ide.IconProvider;
import com.intellij.lang.Language;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.Optional;

/**
 * mapper.xml 和 mapperClass 的文件图标修改为骚气的小鸟
 */
public class XmlAndMapperIconProvider extends IconProvider {
    private volatile MybatisXSettings instance;

    @NotNull
    private MybatisXSettings getInstance() {
        if (instance == null) {
            synchronized (XmlAndMapperIconProvider.class) {
                if (instance == null) {
                    instance = MybatisXSettings.getInstance();
                }
            }
        }
        return instance;
    }

    @Override
    public @Nullable
    Icon getIcon(@NotNull PsiElement element, int flags) {
        if (isDefaultIcon()) {
            return null;
        }
        if (isMapperClass(element)) {
            return Icons.MAPPER_CLASS_ICON;
        }
        if (MapperUtils.isElementWithinMybatisFile(element)) {
            return Icons.MAPPER_XML_ICON;
        }
        return null;
    }

    /**
     * XML 和 Mapper 都使用默认图标
     *
     * @return
     */
    private boolean isDefaultIcon() {
        return getInstance().getMapperIcon() != null &&
            MybatisXSettings.MapperIcon.DEFAULT.name()
                .equals(getInstance().getMapperIcon());
    }

    private boolean isMapperClass(@NotNull PsiElement element) {
        Language language = element.getLanguage();
        if (!language.isKindOf(JavaLanguage.INSTANCE)) {
            return false;
        }
        if (!(element instanceof PsiClass)) {
            return false;
        }
        PsiClass mayMapperClass = (PsiClass) element;
        if (!mayMapperClass.isInterface()) {
            return false;
        }

        Collection<Mapper> mappers = MapperUtils.findMappers(element.getProject(), mayMapperClass);
        return mappers.size() > 0;
    }


}
