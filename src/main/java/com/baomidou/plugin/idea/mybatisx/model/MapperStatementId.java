package com.baomidou.plugin.idea.mybatisx.model;

import com.baomidou.plugin.idea.mybatisx.dom.model.IdDomElement;
import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.util.MapperUtils;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Mapper 方法语句唯一标识值对象
 * 扁平统一格式：canonicalId 形式为 "canonicalNamespace.statementId"（如 "com.foo.MapperDao.InnerMapper.selectById"）
 */
public final class MapperStatementId implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private final String canonicalId;

    @NotNull
    private final String rawId;

    private MapperStatementId(@NotNull String canonicalId, @NotNull String rawId) {
        this.canonicalId = canonicalId;
        this.rawId = rawId;
    }

    @NotNull
    public static MapperStatementId of(@NotNull MapperNamespace namespace, @NotNull String statementId) {
        String canonical = namespace.getCanonicalName().isEmpty() ? statementId : namespace.getCanonicalName() + "." + statementId;
        String raw = namespace.getRawText().isEmpty() ? statementId : namespace.getRawText() + "." + statementId;
        return new MapperStatementId(canonical, raw);
    }

    @NotNull
    public static MapperStatementId ofMethod(@NotNull PsiMethod psiMethod) {
        PsiClass containingClass = psiMethod.getContainingClass();
        MapperNamespace namespace = MapperNamespace.fromPsiClass(containingClass);
        return of(namespace, psiMethod.getName());
    }

    @NotNull
    public static MapperStatementId ofDomElement(@NotNull IdDomElement domElement) {
        Mapper mapper = MapperUtils.getMapper(domElement);
        MapperNamespace namespace = MapperNamespace.fromMapper(mapper);
        String id = MapperUtils.getId(domElement);
        return of(namespace, id != null ? id : "");
    }

    @NotNull
    public static MapperStatementId ofSignature(@Nullable String rawSignature) {
        if (rawSignature == null) {
            return new MapperStatementId("", "");
        }
        String canonical = rawSignature.replace('$', '.');
        return new MapperStatementId(canonical, rawSignature);
    }

    @NotNull
    public String getCanonicalId() {
        return canonicalId;
    }

    @NotNull
    public String getRawId() {
        return rawId;
    }

    public boolean isEmpty() {
        return canonicalId.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (o instanceof MapperStatementId other) {
            return Objects.equals(this.canonicalId, other.canonicalId);
        }
        if (o instanceof String str) {
            return Objects.equals(this.canonicalId, str.replace('$', '.'));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(canonicalId);
    }

    @Override
    public String toString() {
        return canonicalId;
    }
}
