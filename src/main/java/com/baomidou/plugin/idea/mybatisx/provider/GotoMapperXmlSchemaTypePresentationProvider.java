package com.baomidou.plugin.idea.mybatisx.provider;

import com.baomidou.plugin.idea.mybatisx.util.MapperUtils;
import com.intellij.codeInsight.navigation.GotoTargetPresentationProvider;
import com.intellij.platform.backend.presentation.TargetPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.xml.XmlTagImpl;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.xml.util.XmlUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class GotoMapperXmlSchemaTypePresentationProvider implements GotoTargetPresentationProvider {
    @Override
    public @Nullable TargetPresentation getTargetPresentation(@NotNull PsiElement element, boolean differentNames) {
        if (element instanceof XmlTagImpl xmlTag) {
            if (MapperUtils.isElementWithinMybatisFile(element)) {
                return TargetPresentation
                    .builder(getPresentableTest(xmlTag))
                    .containerText(getContainerText(xmlTag))
                    .presentation();
            }
        }
        return null;
    }

    private static String getPresentableTest(XmlTagImpl element) {
        XmlAttribute attr = element.getAttribute("id", XmlUtil.XML_SCHEMA_URI);
        attr = attr == null ? element.getAttribute("id") : attr;
        return (attr == null || attr.getValue() == null ? element.getName() : attr.getValue());
    }

    private static String getContainerText(XmlTagImpl element) {
        final PsiFile file = element.getContainingFile();
        String databaseId = getDatabaseId(element);
        return databaseId + file.getVirtualFile().getName();
    }

    @NotNull
    private static String getDatabaseId(XmlTagImpl element) {
        final XmlAttribute databaseIdAttr = element.getAttribute("databaseId");
        String databaseId = null;
        if (databaseIdAttr != null) {
            databaseId = databaseIdAttr.getValue() + ",";
        }
        if (databaseId == null) {
            databaseId = "";
        }
        return databaseId;
    }
}
