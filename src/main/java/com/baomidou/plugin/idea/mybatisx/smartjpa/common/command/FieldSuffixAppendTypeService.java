package com.baomidou.plugin.idea.mybatisx.smartjpa.common.command;


import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.model.AppendTypeEnum;

import java.util.Optional;

/**
 * The type Field suffix append type service.
 */
public class FieldSuffixAppendTypeService implements AppendTypeCommand {

    private final SyntaxAppender syntaxAppender;

    /**
     * Instantiates a new Field suffix append type service.
     *
     * @param syntaxAppender the syntax appender
     */
    public FieldSuffixAppendTypeService(final SyntaxAppender syntaxAppender) {

        this.syntaxAppender = syntaxAppender;
    }

    @Override
    public Optional<SyntaxAppender> execute() {
        if (this.syntaxAppender.getType() == AppendTypeEnum.SUFFIX) {
            return Optional.of(this.syntaxAppender);
        }
        return Optional.empty();
    }
}
