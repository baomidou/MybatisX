package com.baomidou.plugin.idea.mybatisx.locator;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.baomidou.plugin.idea.mybatisx.util.JavaUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author yanglin
 */
public class MapperLocator {

    public static LocateStrategy dfltLocateStrategy = new PackageLocateStrategy();

    public static MapperLocator getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, MapperLocator.class);
    }

    public boolean process(@Nullable PsiMethod method) {
        return null != method && process(method.getContainingClass());
    }

    public boolean process(@Nullable PsiClass clazz) {
        return null != clazz && JavaUtils.isElementWithinInterface(clazz) && dfltLocateStrategy.apply(clazz);
    }

}
