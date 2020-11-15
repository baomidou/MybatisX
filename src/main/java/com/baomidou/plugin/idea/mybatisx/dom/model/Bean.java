package com.baomidou.plugin.idea.mybatisx.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The interface Bean.
 *
 * @author yanglin
 */
public interface Bean extends DomElement {

    /**
     * Gets bean properties.
     *
     * @return the bean properties
     */
    @NotNull
    @SubTagList("property")
    List<BeanProperty> getBeanProperties();

}
