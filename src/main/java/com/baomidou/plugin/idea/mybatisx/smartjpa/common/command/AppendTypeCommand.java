package com.baomidou.plugin.idea.mybatisx.smartjpa.common.command;



import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;

import java.util.Optional;

public interface AppendTypeCommand {
    Optional<SyntaxAppender> execute();
}
