package com.baomidou.plugin.idea.mybatisx.generate.typemapping;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.Optional;

/**
 * 把映射 JSON 解析成 {@link TypeMapping}。格式不合法时返回空，不抛给调用方。
 */
public final class TypeMappingLoader {
    private static final Gson GSON = new Gson();

    private TypeMappingLoader() {
    }

    public static Optional<TypeMapping> parse(String json) {
        if (json == null || json.isBlank()) {
            return Optional.empty();
        }
        try {
            TypeMapping mapping = GSON.fromJson(json, TypeMapping.class);
            if (mapping == null || mapping.rules == null) {
                return Optional.empty();
            }
            return Optional.of(mapping);
        } catch (JsonSyntaxException ex) {
            return Optional.empty();
        }
    }
}
