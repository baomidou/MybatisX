package com.baomidou.plugin.idea.mybatisx.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;

/**
 * <p>
 * Mapper 检查
 * </p>
 *
 * @author yanglin
 * @since 2018-07-30
 */
public abstract class MapperInspection extends AbstractBaseJavaLocalInspectionTool {

    public static final ProblemDescriptor[] EMPTY_ARRAY = new ProblemDescriptor[0];

}
