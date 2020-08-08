package com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.changer;

import org.jetbrains.annotations.NotNull;

public class NotInParameterChanger extends InParameterChanger {
    @Override
    protected @NotNull String getIn() {
        return "not in";
    }
}
