package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager.statement;



import com.baomidou.plugin.idea.mybatisx.smartjpa.completion.parameter.MxParameter;

import java.util.List;

public interface Statement {
    List<MxParameter> getParameters();
}
