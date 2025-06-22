package com.rngad33.web.manager.jsoup;

import cn.hutool.core.util.ObjUtil;
import com.rngad33.web.model.enums.misc.ErrorCodeEnum;
import com.rngad33.web.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

/**
 * Bing源模板
 */
@Service
@Slf4j
public class JsoupTemplateFromBing extends JsoupTemplate {

    /**
     * 获取图片元素
     *
     * @param document
     * @return
     */
    protected Elements getImgElement(Document document) {
        // 解析图片元素
        Element div = document.getElementsByClass("dgControl").first();
        log.error("——！抓取外层元素失败！——");
        ThrowUtils.throwIf(ObjUtil.isEmpty(div), ErrorCodeEnum.NOT_PARAMS);
        // 筛选图片元素（选择所有类名为 mimg 的 <img> 标签并存储在 imgElementList 中）
        Elements imgElementList = div.select("img.mimg");
        log.info(">>>元素抓取完毕，开始上传图片");
        return imgElementList;
    }

    /**
     * 获取图片地址
     *
     * @param imgElement
     * @return
     */
    protected String getFileUrl(Element imgElement) {
        return imgElement.attr("src");
    }

}
