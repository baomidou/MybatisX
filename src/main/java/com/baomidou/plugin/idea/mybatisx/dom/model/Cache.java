package com.baomidou.plugin.idea.mybatisx.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;

import java.util.List;

/**
 * The interface Cache.
 *
 * @author yanglin
 */
public interface Cache extends DomElement {

    /**
     * Gets properties.
     *
     * @return the properties
     */
    @SubTagList("property")
    List<Property> getProperties();

}
