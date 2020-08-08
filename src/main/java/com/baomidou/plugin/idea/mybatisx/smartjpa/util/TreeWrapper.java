package com.baomidou.plugin.idea.mybatisx.smartjpa.util;

import java.util.LinkedList;

public class TreeWrapper<T> {
    private T current;
    private LinkedList<TreeWrapper<T>> collector;

    public TreeWrapper(T current) {
        this.current = current;
        this.collector = new LinkedList<>();
    }


    public TreeWrapper(T current, LinkedList<TreeWrapper<T>> collector) {
        this.current = current;
        this.collector = collector;
    }

    public void addWrapper(TreeWrapper<T> treeWrapper) {
        this.collector.add(treeWrapper);
    }

    public T getAppender() {
        return current;
    }

    public LinkedList<TreeWrapper<T>> getCollector() {
        return collector;
    }
}
