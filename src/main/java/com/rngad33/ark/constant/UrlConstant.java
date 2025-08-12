package com.rngad33.ark.constant;

/**
 * 抓取网址常量
 */
public interface UrlConstant {

    /**
     * 必应源（图片质量较低）
     */
    String sourceBing = "https://www.bing.com/images/async?q=%s&mmasync=1";

    /**
     * Safebooru源（推荐）
     */
    String sourceSafebooru = "https://safebooru.org/index.php?page=post&s=search&tags=%s+";

    /**
     * Konachan源（需开启代理）
     */
    String sourceKonachan = "https://konachan.com/post?tags=%s";

    /**
     * P站源（暂不可用）
     * 使用 Selenium 或 Playwright 模拟浏览器操作
     * 或者调用 Pixiv 官方 API（需要登录、Token 认证）
     */
    String sourcePixiv = "https://www.pixiv.net/ajax/search/artworks/%s?order=date_d&mode=medium&p=1&s_mode=s_tag&type=all&lang=zh";

}