package com.baomidou.plugin.idea.mybatisx.smartjpa.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiReferenceList;
import com.intellij.psi.PsiType;

public class MapperSearch {


    public PsiClass searchEntity(Project project, PsiClass mapperClass) {
        PsiReferenceList extendsList = mapperClass.getExtendsList();
        PsiJavaCodeReferenceElement[] referenceElements = extendsList.getReferenceElements();
        if (referenceElements.length != 1) {
            return null;
        }
        PsiJavaCodeReferenceElement referenceElement = referenceElements[0];
        String qualifiedName = referenceElement.getQualifiedName();
        if ("com.baomidou.mybatisplus.core.mapper.BaseMapper".equals(qualifiedName)) {
            PsiType[] typeParameters = referenceElement.getTypeParameters();

            PsiType typeParameter = typeParameters[0];
            PsiClass entityClass = JavaPsiFacade.getInstance(project)
                    .findClass(typeParameter.getCanonicalText(), mapperClass.getResolveScope());

            return entityClass;
        }
        return null;
    }


}
