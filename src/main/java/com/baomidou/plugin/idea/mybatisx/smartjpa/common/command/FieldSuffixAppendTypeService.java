package com.baomidou.plugin.idea.mybatisx.smartjpa.common.command;




import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.model.AppendTypeEnum;

import java.util.Optional;

public class FieldSuffixAppendTypeService implements AppendTypeCommand {

    private final SyntaxAppender syntaxAppender;

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
