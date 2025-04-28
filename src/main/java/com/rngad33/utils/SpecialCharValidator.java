package com.yupi.usercenter.utils;

import com.yupi.usercenter.enums.SpecialCharEnum;

/**
 * 特殊字符校验
 */
public class SpecialCharValidator {

    /**
     * 校验入口
     *
     * @return 是否（TF）包含
     */
    public static boolean doValidate(String input) {
        // 1. 检查是否包含特殊字符
        if (containsSpecialChars(input)) {
            System.out.println("标点符号！");
            return true;
        }

        // 2. 检查是否包含空白字符
        if (containsWhiteSpaceChars(input)) {
            System.out.println("空白字符！");
            return true;
        }

        // 3. 检查是否包含 HTML/XML 特殊字符
        if (containsHtmlSpecialChars(input)) {
            System.out.println("HTML/XML特殊字符！");
            return true;
        }

        // 4. 检查是否包含 SQL 注入相关字符
        if (containsSqlInjectionChars(input)) {
            System.out.println("SQL注入相关字符！");
            return true;
        }
        return false;
    }

    /**
     * 检查是否包含标点符号
     */
    private static boolean containsSpecialChars(String input) {
        return containsBlacklistedChars(input, SpecialCharEnum.SPECIAL_CHARS.getValue());
    }

    /**
     * 检查是否包含空白字符
     */
    private static boolean containsWhiteSpaceChars(String input) {
        return containsBlacklistedChars(input, SpecialCharEnum.WHITESPACE.getValue());
    }

    /**
     * 检查是否包含 HTML/XML 特殊字符
     */
    private static boolean containsHtmlSpecialChars(String input) {
        return containsBlacklistedChars(input, SpecialCharEnum.HTML_SPECIAL.getValue());
    }

    /**
     * 检查是否包含 SQL 注入相关字符
     */
    private static boolean containsSqlInjectionChars(String input) {
        return containsBlacklistedChars(input, SpecialCharEnum.SQL_INJECTION.getValue());
    }

    /**
     * 黑名单校验（採用）
     *
     * @return 是否（TF）包含
     */
    private static boolean containsBlacklistedChars(String input, String blacklist) {
        for (char c : input.toCharArray()) {
            if (blacklist.indexOf(c) != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 白名单校验
     */
    private static boolean validateWithWhitelist(String input, String whitelist) {
        for (char c : input.toCharArray()) {
            if (whitelist.indexOf(c) == -1) {
                return false;
            }
        }
        return true;
    }

}