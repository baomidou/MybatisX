package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager.statement;



import com.baomidou.plugin.idea.mybatisx.smartjpa.completion.parameter.MxParameter;

import java.util.Collections;
import java.util.List;

/**
 * 结果
 */
public class ResultStatement implements Statement {

    /**
     * 始终不需要根据结果生成任何参数
     * @return
     */
    @Override
    public List<MxParameter> getParameters() {
        return Collections.emptyList();
    }
}
