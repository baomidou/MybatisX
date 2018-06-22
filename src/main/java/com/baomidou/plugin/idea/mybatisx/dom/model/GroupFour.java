package com.baomidou.plugin.idea.mybatisx.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;

import java.util.List;

/**
 * @author yanglin
 */
public interface GroupFour extends DomElement {

    @SubTag("constructor")
    public Constructor getConstructor();

    @SubTagList("id")
    public List<Id> getIds();

    @SubTagList("result")
    public List<Result> getResults();

    @SubTagList("association")
    public List<Association> getAssociations();

    @SubTagList("collection")
    public List<Collection> getCollections();

    @SubTag("discriminator")
    public Discriminator getDiscriminator();
}
