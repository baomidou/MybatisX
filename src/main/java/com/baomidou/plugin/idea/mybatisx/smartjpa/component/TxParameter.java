package com.baomidou.plugin.idea.mybatisx.smartjpa.component;

import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;

public class TxParameter {
    private String typeText;
    private String canonicalTypeText;
    private String name;
    private boolean paramAnnotation;

    public static TxParameter createByPsiField(PsiField psiField) {
        TxParameter txParameter = new TxParameter();
        final PsiType type = psiField.getType();
        txParameter.typeText = type.getPresentableText();
        txParameter.canonicalTypeText = type.getCanonicalText();
        txParameter.name = psiField.getName();
        txParameter.paramAnnotation = true;
        return txParameter;
    }

    public static TxParameter copy(TxParameter parameter) {
        TxParameter txParameter = new TxParameter();
        txParameter.typeText = parameter.getTypeText();
        txParameter.canonicalTypeText = parameter.getCanonicalTypeText();
        txParameter.name = parameter.getName();
        return txParameter;
    }

    /**
     * @param name              变量名
     * @param typeText          定义类型简称
     * @param canonicalTypeText 定义类型的全称,用于导入
     * @return
     */
    public static TxParameter createByOrigin(String name, String typeText, String canonicalTypeText) {
        return createByOrigin(name, typeText, canonicalTypeText, true);
    }


    /**
     * @param name              变量名, 例如:  blogCollection
     * @param typeText          定义类型简称,例如:  java.util.Collection
     * @param canonicalTypeText 定义类型的全称,用于导入;  例如: java.util.Collection
     * @return
     */
    public static TxParameter createByOrigin(String name, String typeText, String canonicalTypeText, boolean paramAnnotation) {
        TxParameter txParameter = new TxParameter();
        txParameter.name = name;
        txParameter.typeText = typeText;
        txParameter.canonicalTypeText = canonicalTypeText;
        txParameter.paramAnnotation = paramAnnotation;
        return txParameter;
    }

    /**
     * 字段类型简称
     *
     * @return
     */
    public String getTypeText() {
        return typeText;
    }


    public String getCanonicalTypeText() {
        return canonicalTypeText;
    }

    /**
     * 字段名称
     *
     * @return
     */
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isParamAnnotation() {
        return paramAnnotation;
    }
}
