package com.baomidou.plugin.idea.mybatisx.dom.model;

import com.intellij.util.xml.SubTagList;

import java.util.List;

/**
 * @author yanglin
 */
public interface GroupThree extends GroupTwo {

    @SubTagList("selectKey")
    List<SelectKey> getSelectKey();

}
