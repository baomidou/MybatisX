package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager;

import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.AreaSequence;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomAreaAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TypeDescriptor;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class StatementBlock {

    /**
     * 标签名称
     */
    private String tagName;
    /**
     * 结果集区域
     */
    private SyntaxAppenderFactory resultAppenderFactory;
    /**
     * 条件区域
     */
    private SyntaxAppenderFactory conditionAppenderFactory;
    /**
     * 排序区域
     */
    private SyntaxAppenderFactory sortAppenderFactory;
    /**
     * 返回值类型
     */
    private TypeDescriptor returnDescriptor;

    public SyntaxAppenderFactory getResultAppenderFactory() {
        return resultAppenderFactory;
    }

    public void setResultAppenderFactory(SyntaxAppenderFactory resultAppenderFactory) {
        this.resultAppenderFactory = resultAppenderFactory;
    }

    public SyntaxAppenderFactory getConditionAppenderFactory() {
        return conditionAppenderFactory;
    }

    public void setConditionAppenderFactory(SyntaxAppenderFactory conditionAppenderFactory) {
        this.conditionAppenderFactory = conditionAppenderFactory;
    }

    public SyntaxAppenderFactory getSortAppenderFactory() {
        return sortAppenderFactory;
    }

    public void setSortAppenderFactory(SyntaxAppenderFactory sortAppenderFactory) {
        this.sortAppenderFactory = sortAppenderFactory;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }


    public SyntaxAppenderFactory getSyntaxAppenderFactoryByStr(String text) {
        if (existCurrentArea(getResultAppenderFactory(), text)) {
            return getResultAppenderFactory();
        }
        if (existCurrentArea(getConditionAppenderFactory(), text)) {
            return getConditionAppenderFactory();
        }
        if (existCurrentArea(getSortAppenderFactory(), text)) {
            return getSortAppenderFactory();
        }
        return getResultAppenderFactory();
    }

    private boolean existCurrentArea(SyntaxAppenderFactory appenderFactory, String text) {
        if (appenderFactory == null) {
            return false;
        }
        if (text.equals(appenderFactory.getTipText())) {
            return true;
        }
        return false;
    }

    public void setReturnWrapper(TypeDescriptor typeDescriptor) {
        this.returnDescriptor = typeDescriptor;
    }

    public TypeDescriptor getReturnDescriptor() {
        return returnDescriptor;
    }

    public LinkedList<SyntaxAppender> findPriority(Comparator<SyntaxAppender> stringLengthComparator,
                                                   String splitStr) {
        if(StringUtils.isNoneBlank(splitStr)&&
            !(getTagName().startsWith(splitStr) || splitStr.startsWith(getTagName()))){
            return new LinkedList<>();
        }
        String replaceStr = splitStr;
        LinkedList<SyntaxAppender> syntaxAppenderList = new LinkedList<>();
        AreaSequence currentArea = AreaSequence.RESULT;
        // 找到一个合适的前缀
        while (replaceStr.length() > 0) {
            PriorityQueue<SyntaxAppender> priorityQueue = new PriorityQueue<>(stringLengthComparator);
            if (currentArea.getSequence() <= AreaSequence.RESULT.getSequence()) {
                resultAppenderFactory.findPriority(priorityQueue, syntaxAppenderList, replaceStr);
            }
            if (conditionAppenderFactory != null && currentArea.getSequence() <= AreaSequence.CONDITION.getSequence()) {
                conditionAppenderFactory.findPriority(priorityQueue, syntaxAppenderList, replaceStr);
            }
            if (sortAppenderFactory != null && currentArea.getSequence() <= AreaSequence.SORT.getSequence()) {
                sortAppenderFactory.findPriority(priorityQueue, syntaxAppenderList, replaceStr);
            }
            SyntaxAppender priorityAppender = priorityQueue.peek();
            if (priorityAppender == null) {
                break;
            }
            syntaxAppenderList.add(priorityAppender);
            if (priorityAppender.getAreaSequence() == AreaSequence.AREA) {
                currentArea = ((CustomAreaAppender)priorityAppender).getChildAreaSequence();
            }else{
                currentArea = priorityAppender.getAreaSequence();
            }
            replaceStr = replaceStr.substring(priorityAppender.getText().length());
        }
        return syntaxAppenderList;
    }
}
