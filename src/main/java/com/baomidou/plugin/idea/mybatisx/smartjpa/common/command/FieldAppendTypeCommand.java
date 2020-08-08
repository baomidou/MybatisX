package com.baomidou.plugin.idea.mybatisx.smartjpa.common.command;



import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;

import java.util.Optional;

public class FieldAppendTypeCommand implements AppendTypeCommand {
    private final SyntaxAppender syntaxAppender;

    public FieldAppendTypeCommand(final SyntaxAppender syntaxAppender) {

        this.syntaxAppender = syntaxAppender;
    }

    @Override
    public Optional<SyntaxAppender> execute() {
        return Optional.of(this.syntaxAppender);
    }
}
