package com.rngad33.web.manager.jsoup;

import com.rngad33.web.model.enums.misc.ErrorCodeEnum;
import com.rngad33.web.service.PictureService;
import com.rngad33.web.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Safebooru源模板
 */
@Service
@Slf4j
public class JsoupTemplateFromSafebooru extends JsoupTemplate {

    public JsoupTemplateFromSafebooru(@Lazy PictureService pictureService) {
        super(pictureService);
    }

    /**
     * 获取图片元素
     *
     * @param document
     * @return 缩略图表
     */
    protected Elements getImgElement(Document document) {
        // 筛选图片元素（选择所有class="thumb"的span元素内的img标签）
        Elements imgElementList = document.select("span.thumb img");
        ThrowUtils.throwIf(imgElementList.isEmpty(), ErrorCodeEnum.USER_LOSE_ACTION, "未找到图片元素");
        log.info(">>>内层元素抓取完毕，开始上传图片");
        return imgElementList;
    }

    /**
     * 获取图片地址
     *
     * @param imgElement
     * @return
     */
    protected String getFileUrl(Element imgElement) {
        // 获取缩略图所在 a 标签的 href 属性（即详情页 URL）
        String detailPageUrl = imgElement.parent().attr("href");
        // 访问详情页
        Document detailDoc = Jsoup.connect(detailPageUrl).userAgent("Mozilla/5.0").get();
        // 在详情页中选择 id 为 "image" 的 img 标签
        Element fullImg = detailDoc.select("img#image").first();
        if (fullImg != null) {
            // 获取完整图片的 src 属性
            return fullImg.absUrl("src");
        }
        return null;
    }

}