package com.rngad33.web.manager.jsoup;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

/**
 * Konachan源模板
 */
@Service
@Slf4j
public class JsoupTemplateFromKonachan extends JsoupTemplate {

    /**
     * 获取图片元素
     *
     * @return
     */
    protected Elements getImgElement(Document document) {
        /*
        Element div = document.getElementsByClass("dgControl").first();
        log.error("——！抓取外层元素失败！——");
        ThrowUtils.throwIf(ObjUtil.isEmpty(div), ErrorCodeEnum.NOT_PARAMS);
        // todo 筛选图片元素（选择所有类名为 ??? 的 <img> 标签并存储在 imgElementList 中）
        Elements imgElementList = div.select(".post-preview a img");
        log.info(">>>元素抓取完毕，开始上传图片");
         */
        return null;
    }

    /**
     * 获取图片地址
     *
     * @return
     */
    protected String getFileUrl(Element imgElement) {
        /*
        String thumbUrl = imgElement.absUrl("src");   // 缩略图地址
        String detailPageUrl = imgElement.parent().absUrl("src");   // 详情页地址
        Document detailDoc = Jsoup.connect(detailPageUrl).userAgent("Mozilla/5.0").get();
        Element fullImg = detailDoc.select("img#image").first();
        if (fullImg == null) {
            continue;
        }
        String fileUrl = fullImg.absUrl("src");
         */
        return null;
    }

}