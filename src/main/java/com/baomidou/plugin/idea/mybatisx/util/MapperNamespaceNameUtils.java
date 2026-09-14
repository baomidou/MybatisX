package com.baomidou.plugin.idea.mybatisx.util;

import com.intellij.psi.PsiClass;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Mapper XML namespace 文件名解析、分词及匹配工具类
 */
public final class MapperNamespaceNameUtils {

    private MapperNamespaceNameUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * 去除 .xml、.mapper.xml 等扩展名，获取文件名主干
     *
     * @param fileName 如 UserMapper.xml 或 User.mapper.xml
     * @return 文件名主干，如 UserMapper 或 User
     */
    @NotNull
    public static String extractBaseName(@Nullable String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return "";
        }
        String name = fileName.trim();
        if (name.toLowerCase(Locale.ROOT).endsWith(".xml")) {
            name = name.substring(0, name.length() - 4);
        }
        if (name.toLowerCase(Locale.ROOT).endsWith(".mapper")) {
            name = name.substring(0, name.length() - 7);
        }
        return name;
    }

    /**
     * 将文件名或类名转换为驼峰形式（处理下划线和中划线）
     */
    @NotNull
    public static String toCamelCase(@NotNull String text) {
        String[] parts = text.split("[_-]+");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            sb.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                sb.append(part.substring(1));
            }
        }
        return sb.toString();
    }

    /**
     * 对文件名主干进行拆词（按驼峰、下划线、中划线等拆分），不使用停用词过滤
     *
     * @param baseName 文件名主干
     * @return 分词列表
     */
    @NotNull
    public static List<String> extractTokens(@Nullable String baseName) {
        if (StringUtils.isBlank(baseName)) {
            return Collections.emptyList();
        }
        // 按照驼峰大写字母、下划线、中划线及非字母数字字符切分
        String[] rawTokens = baseName.split("(?<!^)(?=[A-Z])|[^a-zA-Z0-9]+");
        List<String> tokens = new ArrayList<>();
        for (String rawToken : rawTokens) {
            String token = rawToken.trim();
            if (StringUtils.isNotEmpty(token)) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    /**
     * 兼容方法：直接返回分词列表
     */
    @NotNull
    public static List<String> extractBusinessTokens(@Nullable String baseName) {
        return extractTokens(baseName);
    }

    /**
     * 根据文件名获取首要候选短类名（仅包含原文件名及驼峰变换，不做去后缀变体猜测）
     *
     * @param fileName 如 UserMapper.xml
     * @return 候选短类名集合
     */
    @NotNull
    public static Set<String> getCandidateClassNames(@Nullable String fileName) {
        Set<String> candidates = new LinkedHashSet<>();
        String baseName = extractBaseName(fileName);
        if (StringUtils.isBlank(baseName)) {
            return candidates;
        }

        candidates.add(baseName);

        String camel = toCamelCase(baseName);
        if (StringUtils.isNotEmpty(camel)) {
            candidates.add(camel);
        }

        return candidates;
    }

    /**
     * 判断是否是符合条件的接口候选
     */
    public static boolean isCandidateInterface(@Nullable PsiClass psiClass) {
        return psiClass != null
            && psiClass.isValid()
            && psiClass.isInterface()
            && !psiClass.isAnnotationType()
            && psiClass.getQualifiedName() != null;
    }

    /**
     * 计算某类与当前 Mapper XML 文件的分词匹配优先级打分
     *
     * @param psiClass        候选类
     * @param fileName        当前 XML 文件名
     * @param inCurrentModule 是否属于当前模块
     * @return 优先级分数，如果非接口则返回 -1.0
     */
    public static double calculatePriority(@NotNull PsiClass psiClass,
                                          @Nullable String fileName,
                                          boolean inCurrentModule) {
        if (!isCandidateInterface(psiClass)) {
            return -1.0;
        }
        String className = psiClass.getName();
        if (className == null) {
            return -1.0;
        }

        double moduleBonus = inCurrentModule ? 200.0 : 0.0;
        String baseName = extractBaseName(fileName);
        if (StringUtils.isBlank(baseName)) {
            return moduleBonus;
        }

        // 1. 完全同名匹配（如 UserMapper.xml 对应 UserMapper）
        if (className.equalsIgnoreCase(baseName)) {
            return 1000.0 + moduleBonus;
        }

        // 2. 转换驼峰后完全一致（如 user_mapper.xml 对应 UserMapper）
        String camelBaseName = toCamelCase(baseName);
        if (className.equalsIgnoreCase(camelBaseName)) {
            return 980.0 + moduleBonus;
        }

        // 3. 基于分词的匹配：计算候选类名中命中的文件分词数量
        List<String> tokens = extractTokens(baseName);
        if (!tokens.isEmpty()) {
            int matchedTokenCount = 0;
            String lowerClassName = className.toLowerCase(Locale.ROOT);
            for (String token : tokens) {
                if (lowerClassName.contains(token.toLowerCase(Locale.ROOT))) {
                    matchedTokenCount++;
                }
            }
            if (matchedTokenCount == tokens.size()) {
                // 全部文件名分词均被包含（如 UserMapper 的所有分词在 UserMapperExt 中都出现）
                return 600.0 + moduleBonus;
            } else if (matchedTokenCount > 0) {
                // 部分分词被包含，按命中分词数量加权
                return 300.0 + (matchedTokenCount * 50.0) + moduleBonus;
            }
        }

        return moduleBonus;
    }
}
