package com.baomidou.plugin.idea.mybatisx.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.Required;
import com.intellij.util.xml.SubTagList;

import java.util.List;

/**
 * The interface Discriminator.
 *
 * @author yanglin
 */
public interface Discriminator extends DomElement {

    /**
     * Gets cases.
     *
     * @return the cases
     */
    @Required
    @SubTagList("case")
    List<Case> getCases();

}
