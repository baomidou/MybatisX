package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager;


import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * 分割符号帮助
 */
class SyntaxSplitHelper {

    private final List<StatementBlock> statementBlockList;
    // 文本长度优先排序, 其次按照区域顺序排序
    Comparator<SyntaxAppender> stringLengthComparator = (o1, o2) ->
    {
        // 长度相等, 按照区域顺序排序
        int compare = o2.getText().length() - o1.getText().length();
        if (compare == 0) {
            return o1.getAreaSequence().getSequence() - o2.getAreaSequence().getSequence();
        }
        return compare;
    };

    Comparator<LinkedList<SyntaxAppender>> syntaxAppenderComparator = (o1, o2) ->
    {
        // 长度相等, 按照区域顺序排序
        return o2.size() - o1.size();
    };

    public SyntaxSplitHelper(final List<StatementBlock> statementBlockList) {
        this.statementBlockList = statementBlockList;
    }

    /**
     *
     * @param splitText
     * @return
     */
    @NotNull
    public LinkedList<SyntaxAppender> splitAppenderByText(final String splitText) {
        PriorityQueue<LinkedList<SyntaxAppender>> collect = new PriorityQueue<>(syntaxAppenderComparator);
        for (StatementBlock statementBlock : statementBlockList) {
            LinkedList<SyntaxAppender> priority = statementBlock.findPriority(stringLengthComparator, splitText);
            collect.add(priority);
        }
        return collect.peek();
    }

}
