package com.rngad33.web.manager.jsoup;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.rngad33.web.constant.UrlConstant;
import com.rngad33.web.exception.MyException;
import com.rngad33.web.model.dto.picture.PictureUploadByBatchRequest;
import com.rngad33.web.model.dto.picture.PictureUploadRequest;
import com.rngad33.web.model.entity.User;
import com.rngad33.web.model.enums.misc.ErrorCodeEnum;
import com.rngad33.web.model.vo.PictureVO;
import com.rngad33.web.service.PictureService;
import com.rngad33.web.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

/**
 * Jsoup模板方法
 */
@Service
@Slf4j
public abstract class JsoupTemplate {

    private final PictureService pictureService;

    /**
     * 构造函数注入 + 懒加载
     *
     * @param pictureService
     */
    public JsoupTemplate(@Lazy PictureService pictureService) {
        this.pictureService = pictureService;
    }

    /**
     * 定义抓取流程
     *
     * @param pictureUploadByBatchRequest
     * @param loginUser
     * @return
     * @throws IOException
     */
    public final int executePictures(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser) {
        // 设置搜索参数
        String searchText = pictureUploadByBatchRequest.getSearchText();
        Integer count = pictureUploadByBatchRequest.getCount();
        ThrowUtils.throwIf(count > 30, ErrorCodeEnum.PARAMS_ERROR, "一次最多抓取30条数据！");
        String fetchUrl = String.format(UrlConstant.sourceBing, searchText);
        // - 图片名称前缀默认为搜索词
        String namePrefix = pictureUploadByBatchRequest.getNamePrefix();
        if (StrUtil.isBlank(namePrefix)) {
            namePrefix = searchText;
        }
        // 解析图源
        Document document = null;
        int loseCount = 0;
        log.info(">>>抓取器初始化完成，正在连接图源");
        do {
            try {
                document = Jsoup.connect(fetchUrl).get();
                loseCount = 0;
                log.info(">>>图源连接成功，开始抓取元素");
            } catch (IOException e) {
                loseCount++;
                log.error("——！图源连接失败，正在重新建立连接！——");
                ThrowUtils.throwIf(loseCount > 12, ErrorCodeEnum.TOO_MANY_TIMES_MESSAGE, "多次连接失败，进程已终止！");
            }
        } while (document == null);
        if (ObjUtil.isEmpty(document)) {
            log.error("——！抓取外层元素失败！——");
            throw new MyException(ErrorCodeEnum.NOT_PARAMS);
        }
        log.info(">>>外层元素抓取完毕，开始抓取内层元素");
        // 解析图片元素
        Elements imgElementList = this.getImgElement(document);
        // 遍历元素，依次上传
        int uploadCount = 0;
        for (Element imgElement : imgElementList) {
            // 获取图片详情页的 URL
            String fileUrl = this.getFileUrl(imgElement);
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
            uploadCount = this.uploadPic(namePrefix, fileUrl, uploadCount, loginUser);
            if (uploadCount >= count) {
                break;
            }
        }
        return uploadCount;
    }

    /**
     * 获取图片元素
     *
     * @return 缩略图表
     */
    protected abstract Elements getImgElement(Document document);

    /**
     * 获取图片地址
     *
     * @return
     */
    protected abstract String getFileUrl(Element imgElement);

    /**
     * 上传图片
     *
     * @param fileUrl 文件地址
     * @param loginUser 当前用户
     * @return 是否完成任务
     */
    private int uploadPic(String namePrefix, String fileUrl, int uploadCount, User loginUser) {
        PictureUploadRequest pictureUploadRequest = new PictureUploadRequest();
        pictureUploadRequest.setFileUrl(fileUrl);
        pictureUploadRequest.setName(namePrefix + (uploadCount + 1) + new Date());
        try {
            PictureVO pictureVO = pictureService.uploadPicture(fileUrl, pictureUploadRequest, loginUser);
            log.info(">>>已上传图片：{}", pictureVO.getId());
            uploadCount++;
        } catch (Exception e) {
            log.error("——！图片上传失败，正在尝试重新抓取！——");
        }
        return uploadCount;
    }

}