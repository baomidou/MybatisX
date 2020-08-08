package com.baomidou.plugin.idea.mybatisx.smartjpa.util;


import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;

import java.util.LinkedList;
import java.util.Stack;

public class SyntaxAppenderTreeUtil {
    /**
     * 转成树还有一些BUG。  连续的区域问题
     * @param jpaStringList
     * @return
     */
    public LinkedList<TreeWrapper<SyntaxAppender>> toTree(LinkedList<SyntaxAppender> jpaStringList) {


        LinkedList<TreeWrapper<SyntaxAppender>> list = new LinkedList<>();
        SyntaxAppender current = null;
        while ((current = jpaStringList.poll()) != null) {
            Stack<SyntaxAppender> treeHelp = new Stack<>();
            TreeWrapper<SyntaxAppender> treeWrapper = new TreeWrapper<>(current);
            current.toTree(jpaStringList, treeHelp, treeWrapper);
            list.add(treeWrapper);
        }
        return list;
    }

}
