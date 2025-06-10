package com.baomidou.plugin.idea.mybatisx.inspection;

import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.xml.XmlFileImpl;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.highlighting.BasicDomElementsInspection;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import com.intellij.util.xml.highlighting.DomHighlightingHelper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Mapper XML 检查
 * </p>
 *
 * @author yanglin
 * @since 2018 -07-30
 */
public class MapperXmlInspection extends BasicDomElementsInspection<DomElement> {
    private static final Logger logger = LoggerFactory.getLogger(MapperXmlInspection.class);

    /**
     * Instantiates a new Mapper xml inspection.
     */
    public MapperXmlInspection() {
        super(DomElement.class);
    }

    @Override
    protected void checkDomElement(DomElement element, DomElementAnnotationHolder holder, DomHighlightingHelper helper) {
        try {
            super.checkDomElement(element, holder, helper);
        } catch (Exception e) {
            logger.error("checkDomElement error:{}", e.getMessage());
        }
    }

    @Override
    public String getStaticDescription() {
        return "Static MapperXmlInspection";
    }

    @Override
    public boolean isAvailableForFile(@NotNull PsiFile file) {
        if (file instanceof XmlFile) {
            XmlTag rootTag = ((XmlFile) file).getRootTag();
            return rootTag != null && rootTag.getName().equals("mapper");
        }
        return false;
    }
}
