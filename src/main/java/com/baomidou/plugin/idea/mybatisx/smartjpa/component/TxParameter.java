package com.baomidou.plugin.idea.mybatisx.smartjpa.component;

import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.AreaSequence;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;

/**
 * The type Tx parameter.
 */
public class TxParameter {
    private AreaSequence areaSequence;
    private String typeText;
    private String canonicalTypeText;
    private String name;
    private boolean paramAnnotation;

    /**
     * Create by psi field tx parameter.
     *
     * @param psiField the psi field
     * @param areaSequence
     * @return the tx parameter
     */
    public static TxParameter createByPsiField(PsiField psiField,
                                               AreaSequence areaSequence) {
        TxParameter txParameter = new TxParameter();
        final PsiType type = psiField.getType();
        txParameter.typeText = type.getPresentableText();
        txParameter.canonicalTypeText = type.getCanonicalText();
        txParameter.name = psiField.getName();
        txParameter.paramAnnotation = true;
        txParameter.areaSequence = areaSequence;
        return txParameter;
    }

    public AreaSequence getAreaSequence() {
        return areaSequence;
    }

    /**
     * Copy tx parameter.
     *
     * @param parameter the parameter
     * @return the tx parameter
     */
    public static TxParameter copy(TxParameter parameter) {
        TxParameter txParameter = new TxParameter();
        txParameter.typeText = parameter.getTypeText();
        txParameter.canonicalTypeText = parameter.getCanonicalTypeText();
        txParameter.name = parameter.getName();
        return txParameter;
    }

    /**
     * Create by origin tx parameter.
     *
     * @param name              变量名
     * @param typeText          定义类型简称
     * @param canonicalTypeText 定义类型的全称,用于导入
     * @return tx parameter
     */
    public static TxParameter createByOrigin(String name, String typeText, String canonicalTypeText) {
        return createByOrigin(name, typeText, canonicalTypeText, true);
    }


    /**
     * Create by origin tx parameter.
     *
     * @param name              变量名, 例如:  blogCollection
     * @param typeText          定义类型简称,例如:  java.util.Collection
     * @param canonicalTypeText 定义类型的全称,用于导入;  例如: java.util.Collection
     * @param paramAnnotation   the param annotation
     * @return tx parameter
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
     * @return type text
     */
    public String getTypeText() {
        return typeText;
    }


    /**
     * Gets canonical type text.
     *
     * @return the canonical type text
     */
    public String getCanonicalTypeText() {
        return canonicalTypeText;
    }

    /**
     * 字段名称
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Is param annotation boolean.
     *
     * @return the boolean
     */
    public boolean isParamAnnotation() {
        return paramAnnotation;
    }
}
