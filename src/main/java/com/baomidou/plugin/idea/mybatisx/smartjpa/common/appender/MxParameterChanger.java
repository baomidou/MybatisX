package com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender;




import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.operator.suffix.SuffixOperator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxParameter;

import java.util.List;

public interface MxParameterChanger extends SuffixOperator {
    List<TxParameter> getParameter(TxParameter txParameter);
}
