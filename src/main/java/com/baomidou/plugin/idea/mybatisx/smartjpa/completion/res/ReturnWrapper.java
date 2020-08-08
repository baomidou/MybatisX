package com.baomidou.plugin.idea.mybatisx.smartjpa.completion.res;

import com.intellij.psi.PsiClass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ReturnWrapper {

    private ReturnWrapper() {
    }

    public static ReturnWrapper createByPsiClass(PsiClass psiClass) {
        ReturnWrapper returnWrapper = new ReturnWrapper();
        returnWrapper.qualifiedName = psiClass.getQualifiedName();
        returnWrapper.simpleName = psiClass.getName();
        return returnWrapper;
    }

    public static ReturnWrapper createByOrigin(String qualifiedName, String simpleName) {
        ReturnWrapper returnWrapper = new ReturnWrapper();
        returnWrapper.qualifiedName = qualifiedName;
        returnWrapper.simpleName = simpleName;
        return returnWrapper;
    }


    private String qualifiedName;
    private String simpleName;

    public List<String> getImportList() {
        if (qualifiedName == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(qualifiedName);
    }

    public String getSimpleName() {
        return simpleName;
    }

}
