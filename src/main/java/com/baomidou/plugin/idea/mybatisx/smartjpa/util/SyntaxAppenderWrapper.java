package com.baomidou.plugin.idea.mybatisx.smartjpa.util;

import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;

import java.util.LinkedList;

public class SyntaxAppenderWrapper {
    /**
     * 缓存不可变更的
     */
    private SyntaxAppender syntaxAppender;
    /**
     * 为了扩展符号追加器的内容的集合
     */
    private LinkedList<SyntaxAppenderWrapper> collector;

    public SyntaxAppenderWrapper(SyntaxAppender syntaxAppender) {
        this.syntaxAppender = syntaxAppender;
        this.collector = new LinkedList<>();
    }


    public SyntaxAppenderWrapper(SyntaxAppender syntaxAppender, LinkedList<SyntaxAppenderWrapper> collector) {
        this.syntaxAppender = syntaxAppender;
        this.collector = collector;
    }

    public void addWrapper(SyntaxAppenderWrapper syntaxAppenderWrapper) {
        this.collector.add(syntaxAppenderWrapper);
    }

    public SyntaxAppender getAppender() {
        return syntaxAppender;
    }

    public LinkedList<SyntaxAppenderWrapper> getCollector() {
        return collector;
    }
}
