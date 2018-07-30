package com.baomidou.plugin.idea.mybatisx.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author yanglin
 */
public interface Configuration extends DomElement {

    @NotNull
    @SubTagList("typeAliases")
    List<TypeAliases> getTypeAliases();

}
