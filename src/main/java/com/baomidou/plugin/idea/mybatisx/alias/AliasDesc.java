package com.baomidou.plugin.idea.mybatisx.alias;

import com.intellij.psi.PsiClass;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * The type Alias desc.
 *
 * @author yanglin
 */
@Getter
public class AliasDesc {

    /**
     * -- GETTER --
     *  Gets clazz.
     *
     * @return the clazz
     */
    private PsiClass clazz;

    /**
     * -- GETTER --
     *  Gets alias.
     *
     * @return the alias
     */
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
     * Sets clazz.
     *
     * @param clazz the clazz
     */
    public void setClazz(PsiClass clazz) {
        this.clazz = clazz;
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

        if (!Objects.equals(alias, aliasDesc.alias)) {
            return false;
        }
        return Objects.equals(clazz, aliasDesc.clazz);
    }

    @Override
    public int hashCode() {
        int result = clazz != null ? clazz.hashCode() : 0;
        result = 31 * result + (alias != null ? alias.hashCode() : 0);
        return result;
    }
}
