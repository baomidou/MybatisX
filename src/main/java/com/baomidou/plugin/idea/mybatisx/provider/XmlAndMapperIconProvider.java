package com.baomidou.plugin.idea.mybatisx.provider;

import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.setting.MybatisSetting;
import com.baomidou.plugin.idea.mybatisx.util.Icons;
import com.baomidou.plugin.idea.mybatisx.util.MapperUtils;
import com.intellij.ide.IconProvider;
import com.intellij.lang.Language;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Optional;

/**
 * mapper.xml 和 mapperClass 的文件图标修改为骚气的小鸟
 */
public class XmlAndMapperIconProvider extends IconProvider {
    MybatisSetting instance = MybatisSetting.getInstance();

    @Override
    public @Nullable Icon getIcon(@NotNull PsiElement element, int flags) {
        if(instance.getMapperIcon() != null &&
            MybatisSetting.MapperIcon.DEFAULT.name().equals(instance.getMapperIcon())){
            return null;
        }
        Language language = element.getLanguage();
        if (language.is(JavaLanguage.INSTANCE)) {
            if (element instanceof PsiClass) {
                PsiClass mayMapperClass = (PsiClass) element;
                Optional<Mapper> firstMapper = MapperUtils.findFirstMapper(element.getProject(), mayMapperClass);
                if (firstMapper.isPresent()) {
                    return Icons.MAPPER_CLASS_ICON;
                }
                logger.info("mapper class icon , element: {}", element);
            }
        }
        if (MapperUtils.isElementWithinMybatisFile(element)) {
            logger.info("mapper xml icon , element: {}", element);
            return Icons.MAPPER_XML_ICON;
        }
        return null;
    }

    /**
     * The Logger.
     */
    Logger logger = LoggerFactory.getLogger(XmlAndMapperIconProvider.class);
}
