package com.baomidou.plugin.idea.mybatisx.smartjpa.common.command;






import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.AreaSequence;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomAreaAppender;

import java.util.Optional;

/**
 * 区域前缀
 * 查询: select,get,query
 * 增加: insert
 * 删除: delete
 * 修改: update,modify
 * 条件: By
 * 排序: OrderBy
 */
public class AreaPrefixAppendTypeCommand implements AppendTypeCommand {

    private final String areaPrefix;
    private final String areaType;
    private final AreaSequence areaSequence;
    private final AreaSequence childAreaSequence;
    private final SyntaxAppenderFactory syntaxAppenderFactory;

    /**
     * Instantiates a new Area prefix append type command.
     *
     * @param areaPrefix            the area prefix
     * @param areaType              the area type
     * @param areaSequence          the area sequence
     * @param childAreaSequence     the child area sequence
     * @param syntaxAppenderFactory the syntax appender factory
     */
    public AreaPrefixAppendTypeCommand(String areaPrefix, String areaType, AreaSequence areaSequence, AreaSequence childAreaSequence, SyntaxAppenderFactory syntaxAppenderFactory) {
        this.areaPrefix = areaPrefix;
        this.areaType = areaType;
        this.areaSequence = areaSequence;
        this.childAreaSequence = childAreaSequence;
        this.syntaxAppenderFactory = syntaxAppenderFactory;
    }

    @Override
    public Optional<SyntaxAppender> execute() {
        return Optional.of(CustomAreaAppender.createCustomAreaAppender(this.areaPrefix, areaType, areaSequence, childAreaSequence, syntaxAppenderFactory));
    }
}
