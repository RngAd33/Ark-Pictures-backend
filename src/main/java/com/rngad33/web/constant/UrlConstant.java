package com.rngad33.web.constant;

/**
 * 抓取网址常量
 */
public interface UrlConstant {

    /**
     * 必应源
     */
    String sourceBing = "https://www.bing.com/images/async?q=%s&mmasync=1";

    /**
     * P站源（需开启代理）
     */
    String sourcePixiv = "https://www.pixiv.net/ajax/search/artworks/%s?order=date_d&mode=medium&p=1&s_mode=s_tag&type=all&lang=zh";

}