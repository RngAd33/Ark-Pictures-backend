package com.rngad33.web.manager;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.rngad33.web.constant.UrlConstant;
import com.rngad33.web.model.dto.picture.PictureUploadRequest;
import com.rngad33.web.model.entity.User;
import com.rngad33.web.model.enums.misc.ErrorCodeEnum;
import com.rngad33.web.model.vo.PictureVO;
import com.rngad33.web.service.PictureService;
import com.rngad33.web.utils.ThrowUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

/**
 * Jsoup通用化策略
 */
@Service
@Slf4j
public class JsoupManager {

    @Resource
    private PictureService pictureService;

    public int executePictures(int count, String searchText, String namePrefix, User loginUser) {
        // 设置图源
        String fetchUrl = String.format(UrlConstant.sourceBing, searchText);
        // 抓取图片
        Document document = null;
        int loseCount = 0;
        log.info("抓取器已启动，正在连接图源>>>");
        do {
            try {
                document = Jsoup.connect(fetchUrl).get();
                loseCount = 0;
                log.info(">>>图源连接成功，开始抓取元素");
            } catch (IOException e) {
                loseCount++;
                log.error("——！图源连接失败，正在重新建立连接！——");
                ThrowUtils.throwIf(loseCount > 12,
                        ErrorCodeEnum.TOO_MANY_TIMES_MESSAGE, "抓取器联网多次失败，进程已终止！");
            }
        } while (document == null);
        // 解析图片元素
        Element div = document.getElementsByClass("dgControl").first();
        log.error("——！抓取外层元素失败！——");
        ThrowUtils.throwIf(ObjUtil.isEmpty(div), ErrorCodeEnum.NOT_PARAMS);
        // 筛选图片元素（选择所有类名为 mimg 的 <img> 标签并存储在 imgElementList 中）
        Elements imgElementList = div.select(".post-preview a img");
        log.info(">>>元素抓取完毕，开始上传图片");
        // 遍历元素，依次上传
        int uploadCount = 0;
        for (Element imgElement : imgElementList) {
            // 获取图片详情页的 URL
            String thumbUrl = imgElement.absUrl("src"); // 缩略图地址
            String detailPageUrl = imgElement.parent().absUrl("href");

            Document detailDoc = Jsoup.connect(detailPageUrl).userAgent("Mozilla/5.0").get();
            Element fullImg = detailDoc.select("img#image").first();
            if (fullImg == null) {
                continue;
            }
            String fileUrl = fullImg.absUrl("src");
            if (StrUtil.isBlank(fileUrl)) {
                log.info("——！当前图片链接为空，已跳过：{}！——", fileUrl);
                continue;
            }
            // 处理图片地址，防止转义或者对象存储冲突
            int questionIndex = fileUrl.indexOf("?");
            if (questionIndex > -1) {
                fileUrl = fileUrl.substring(0, questionIndex);
            }
            // 上传图片
            PictureUploadRequest pictureUploadRequest = new PictureUploadRequest();
            pictureUploadRequest.setFileUrl(fileUrl);
            pictureUploadRequest.setName(namePrefix + (uploadCount + 1) + new Date());
            try {
                PictureVO pictureVO = pictureService.uploadPicture(fileUrl, pictureUploadRequest, loginUser);
                log.info(">>>已上传图片：{}", pictureVO.getId());
                uploadCount++;
            } catch (Exception e) {
                log.error("——！图片上传失败，正在尝试重新抓取！——");
                continue;
            }
            if (uploadCount >= count) {
                break;
            }
        }
        return uploadCount;
    }

}