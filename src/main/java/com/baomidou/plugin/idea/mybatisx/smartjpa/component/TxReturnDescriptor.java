package com.baomidou.plugin.idea.mybatisx.smartjpa.component;

import com.intellij.psi.PsiClass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 返回值描述符
 */
public class TxReturnDescriptor implements TypeDescriptor {

    private TxReturnDescriptor() {
    }

    public static TxReturnDescriptor createByPsiClass(PsiClass psiClass) {
        TxReturnDescriptor txReturnDescriptor = new TxReturnDescriptor();
        txReturnDescriptor.qualifiedName = psiClass.getQualifiedName();
        txReturnDescriptor.simpleName = psiClass.getName();
        return txReturnDescriptor;
    }

    public static TxReturnDescriptor createByOrigin(String qualifiedName, String simpleName) {
        TxReturnDescriptor txReturnDescriptor = new TxReturnDescriptor();
        txReturnDescriptor.qualifiedName = qualifiedName;
        txReturnDescriptor.simpleName = simpleName;
        return txReturnDescriptor;
    }


    private String qualifiedName;
    private String simpleName;

    @Override
    public List<String> getImportList() {
        if (qualifiedName == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(qualifiedName);
    }

    @Override
    public String getContent() {
        return simpleName;
    }

}
