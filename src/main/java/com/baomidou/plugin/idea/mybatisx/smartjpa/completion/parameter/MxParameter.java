package com.baomidou.plugin.idea.mybatisx.smartjpa.completion.parameter;

import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;

public class MxParameter {
    private String typeText;
    private String canonicalTypeText;
    private String name;

    public static MxParameter createByPsiField(PsiField psiField) {
        MxParameter mxParameter = new MxParameter();
        final PsiType type = psiField.getType();
        mxParameter.typeText = type.getPresentableText();
        mxParameter.canonicalTypeText = type.getCanonicalText();
        mxParameter.name = psiField.getName();
        return mxParameter;
    }

    public static MxParameter copy(MxParameter parameter) {
        MxParameter mxParameter = new MxParameter();
        mxParameter.typeText = parameter.getTypeText();
        mxParameter.canonicalTypeText = parameter.getCanonicalTypeText();
        mxParameter.name = parameter.getName();
        return mxParameter;
    }

    public static MxParameter createByOrigin(String name, String typeText, String canonicalTypeText) {
        MxParameter mxParameter = new MxParameter();
        mxParameter.name = name;
        mxParameter.typeText = typeText;
        mxParameter.canonicalTypeText = canonicalTypeText;
        return mxParameter;
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
