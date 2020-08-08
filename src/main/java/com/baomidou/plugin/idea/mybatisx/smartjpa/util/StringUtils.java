package com.baomidou.plugin.idea.mybatisx.smartjpa.util;

public class StringUtils {
    public static String upperCaseFirstChar(String _str) {
        if (_str == null) {
            return null;
        } else {
            return _str.isEmpty() ? _str : _str.substring(0, 1).toUpperCase() + _str.substring(1);
        }
    }


    public static String lowerCaseFirstChar(String _str) {
        if (_str == null) {
            return null;
        } else {
            return _str.isEmpty() ? _str : _str.substring(0, 1).toLowerCase() + _str.substring(1);
        }
    }
}
