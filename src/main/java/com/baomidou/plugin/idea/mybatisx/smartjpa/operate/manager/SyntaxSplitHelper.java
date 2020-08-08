package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager;



import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppenderFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * 分割符号帮助
 */
class SyntaxSplitHelper {

    private final List<SyntaxAppenderFactory> syntaxAppenderFactoryList;
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

    public SyntaxSplitHelper(final List<SyntaxAppenderFactory> syntaxAppenderFactoryList) {
        this.syntaxAppenderFactoryList = syntaxAppenderFactoryList;
    }

    @NotNull
    public LinkedList<SyntaxAppender> splitAppenderByText(final String splitText) {
        String splitStr = splitText;

        final LinkedList<SyntaxAppender> syntaxAppenderList = new LinkedList<>();

        // 找到一个合适的前缀
        while (splitStr.length() > 0) {
            PriorityQueue<SyntaxAppender> priorityQueue = new PriorityQueue<>(stringLengthComparator);
            // 处理符号, 连接符, 后缀, 区域
            for (final SyntaxAppenderFactory syntaxAppenderFactory : syntaxAppenderFactoryList) {
                syntaxAppenderFactory.findPriority(priorityQueue, syntaxAppenderList, splitStr);
            }
            SyntaxAppender priorityAppender = priorityQueue.peek();
            if (priorityAppender == null) {
                break;
            }
            syntaxAppenderList.add(priorityAppender);
            splitStr = splitStr.substring(priorityAppender.getText().length());
        }
        return syntaxAppenderList;
    }


}
