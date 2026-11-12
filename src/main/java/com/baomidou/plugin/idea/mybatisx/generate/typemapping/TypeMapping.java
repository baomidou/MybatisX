package com.baomidou.plugin.idea.mybatisx.generate.typemapping;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * 一份类型映射 JSON。
 */
public class TypeMapping {
    public String name;
    public Map<String, Boolean> options;
    public List<Rewrite> rewrites;
    public Map<String, String> aliases;
    public List<Rule> rules;

    public String displayName(String fileName) {
        if (name != null && !name.isBlank()) {
            return name;
        }
        if (fileName == null) {
            return "";
        }
        int dot = fileName.lastIndexOf('.');
        if (dot > 0) {
            return fileName.substring(0, dot);
        }
        return fileName;
    }

    public static class Rewrite {
        public String from;
        public String type;
        public Boolean unsigned;
    }

    public static class Rule {
        public String type;
        public Boolean unsigned;
        public When when;
        public String enabledBy;
        @SerializedName("java")
        public JsonElement javaType;
        public String jdbcType;
    }

    public static class When {
        public Integer length;
        public Integer scale;
        public Integer scaleMin;
        public Integer precisionMax;
    }
}
