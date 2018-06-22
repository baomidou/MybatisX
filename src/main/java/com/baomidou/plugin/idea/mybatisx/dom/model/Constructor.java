package com.baomidou.plugin.idea.mybatisx.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;

import java.util.List;

/**
 * @author yanglin
 */
public interface Constructor extends DomElement {

    @SubTagList("arg")
    public List<Arg> getArgs();

    @SubTagList("idArg")
    public List<IdArg> getIdArgs();
}
