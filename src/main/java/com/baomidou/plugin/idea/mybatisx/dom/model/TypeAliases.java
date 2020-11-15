package com.baomidou.plugin.idea.mybatisx.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The interface Type aliases.
 *
 * @author yanglin
 */
public interface TypeAliases extends DomElement {

    /**
     * Gets type alias.
     *
     * @return the type alias
     */
    @NotNull
    @SubTagList("typeAlias")
    List<TypeAlias> getTypeAlias();

    /**
     * Gets packages.
     *
     * @return the packages
     */
    @NotNull
    @SubTagList("package")
    List<Package> getPackages();

}
