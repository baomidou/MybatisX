package com.baomidou.plugin.idea.mybatisx.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.Required;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The interface Choose.
 *
 * @author yanglin
 */
public interface Choose extends DomElement {

    /**
     * Gets whens.
     *
     * @return the whens
     */
    @NotNull
    @Required
    @SubTagList("when")
    List<When> getWhens();

    /**
     * Gets otherwise.
     *
     * @return the otherwise
     */
    @SubTag("otherwise")
    Otherwise getOtherwise();

}
