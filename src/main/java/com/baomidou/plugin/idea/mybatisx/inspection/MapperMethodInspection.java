package com.baomidou.plugin.idea.mybatisx.inspection;

import com.baomidou.plugin.idea.mybatisx.annotation.Annotation;
import com.baomidou.plugin.idea.mybatisx.dom.model.Select;
import com.baomidou.plugin.idea.mybatisx.locator.MapperLocator;
import com.baomidou.plugin.idea.mybatisx.service.JavaService;
import com.baomidou.plugin.idea.mybatisx.setting.config.AbstractStatementGenerator;
import com.baomidou.plugin.idea.mybatisx.util.JavaUtils;
import com.google.common.collect.Lists;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * The type Mapper method inspection.
 *
 * @author yanglin
 */
public class MapperMethodInspection extends MapperInspection {

    private static final Set<String> STATEMENT_PROVIDER_NAMES = new HashSet<String>() {
        {
            add("org.apache.ibatis.annotations.SelectProvider");
            add("org.apache.ibatis.annotations.UpdateProvider");
            add("org.apache.ibatis.annotations.InsertProvider");
            add("org.apache.ibatis.annotations.DeleteProvider");
        }
    };
    private static final Set<String> MYBATIS_PLUS_BASE_MAPPER_NAMES = new HashSet<String>() {
        {
            // mp3
            add("com.baomidou.mybatisplus.core.mapper.BaseMapper");
            // mp2
            add("com.baomidou.mybatisplus.mapper.BaseMapper");
        }
    };

    @Nullable
    @Override
    public ProblemDescriptor[] checkMethod(@NotNull PsiMethod method, @NotNull InspectionManager manager, boolean isOnTheFly) {
        if (!MapperLocator.getInstance(method.getProject()).process(method)
            || JavaUtils.isAnyAnnotationPresent(method, Annotation.STATEMENT_SYMMETRIES)) {
            return EMPTY_ARRAY;
        }
        List<ProblemDescriptor> res = createProblemDescriptors(method, manager, isOnTheFly);
        return res.toArray(new ProblemDescriptor[0]);
    }

    private List<ProblemDescriptor> createProblemDescriptors(PsiMethod method, InspectionManager manager, boolean isOnTheFly) {
        ArrayList<ProblemDescriptor> res = Lists.newArrayList();
        Optional<ProblemDescriptor> p1 = checkStatementExists(method, manager, isOnTheFly);
        p1.ifPresent(res::add);
        Optional<ProblemDescriptor> p2 = checkResultType(method, manager, isOnTheFly);
        p2.ifPresent(res::add);
        return res;
    }

    private Optional<ProblemDescriptor> checkResultType(PsiMethod method, InspectionManager manager, boolean isOnTheFly) {
        Optional<DomElement> ele = JavaService.getInstance(method.getProject()).findStatement(method);
        if (ele.isPresent()) {
            DomElement domElement = ele.get();
            if (domElement instanceof Select) {
                Select select = (Select) domElement;
                Optional<PsiClass> target = AbstractStatementGenerator.getSelectResultType(method);
                PsiClass clazz = select.getResultType().getValue();
                PsiIdentifier ide = method.getNameIdentifier();
                if (null != ide && null == select.getResultMap().getValue()) {
                    if (target.isPresent()) {
                        final PsiClass targetClass = target.get();
                        if (!equalsOrInheritor(clazz, targetClass)) {
                            String srcType = clazz != null ? clazz.getQualifiedName() :"";
                            String targetType = targetClass.getQualifiedName();
                            String descriptionTemplate = "Result type not match for select id=\"#ref\""
                                + "\n srcType: " + srcType
                                + "\n targetType: " + targetType;
                            ProblemDescriptor problemDescriptor = manager.createProblemDescriptor(ide,
                                descriptionTemplate,
                                (LocalQuickFix) null,
                                ProblemHighlightType.GENERIC_ERROR,
                                isOnTheFly);
                            return Optional.of(problemDescriptor);
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }

    private boolean equalsOrInheritor(PsiClass child, PsiClass parent) {
        if (child == null) {
            return false;
        }
        return child.equals(parent) || child.isInheritor(parent, true);
    }

    private Optional<ProblemDescriptor> checkStatementExists(PsiMethod method, InspectionManager manager, boolean isOnTheFly) {
        PsiIdentifier ide = method.getNameIdentifier();
        // SelectProvider爆红 issue: https://gitee.com/baomidou/MybatisX/issues/I17JQ4
        PsiAnnotation[] annotation = method.getAnnotations();
        if (annotation.length > 0) {
            // 如果存在提供者注解, 就返回验证成功
            for (PsiAnnotation psiAnnotation : annotation) {
                if (STATEMENT_PROVIDER_NAMES.contains(psiAnnotation.getQualifiedName())) {
                    return Optional.empty();
                }
            }
        }
        JavaService instance = JavaService.getInstance(method.getProject());
        if (!instance.findStatement(method).isPresent() && null != ide) {
            if (isMybatisPlusMethod(method)) {
                return Optional.empty();
            }
            return Optional.of(manager.createProblemDescriptor(ide, "Statement with id=\"#ref\" not defined in mapper xml",
                new StatementNotExistsQuickFix(method), ProblemHighlightType.GENERIC_ERROR, isOnTheFly));
        }
        return Optional.empty();
    }

    private boolean isMybatisPlusMethod(PsiMethod method) {
        PsiClass parentOfType = PsiTreeUtil.getParentOfType(method, PsiClass.class);
        if (parentOfType == null) {
            return false;
        }
        PsiMethod[] methodsBySignature = parentOfType.findMethodsBySignature(method, true);
        if (methodsBySignature.length > 1) {
            for (int index = methodsBySignature.length; index > 0; index--) {
                PsiClass mapperClass = PsiTreeUtil.getParentOfType(methodsBySignature[index - 1], PsiClass.class);
                if (mapperClass == null) {
                    continue;
                }
                if (MYBATIS_PLUS_BASE_MAPPER_NAMES.contains(mapperClass.getQualifiedName())) {
                    return true;
                }
            }
            return true;
        }
        return false;
    }

}
