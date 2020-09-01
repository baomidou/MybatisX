package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.dialect;

import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager.StatementBlock;

public interface CustomStatement {
    StatementBlock getStatementBlock();

    String operatorName();
}
