package com.baomidou.plugin.idea.mybatisx.smartjpa.common;


import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CompositeAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.TreeWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiParameter;

import java.util.LinkedList;

public class TemplateResolver {

    public String getTemplateText(LinkedList<SyntaxAppender> current,
                                  String tableName, PsiClass entityClass,
                                  LinkedList<PsiParameter> parameters, LinkedList<TreeWrapper<SyntaxAppender>> collector) {
        SyntaxAppender syntaxAppender = null;
        if (current.size() == 1) {
            syntaxAppender = current.poll();
        } else if (current.size() > 1) {
            syntaxAppender = new CompositeAppender(current.toArray(new SyntaxAppender[0]));
        } else {
            return "";
        }
        return syntaxAppender.getTemplateText(tableName, entityClass, parameters, collector);

    }



}
