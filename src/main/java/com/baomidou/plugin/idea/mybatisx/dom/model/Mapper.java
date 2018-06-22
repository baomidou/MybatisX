package com.baomidou.plugin.idea.mybatisx.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;
import com.intellij.util.xml.Required;
import com.intellij.util.xml.SubTagList;
import com.intellij.util.xml.SubTagsList;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author yanglin
 */
public interface Mapper extends DomElement {

    @NotNull
    @SubTagsList({"insert", "update", "delete", "select"})
    public List<IdDomElement> getDaoElements();

    @Required
    @NameValue
    @NotNull
    @Attribute("namespace")
    public GenericAttributeValue<String> getNamespace();

    @NotNull
    @SubTagList("resultMap")
    public List<ResultMap> getResultMaps();

    @NotNull
    @SubTagList("parameterMap")
    public List<ParameterMap> getParameterMaps();

    @NotNull
    @SubTagList("sql")
    public List<Sql> getSqls();

    @NotNull
    @SubTagList("insert")
    public List<Insert> getInserts();

    @NotNull
    @SubTagList("update")
    public List<Update> getUpdates();

    @NotNull
    @SubTagList("delete")
    public List<Delete> getDeletes();

    @NotNull
    @SubTagList("select")
    public List<Select> getSelects();

    @SubTagList("select")
    public Select addSelect();

    @SubTagList("update")
    public Update addUpdate();

    @SubTagList("insert")
    public Insert addInsert();

    @SubTagList("delete")
    public Delete addDelete();
}
