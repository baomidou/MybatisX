package com.baomidou.plugin.idea.mybatisx.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The interface Configuration.
 *
 * @author yanglin
 */
public interface Configuration extends DomElement {

    /**
     * Gets type aliases.
     *
     * @return the type aliases
     */
    @NotNull
    @SubTagList("typeAliases")
    List<TypeAliases> getTypeAliases();

}
