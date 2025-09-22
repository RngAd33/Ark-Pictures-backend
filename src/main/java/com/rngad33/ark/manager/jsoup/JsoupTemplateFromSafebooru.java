package com.rngad33.ark.manager.jsoup;

import com.rngad33.ark.model.enums.misc.ErrorCodeEnum;
import com.rngad33.ark.service.PictureService;
import com.rngad33.ark.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Safebooru源模板
 */
@Service
@Slf4j
public class JsoupTemplateFromSafebooru extends JsoupTemplate {

    /**
     * 构造函数注入 + 懒加载
     *
     * @param pictureService
     */
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
        // 筛选图片元素
        Elements imgElementList = document.select("img.preview");
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
    protected String getFileUrl(Element imgElement) throws IOException {
        String m = imgElement.attr("src");
        // 构造图片详情页地址
        String imageId = m.substring(m.lastIndexOf("?") + 1);
        String detailPageUrl = "https://safebooru.org/index.php?page=post&s=view&id=" + imageId;
        // 获取原图地址
        Document doc = Jsoup.connect(detailPageUrl).get();
        Element originalLink = doc.select("div.link-list a:contains(Original image)").first();
        if (originalLink != null) {
            String href = originalLink.attr("href");
            if (href.startsWith("http")) {
                return href;
            } else if (href.startsWith("/")) {
                return "https://safebooru.org" + href;
            }
        }
        return null;
    }

}