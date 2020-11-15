package com.baomidou.plugin.idea.mybatisx.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The interface Beans.
 *
 * @author yanglin
 */
public interface Beans extends DomElement {

    /**
     * Gets beans.
     *
     * @return the beans
     */
    @NotNull
    @SubTagList("bean")
    List<Bean> getBeans();

}
