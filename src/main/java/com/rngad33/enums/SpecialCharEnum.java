package com.yupi.usercenter.enums;

import lombok.Getter;

/**
 * 特殊字符枚举
 */
@Getter
public enum SpecialCharEnum {

    SPECIAL_CHARS("标点符号", "!@#$%^&*()+-=[]{}|;':\",./<>?`~\\！￥……（）——【】；《》‘’、“”，。：·\\pP|\\pS"),
    WHITESPACE("空白字符", " \t\n\r\f"),
    CONTROL_CHARS("控制字符","\u0000-\u001F\u007F-\u009F"),
    HTML_SPECIAL("HTML/XML特殊字符", "<>&\"'"),
    SQL_INJECTION("SQL注入相关字符","'\"\\;--");

    private final String text;

    private final String value;

    SpecialCharEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }
}