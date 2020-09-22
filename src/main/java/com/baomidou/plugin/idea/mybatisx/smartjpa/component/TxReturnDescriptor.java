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

    /**
     * Create collection by psi class tx return descriptor.
     *
     * @param psiClass the psi class
     * @return the tx return descriptor
     */
    public static TxReturnDescriptor createCollectionByPsiClass(PsiClass psiClass) {
        TxReturnDescriptor txReturnDescriptor = new TxReturnDescriptor();
        txReturnDescriptor.qualifiedName = Arrays.asList("java.util.List", psiClass.getQualifiedName());
        txReturnDescriptor.simpleName = "List<" + psiClass.getName() + ">";
        return txReturnDescriptor;
    }

    /**
     * Create by psi class tx return descriptor.
     *
     * @param psiClass the psi class
     * @return the tx return descriptor
     */
    public static TxReturnDescriptor createByPsiClass(PsiClass psiClass) {
        TxReturnDescriptor txReturnDescriptor = new TxReturnDescriptor();
        txReturnDescriptor.qualifiedName = Collections.singletonList(psiClass.getQualifiedName());
        txReturnDescriptor.simpleName = psiClass.getName();
        return txReturnDescriptor;
    }

    /**
     * Create by origin tx return descriptor.
     *
     * @param qualifiedName the qualified name
     * @param simpleName    the simple name
     * @return the tx return descriptor
     */
    public static TxReturnDescriptor createByOrigin(String qualifiedName, String simpleName) {
        TxReturnDescriptor txReturnDescriptor = new TxReturnDescriptor();
        txReturnDescriptor.qualifiedName = Collections.singletonList(qualifiedName);
        txReturnDescriptor.simpleName = simpleName;
        return txReturnDescriptor;
    }

    /**
     * 返回类型的导入全称
     * 例如方法:
     * public Collection<User> selectByTitle(String title);
     * simpleName 指的是:  java.util.Collection
     */
    private List<String> qualifiedName;
    /**
     * 返回类型的简单名称;
     * 例如方法:
     * public Collection<User> selectByTitle(String title);
     * simpleName 指的是:   Collection<User>
     */
    private String simpleName;

    @Override
    public List<String> getImportList() {
        if (qualifiedName == null) {
            return Collections.emptyList();
        }
        return qualifiedName;
    }

    @Override
    public String getContent() {
        return simpleName;
    }

}
