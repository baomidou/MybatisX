package com.baomidou.plugin.idea.mybatisx.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.Required;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author yanglin
 */
public interface Choose extends DomElement {

    @NotNull
    @Required
    @SubTagList("when")
    public List<When> getWhens();

    @SubTag("otherwise")
    public Otherwise getOtherwise();

}
