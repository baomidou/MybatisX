package com.baomidou.plugin.idea.mybatisx.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author yanglin
 */
public interface TypeAliases extends DomElement {

    @NotNull
    @SubTagList("typeAlias")
    List<TypeAlias> getTypeAlias();

    @NotNull
    @SubTagList("package")
    List<Package> getPackages();

}
