package com.baomidou.plugin.idea.mybatisx.alias;


import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * The type Alias facade.
 *
 * @author yanglin
 */
public class AliasFacade {

    private Project project;

    private JavaPsiFacade javaPsiFacade;

    private List<AliasResolver> resolvers;

    /**
     * Instantiates a new Alias facade.
     *
     * @param project the project
     */
    public AliasFacade(Project project) {
        this.project = project;
        this.resolvers = new ArrayList<>();
        this.javaPsiFacade = JavaPsiFacade.getInstance(project);
        initResolvers();
    }

    /**
     * Gets instance.
     *
     * @param project the project
     * @return the instance
     */
    public static final AliasFacade getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, AliasFacade.class);
    }

    private void initResolvers() {
        try {
            Class.forName("com.intellij.spring.model.utils.SpringModelUtils");
            this.registerResolver(AliasResolverFactory.createBeanResolver(project));
            // support springboot alias
            this.registerResolver(AliasResolverFactory.createSpringBootResolver(project));
        } catch (ClassNotFoundException e) {
        }
        this.registerResolver(AliasResolverFactory.createSingleAliasResolver(project));
        this.registerResolver(AliasResolverFactory.createConfigPackageResolver(project));
        this.registerResolver(AliasResolverFactory.createAnnotationResolver(project));
        this.registerResolver(AliasResolverFactory.createInnerAliasResolver(project));
    }

    /**
     * Find psi class optional.
     *
     * @param element   the element
     * @param shortName the short name
     * @return the optional
     */
    public Optional<PsiClass> findPsiClass(@Nullable PsiElement element, @NotNull String shortName) {
        // 查找mybatis内部支持的引用类型, 支持跳转到引用类
        String fullName = InternalAlias.referenceFullName(shortName);
        if (fullName == null) {
            fullName = shortName;
        }
        PsiClass clazz = javaPsiFacade.findClass(fullName, GlobalSearchScope.allScope(project));
        if (null != clazz) {
            return Optional.of(clazz);
        }
        for (AliasResolver resolver : resolvers) {
            for (AliasDesc desc : resolver.getClassAliasDescriptions(element)) {
                if (desc.getAlias().equalsIgnoreCase(fullName)) {
                    return Optional.of(desc.getClazz());
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Gets alias descs.
     *
     * @param element the element
     * @return the alias descs
     */
    @NotNull
    public Collection<AliasDesc> getAliasDescs(@Nullable PsiElement element) {
        ArrayList<AliasDesc> result = new ArrayList<>();
        for (AliasResolver resolver : resolvers) {
            result.addAll(resolver.getClassAliasDescriptions(element));
        }
        return result;
    }

    /**
     * Find alias desc optional.
     *
     * @param clazz the clazz
     * @return the optional
     */
    public Optional<AliasDesc> findAliasDesc(@Nullable PsiClass clazz) {
        if (null == clazz) {
            return Optional.empty();
        }
        for (AliasResolver resolver : resolvers) {
            for (AliasDesc desc : resolver.getClassAliasDescriptions(clazz)) {
                if (desc.getClazz().equals(clazz)) {
                    return Optional.of(desc);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Register resolver.
     *
     * @param resolver the resolver
     */
    public void registerResolver(@NotNull AliasResolver resolver) {
        this.resolvers.add(resolver);
    }



    private static class InternalAlias {

        private static volatile Map<String, String> refMap = null;

        private static Map<String, String> getRefMap() {
            if (refMap == null) {
                synchronized (InternalAlias.class) {
                    if (refMap == null) {
                        InternalAlias.refMap = buildRefMap();
                    }
                }
            }
            return refMap;
        }

        private static Map<String, String> buildRefMap() {
            Map<String, String> refMap = new HashMap<>();
            /* 数组不跳转 基本类型*/
            refMap.put("_byte[]", "java.lang.Byte");
            refMap.put("_long[]", "java.lang.Long");
            refMap.put("_short[]", "java.lang.Short");
            refMap.put("_int[]", "java.lang.Integer");
            refMap.put("_integer[]", "java.lang.Integer");
            refMap.put("_double[]", "java.lang.Double");
            refMap.put("_float[]", "java.lang.Float");
            refMap.put("_boolean[]", "java.lang.Boolean");
            /* 数组不跳转 引用类型类型*/
            refMap.put("byte[]", "java.lang.Byte");
            refMap.put("long[]", "java.lang.Long");
            refMap.put("short[]", "java.lang.Short");
            refMap.put("int[]", "java.lang.Integer");
            refMap.put("integer[]", "java.lang.Integer");
            refMap.put("double[]", "java.lang.Double");
            refMap.put("float[]", "java.lang.Float");
            refMap.put("boolean[]", "java.lang.Boolean");
            /* 数组不跳转 默认引用类型*/
            refMap.put("date[]", "java.util.Date");
            refMap.put("decimal[]", "java.math.BigDecimal");
            refMap.put("bigdecimal[]", "java.math.BigDecimal");
            refMap.put("biginteger[]", "java.math.BigInteger");
            refMap.put("object[]", "java.lang.Object");
            // 基本类型映射的引用类型
            refMap.put("_byte", "java.lang.Byte");
            refMap.put("_long", "java.lang.Long");
            refMap.put("_short", "java.lang.Short");
            refMap.put("_int", "java.lang.Integer");
            refMap.put("_integer", "java.lang.Integer");
            refMap.put("_double", "java.lang.Double");
            refMap.put("_float", "java.lang.Float");
            refMap.put("_boolean", "java.lang.Boolean");
            // 普通引用类型
            refMap.put("string", "java.lang.String");
            refMap.put("byte", "java.lang.Byte");
            refMap.put("long", "java.lang.Long");
            refMap.put("short", "java.lang.Short");
            refMap.put("int", "java.lang.Integer");
            refMap.put("integer", "java.lang.Integer");
            refMap.put("double", "java.lang.Double");
            refMap.put("float", "java.lang.Float");
            refMap.put("boolean", "java.lang.Boolean");
            refMap.put("date", "java.util.Date");
            refMap.put("decimal", "java.math.BigDecimal");
            refMap.put("bigdecimal", "java.math.BigDecimal");
            refMap.put("biginteger", "java.math.BigInteger");
            refMap.put("object", "java.lang.Object");
            refMap.put("map", "java.util.Map");
            refMap.put("hashmap", "java.util.HashMap");
            refMap.put("list", "java.util.List");
            refMap.put("arraylist", "java.util.ArrayList");
            refMap.put("collection", "java.util.Collection");
            refMap.put("iterator", "java.util.Iterator");
            refMap.put("resultset", "java.sql.ResultSet");
            return refMap;
        }

        public static String referenceFullName(String key) {
            return getRefMap().get(key.toLowerCase(Locale.ENGLISH));
        }
    }
}
