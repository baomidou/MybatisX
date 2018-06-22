package com.baomidou.plugin.idea.mybatisx.locator;

import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiPackage;

import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 */
public class PackageLocateStrategy extends LocateStrategy {

    private PackageProvider provider = new MapperXmlPackageProvider();

    @Override
    public boolean apply(@NotNull PsiClass clazz) {
        String packageName = ((PsiJavaFile) clazz.getContainingFile()).getPackageName();
        PsiPackage pkg = JavaPsiFacade.getInstance(clazz.getProject()).findPackage(packageName);
        for (PsiPackage tmp : provider.getPackages(clazz.getProject())) {
            if (tmp.equals(pkg)) {
                return true;
            }
        }
        return false;
    }

}
