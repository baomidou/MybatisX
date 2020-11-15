package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.appender;

import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.AreaSequence;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomAreaAppender;

/**
 * @author ls9527
 */
public class SelectCustomAreaAppender extends CustomAreaAppender {


    public SelectCustomAreaAppender(final String area, final String areaType, final SyntaxAppenderFactory syntaxAppenderFactory) {
        super(area, areaType, AreaSequence.AREA, AreaSequence.RESULT, syntaxAppenderFactory);
    }

}
