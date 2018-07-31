package com.baomidou.plugin.idea.mybatisx.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;

import java.util.List;

/**
 * @author yanglin
 */
public interface Cache extends DomElement {

    @SubTagList("property")
    List<Property> getProperties();

}
