package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager.statement;



import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxParameter;

import java.util.List;

public interface Statement {
    List<TxParameter> getParameters();
}
