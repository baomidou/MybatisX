package com.baomidou.plugin.idea.mybatisx.inspection;

import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CompositeAppender;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.highlighting.BasicDomElementsInspection;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import com.intellij.util.xml.highlighting.DomHighlightingHelper;
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
}
