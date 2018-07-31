package com.baomidou.plugin.idea.mybatisx.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;

import java.util.List;

/**
 * @author yanglin
 */
public interface Constructor extends DomElement {

    @SubTagList("arg")
    List<Arg> getArgs();

    @SubTagList("idArg")
    List<IdArg> getIdArgs();
}
