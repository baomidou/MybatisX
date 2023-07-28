package com.baomidou.plugin.idea.mybatisx.inspection;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.highlighting.BasicDomElementsInspection;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import com.intellij.util.xml.highlighting.DomHighlightingHelper;

/**
 * <p>
 * Mapper XML 检查
 * </p>
 *
 * @author yanglin
 * @since 2018 -07-30
 */
public class MapperXmlInspection extends BasicDomElementsInspection<DomElement> {

    /**
     * Instantiates a new Mapper xml inspection.
     */
    public MapperXmlInspection() {
        super(DomElement.class);
    }

    @Override
    protected void checkDomElement(DomElement element, DomElementAnnotationHolder holder, DomHighlightingHelper helper) {
        super.checkDomElement(element, holder, helper);
    }

    @Override
    public String getStaticDescription() {
        return "Static MapperXmlInspection";
    }
}
