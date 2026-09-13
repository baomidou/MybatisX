package com.baomidou.plugin.idea.mybatisx.inspection;

import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.highlighting.BasicDomElementsInspection;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import com.intellij.util.xml.highlighting.DomHighlightingHelper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

/**
 * <p>
 * Mapper XML 检查
 * </p>
 *
 * @author yanglin
 * @since 2018 -07-30
 */
@Slf4j
public class MapperXmlInspection extends BasicDomElementsInspection<DomElement> {

    /**
     * Instantiates a new Mapper xml inspection.
     */
    public MapperXmlInspection() {
        super(DomElement.class);
    }

    @Override
    protected void checkDomElement(@NotNull DomElement element, @NotNull DomElementAnnotationHolder holder, @NotNull DomHighlightingHelper helper) {
        try {
            super.checkDomElement(element, holder, helper);
        } catch (Exception e) {
            log.error("checkDomElement error:{}", e.getMessage());
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
