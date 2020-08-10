package com.baomidou.plugin.idea.mybatisx.smartjpa.component;

import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;

public class TxParameter {
    private String typeText;
    private String canonicalTypeText;
    private String name;

    public static TxParameter createByPsiField(PsiField psiField) {
        TxParameter txParameter = new TxParameter();
        final PsiType type = psiField.getType();
        txParameter.typeText = type.getPresentableText();
        txParameter.canonicalTypeText = type.getCanonicalText();
        txParameter.name = psiField.getName();
        return txParameter;
    }

    public static TxParameter copy(TxParameter parameter) {
        TxParameter txParameter = new TxParameter();
        txParameter.typeText = parameter.getTypeText();
        txParameter.canonicalTypeText = parameter.getCanonicalTypeText();
        txParameter.name = parameter.getName();
        return txParameter;
    }

    public static TxParameter createByOrigin(String name, String typeText, String canonicalTypeText) {
        TxParameter txParameter = new TxParameter();
        txParameter.name = name;
        txParameter.typeText = typeText;
        txParameter.canonicalTypeText = canonicalTypeText;
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
}
