package com.baomidou.plugin.idea.mybatisx.provider;

import com.baomidou.plugin.idea.mybatisx.util.MapperUtils;
import com.intellij.codeInsight.navigation.GotoTargetHandler;
import com.intellij.codeInsight.navigation.GotoTargetRendererProvider;
import com.intellij.ide.util.PsiElementListCellRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.xml.XmlTagImpl;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.xml.util.XmlUtil;
import org.jetbrains.annotations.NotNull;

public class GotoMapperXmlSchemaTypeRendererProvider implements GotoTargetRendererProvider {
    @Override
    public PsiElementListCellRenderer getRenderer(@NotNull PsiElement element, @NotNull GotoTargetHandler.GotoData gotoData) {
        if (element instanceof XmlTagImpl) {
            if (MapperUtils.isElementWithinMybatisFile(element)) {
                return new MyRenderer();
            }
        }
        return null;
    }

    public static class MyRenderer extends PsiElementListCellRenderer<XmlTagImpl> {

        @Override
        public String getElementText(XmlTagImpl element) {
            XmlAttribute attr = element.getAttribute("id", XmlUtil.XML_SCHEMA_URI);
            attr = attr == null ? element.getAttribute("id") : attr;
            return (attr == null || attr.getValue() == null ? element.getName() : attr.getValue());
        }

        @Override
        protected String getContainerText(XmlTagImpl element, String name) {
            final PsiFile file = element.getContainingFile();
            return "(" + file.getVirtualFile().getPath() + ")";
        }

        @Override
        protected int getIconFlags() {
            return 0;
        }
    }
}
