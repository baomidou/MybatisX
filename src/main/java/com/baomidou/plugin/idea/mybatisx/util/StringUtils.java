package com.baomidou.plugin.idea.mybatisx.util;

/**
 * The type String utils.
 */
public class StringUtils {
    /**
     * Upper case first char string.
     *
     * @param _str the str
     * @return the string
     */
    public static String upperCaseFirstChar(String _str) {
        if (_str == null) {
            return null;
        } else {
            return _str.isEmpty() ? _str : _str.substring(0, 1).toUpperCase() + _str.substring(1);
        }
    }


    /**
     * Lower case first char string.
     *
     * @param _str the str
     * @return the string
     */
    public static String lowerCaseFirstChar(String _str) {
        if (_str == null) {
            return null;
        } else {
            return _str.isEmpty() ? _str : _str.substring(0, 1).toLowerCase() + _str.substring(1);
        }
    }


    /**
     * convert string from slash style to camel style, such as my_course will convert to MyCourse
     *
     * @param str the str
     * @return string
     */
    public static String dbStringToCamelStyle(String str) {
        if (str != null) {
            str = str.toLowerCase();
            StringBuilder sb = new StringBuilder();
            sb.append(String.valueOf(str.charAt(0)).toUpperCase());
            for (int i = 1; i < str.length(); i++) {
                char c = str.charAt(i);
                if (c != '_') {
                    sb.append(c);
                } else {
                    if (i + 1 < str.length()) {
                        sb.append(String.valueOf(str.charAt(i + 1)).toUpperCase());
                        i++;
                    }
                }
            }
            return sb.toString();
        }
        return null;
    }

    /**
     * Is empty boolean.
     *
     * @param str the str
     * @return the boolean
     */
    public static boolean isEmpty(Object str) {
        return str == null || "".equals(str);
    }
}
