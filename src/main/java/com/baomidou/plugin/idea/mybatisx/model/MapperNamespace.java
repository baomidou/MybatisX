package com.baomidou.plugin.idea.mybatisx.model;

import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.ClassUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * Mapper 命名空间值对象
 * 负责归一化处理 Java 源码全限定名（以 '.' 分隔）与 MyBatis XML / JVM 二进制名（内部类以 '$' 分隔）
 */
public final class MapperNamespace implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private final String rawText;

    @NotNull
    private final String canonicalName;

    @Nullable
    private final String jvmName;

    private MapperNamespace(@Nullable String rawText, @Nullable PsiClass psiClass) {
        this.rawText = rawText != null ? rawText.trim() : "";
        if (psiClass != null) {
            String qName = psiClass.getQualifiedName();
            this.canonicalName = qName != null ? qName : this.rawText.replace('$', '.');
            this.jvmName = ClassUtil.getJVMClassName(psiClass);
        } else {
            this.canonicalName = this.rawText.replace('$', '.');
            this.jvmName = this.rawText.contains("$") ? this.rawText : null;
        }
    }

    @NotNull
    public static MapperNamespace of(@Nullable String rawText) {
        return new MapperNamespace(rawText, null);
    }

    @NotNull
    public static MapperNamespace fromPsiClass(@Nullable PsiClass psiClass) {
        if (psiClass == null) {
            return of("");
        }
        String raw = ClassUtil.getJVMClassName(psiClass);
        if (raw == null) {
            raw = psiClass.getQualifiedName();
        }
        return new MapperNamespace(raw, psiClass);
    }

    @NotNull
    public static MapperNamespace fromMapper(@Nullable Mapper mapper) {
        if (mapper == null || mapper.getNamespace() == null) {
            return of("");
        }
        String stringValue = mapper.getNamespace().getStringValue();
        PsiClass psiClass = mapper.getNamespace().getValue();
        return new MapperNamespace(stringValue, psiClass);
    }

    @NotNull
    public String getRawText() {
        return rawText;
    }

    @NotNull
    public String getCanonicalName() {
        return canonicalName;
    }

    @NotNull
    public String getJvmName() {
        if (jvmName != null) {
            return jvmName;
        }
        return canonicalName;
    }

    public boolean isEmpty() {
        return canonicalName.isEmpty();
    }

    public Optional<PsiClass> findPsiClass(@NotNull Project project) {
        if (isEmpty()) {
            return Optional.empty();
        }
        JavaPsiFacade facade = JavaPsiFacade.getInstance(project);
        return Optional.ofNullable(facade.findClass(canonicalName, GlobalSearchScope.allScope(project)));
    }

    public Optional<PsiClass[]> findClasses(@NotNull Project project) {
        if (isEmpty()) {
            return Optional.empty();
        }
        JavaPsiFacade facade = JavaPsiFacade.getInstance(project);
        return Optional.of(facade.findClasses(canonicalName, GlobalSearchScope.allScope(project)));
    }

    public boolean matches(@Nullable PsiClass clazz) {
        if (clazz == null || isEmpty()) {
            return false;
        }
        return this.equals(fromPsiClass(clazz));
    }

    public boolean matches(@Nullable String raw) {
        if (raw == null) {
            return false;
        }
        return this.equals(of(raw));
    }

    public boolean matches(@Nullable MapperNamespace other) {
        if (other == null) {
            return false;
        }
        return this.equals(other);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (o instanceof MapperNamespace other) {
            return Objects.equals(this.canonicalName, other.canonicalName);
        }
        if (o instanceof String str) {
            return Objects.equals(this.canonicalName, str.replace('$', '.'));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(canonicalName);
    }

    @Override
    public String toString() {
        return canonicalName;
    }
}
