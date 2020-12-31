package com.baomidou.plugin.idea.mybatisx.alias;

import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;

/**
 * The type Alias desc.
 *
 * @author yanglin
 */
public class AliasDesc {

    private PsiClass clazz;

    private String alias;

    /**
     * Instantiates a new Alias desc.
     */
    public AliasDesc() {
    }

    /**
     * Instantiates a new Alias desc.
     *
     * @param clazz the clazz
     * @param alias the alias
     */
    public AliasDesc(PsiClass clazz, String alias) {
        this.clazz = clazz;
        this.alias = alias;
    }

    /**
     * Create alias desc.
     *
     * @param psiClass the psi class
     * @param alias    the alias
     * @return the alias desc
     */
    public static AliasDesc create(@NotNull PsiClass psiClass, @NotNull String alias) {
        return new AliasDesc(psiClass, alias);
    }

    /**
     * Gets clazz.
     *
     * @return the clazz
     */
    public PsiClass getClazz() {
        return clazz;
    }

    /**
     * Sets clazz.
     *
     * @param clazz the clazz
     */
    public void setClazz(PsiClass clazz) {
        this.clazz = clazz;
    }

    /**
     * Gets alias.
     *
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Sets alias.
     *
     * @param alias the alias
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AliasDesc aliasDesc = (AliasDesc) o;

        if (alias != null ? !alias.equals(aliasDesc.alias) : aliasDesc.alias != null) {
            return false;
        }
        if (clazz != null ? !clazz.equals(aliasDesc.clazz) : aliasDesc.clazz != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = clazz != null ? clazz.hashCode() : 0;
        result = 31 * result + (alias != null ? alias.hashCode() : 0);
        return result;
    }
}
