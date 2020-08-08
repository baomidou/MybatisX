package com.baomidou.plugin.idea.mybatisx.smartjpa.operate;



import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;

import java.util.LinkedList;
import java.util.Set;

public interface TypeManager {

    Set<String> execute(LinkedList<SyntaxAppender> splitList);
}
