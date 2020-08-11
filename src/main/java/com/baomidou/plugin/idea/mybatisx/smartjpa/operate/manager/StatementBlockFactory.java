package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager;


import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppenderFactory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 符号追加器 工厂管理器
 */
public class StatementBlockFactory {

    private final List<StatementBlock> blockList = new ArrayList<>();


    public StatementBlockFactory() {
    }


    @NotNull
    public LinkedList<SyntaxAppender> splitAppenderByText(String splitParam) {

        List<SyntaxAppenderFactory> syntaxAppenderFactoryList = this.blockList.stream()
            .flatMap(x -> {
                return Arrays.stream(new SyntaxAppenderFactory[]{x.getResultAppenderFactory(),
                    x.getConditionAppenderFactory(),
                    x.getSortAppenderFactory()});
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        SyntaxSplitHelper syntaxSplitHelper = new SyntaxSplitHelper(syntaxAppenderFactoryList);
        return syntaxSplitHelper.splitAppenderByText(splitParam);

    }

    public void registerStatementBlock(final StatementBlock statementBlock) {
        this.blockList.add(statementBlock);

        appenderFactoryMap.put(statementBlock.getTagName(), statementBlock);

    }

    private Map<String, StatementBlock> appenderFactoryMap = new ConcurrentHashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(StatementBlockFactory.class);

    public List<SyntaxAppenderFactory> findAreaListByJpa(LinkedList<SyntaxAppender> jpaList) {
        List<SyntaxAppenderFactory> appenderFactories = new ArrayList<>();
        SyntaxAppender peek = jpaList.peek();

        StatementBlock statementBlock = appenderFactoryMap.get(peek.getText());
        appenderFactories.add(statementBlock.getResultAppenderFactory());
        if (statementBlock.getConditionAppenderFactory() != null) {
            appenderFactories.add(statementBlock.getConditionAppenderFactory());
        }
        if (statementBlock.getSortAppenderFactory() != null) {
            appenderFactories.add(statementBlock.getSortAppenderFactory());
        }
        return appenderFactories;
    }

    public Collection<StatementBlock> getAllBlock() {
        return blockList;
    }

    public StatementBlock findBlockByText(String text) {
        return appenderFactoryMap.get(text);
    }
}
