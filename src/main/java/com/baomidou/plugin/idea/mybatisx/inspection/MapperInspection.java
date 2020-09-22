package com.baomidou.plugin.idea.mybatisx.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;

/**
 * <p>
 * Mapper 检查
 * </p>
 *
 * @author yanglin
 * @since 2018 -07-30
 */
public abstract class MapperInspection extends AbstractBaseJavaLocalInspectionTool {

    /**
     * The constant EMPTY_ARRAY.
     */
    public static final ProblemDescriptor[] EMPTY_ARRAY = new ProblemDescriptor[0];

}
