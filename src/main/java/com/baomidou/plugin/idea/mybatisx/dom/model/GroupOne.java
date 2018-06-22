package com.baomidou.plugin.idea.mybatisx.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author yanglin
 */
public interface GroupOne extends DomElement {

    @NotNull
    @SubTagList("include")
    public List<Include> getIncludes();

    @NotNull
    @SubTagList("trim")
    public List<Trim> getTrims();

    @NotNull
    @SubTagList("where")
    public List<Where> getWheres();

    @NotNull
    @SubTagList("set")
    public List<Set> getSets();

    @NotNull
    @SubTagList("foreach")
    public List<Foreach> getForeachs();

    @NotNull
    @SubTagList("choose")
    public List<Choose> getChooses();

    @NotNull
    @SubTagList("if")
    public List<If> getIfs();

}
