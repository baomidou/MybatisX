package com.baomidou.plugin.idea.mybatisx.smartjpa.common.command;



import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;

import java.util.Optional;

/**
 * The interface Append type command.
 */
public interface AppendTypeCommand {
    /**
     * Execute optional.
     *
     * @return the optional
     */
    Optional<SyntaxAppender> execute();
}
