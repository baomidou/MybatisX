package com.baomidou.plugin.idea.mybatisx.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The interface Group one.
 *
 * @author yanglin
 */
public interface GroupOne extends DomElement {

    /**
     * Gets includes.
     *
     * @return the includes
     */
    @NotNull
    @SubTagList("include")
    List<Include> getIncludes();

    /**
     * Gets trims.
     *
     * @return the trims
     */
    @NotNull
    @SubTagList("trim")
    List<Trim> getTrims();

    /**
     * Gets wheres.
     *
     * @return the wheres
     */
    @NotNull
    @SubTagList("where")
    List<Where> getWheres();

    /**
     * Gets sets.
     *
     * @return the sets
     */
    @NotNull
    @SubTagList("set")
    List<Set> getSets();

    /**
     * Gets foreachs.
     *
     * @return the foreachs
     */
    @NotNull
    @SubTagList("foreach")
    List<Foreach> getForeachs();

    /**
     * Gets chooses.
     *
     * @return the chooses
     */
    @NotNull
    @SubTagList("choose")
    List<Choose> getChooses();

    /**
     * Gets ifs.
     *
     * @return the ifs
     */
    @NotNull
    @SubTagList("if")
    List<If> getIfs();

}
