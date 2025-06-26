package com.rngad33.web.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rngad33.web.constant.UrlConstant;
import com.rngad33.web.exception.MyException;
import com.rngad33.web.manager.UserManager;
import com.rngad33.web.manager.jsoup.JsoupTemplate;
import com.rngad33.web.manager.jsoup.JsoupTemplateFromBing;
import com.rngad33.web.manager.jsoup.JsoupTemplateFromKonachan;
import com.rngad33.web.manager.jsoup.JsoupTemplateFromSafebooru;
import com.rngad33.web.manager.upload.PictureUploadTemplate;
import com.rngad33.web.manager.upload.PictureUploadTemplateImplByFile;
import com.rngad33.web.manager.upload.PictureUploadTemplateImplByUrl;
import com.rngad33.web.mapper.PictureMapper;
import com.rngad33.web.model.dto.file.PictureUploadResult;
import com.rngad33.web.model.dto.picture.PictureQueryRequest;
import com.rngad33.web.model.dto.picture.PictureReviewRequest;
import com.rngad33.web.model.dto.picture.PictureUploadByBatchRequest;
import com.rngad33.web.model.dto.picture.PictureUploadRequest;
import com.rngad33.web.model.entity.Picture;
import com.rngad33.web.model.entity.User;
import com.rngad33.web.model.enums.misc.ErrorCodeEnum;
import com.rngad33.web.model.enums.picture.PictureReviewStatusEnum;
import com.rngad33.web.model.vo.PictureVO;
import com.rngad33.web.model.vo.UserVO;
import com.rngad33.web.service.PictureService;
import com.rngad33.web.service.UserService;
import com.rngad33.web.utils.ThrowUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 图片业务实现
 */
@Slf4j
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture> implements PictureService {

    @Resource
    private UserManager userManager;

    @Resource
    private UserService userService;

    @Resource
    private PictureUploadTemplateImplByFile pictureUploadTemplateImplByFile;

    @Resource
    private PictureUploadTemplateImplByUrl pictureUploadTemplateImplByUrl;

    @Resource
    private JsoupTemplateFromBing jsoupTemplateFromBing;

    @Resource
    private JsoupTemplateFromSafebooru jsoupTemplateFromSafebooru;

    @Resource
    private JsoupTemplateFromKonachan jsoupTemplateFromKonachan;

    /**
     * 图片上传
     *
     * @param inputSource 文件输入源（文件 / url）
     * @param pictureUploadRequest 上传请求
     * @return 图片封装类
     */
    @Override
    public PictureVO uploadPicture(Object inputSource, PictureUploadRequest pictureUploadRequest, User loginUser) {
        // 校验是否登录
        ThrowUtils.throwIf(loginUser == null, ErrorCodeEnum.USER_NOT_LOGIN);
        // 判断是新增还是删除
        Long pictureId = null;
        if (pictureUploadRequest != null) {
            pictureId = pictureUploadRequest.getId();
        }
        // 如果是更新，判断图片是否存在
        if (pictureId != null) {
            Picture oldPicture = this.getById(pictureId);
            ThrowUtils.throwIf(oldPicture == null, ErrorCodeEnum.NOT_PARAMS, "图片不存在！");
            // - 仅本人或管理员有权编辑图片
            if (!oldPicture.getUserId().equals(loginUser.getId()) && userManager.isNotAdmin(loginUser)) {
                throw new MyException(ErrorCodeEnum.USER_NOT_AUTH);
            }
        }
        // 按照用户id划分目录
        String uploadFilePrefix = String.format("public/%s", loginUser.getId());
        // - 根据文件输入源的类型，判断使用哪种方法上传文件
        PictureUploadTemplate pictureUploadTemplate = pictureUploadTemplateImplByFile;
        if (inputSource instanceof String) {
            pictureUploadTemplate = pictureUploadTemplateImplByUrl;
        }
        // 上传图片
        PictureUploadResult pictureUploadResult = pictureUploadTemplate.uploadPicture(inputSource, uploadFilePrefix);
        // 构造要入库的图片信息
        // - 支持外层传递图片名称
        String picName = pictureUploadResult.getPicName();
        if (pictureUploadRequest != null && StrUtil.isNotBlank(pictureUploadRequest.getName())) {
            picName = pictureUploadRequest.getName();
        }
        Picture picture = new Picture();
        picture.setName(picName);
        picture.setOriginUrl(pictureUploadResult.getOriginUrl());
        picture.setThumbUrl(pictureUploadResult.getThumbUrl());
        picture.setPicSize(pictureUploadResult.getPicSize());
        picture.setPicWidth(pictureUploadResult.getPicWidth());
        picture.setPicHeight(pictureUploadResult.getPicHeight());
        picture.setPicScale(pictureUploadResult.getPicScale());
        picture.setPicFormat(pictureUploadResult.getPicFormat());
        picture.setUserId(loginUser.getId());
        // 更新，补充信息
        userManager.fillReviewParams(picture, loginUser);   // 审核参数
        if (pictureId != null) {
            picture.setId(pictureId);
            picture.setEditTime(new Date());
        }
        // 操作数据库
        boolean result = this.saveOrUpdate(picture);   // 方法来自：MyBatis-Plus
        ThrowUtils.throwIf(!result, ErrorCodeEnum.USER_LOSE_ACTION, "上传失败！");
        return PictureVO.objToVo(picture);
    }

    /**
     * 获取查询条件
     *
     * @param pictureQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) {
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        if (pictureQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = pictureQueryRequest.getId();
        String name = pictureQueryRequest.getName();
        String introduction = pictureQueryRequest.getIntroduction();
        String category = pictureQueryRequest.getCategory();
        List<String> tags = pictureQueryRequest.getTags();
        Long picSize = pictureQueryRequest.getPicSize();
        Integer picWidth = pictureQueryRequest.getPicWidth();
        Integer picHeight = pictureQueryRequest.getPicHeight();
        Double picScale = pictureQueryRequest.getPicScale();
        String picFormat = pictureQueryRequest.getPicFormat();
        String searchText = pictureQueryRequest.getSearchText();
        Long userId = pictureQueryRequest.getUserId();
        Integer reviewStatus = pictureQueryRequest.getReviewStatus();
        String reviewMessage = pictureQueryRequest.getReviewMessage();
        Long reviewId = pictureQueryRequest.getReviewId();
        Date reviewTime = pictureQueryRequest.getReviewTime();
        String sortField = pictureQueryRequest.getSortField();
        String sortOrder = pictureQueryRequest.getSortOrder();
        // 从多字段中搜索
        if (StrUtil.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("name", searchText)
                    .or()
                    .like("introduction", searchText)
            );
        }
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        queryWrapper.like(StrUtil.isNotBlank(introduction), "introduction", introduction);
        queryWrapper.like(StrUtil.isNotBlank(picFormat), "picFormat", picFormat);
        queryWrapper.like(StrUtil.isNotBlank(reviewMessage), "reviewMessage", reviewMessage);
        queryWrapper.eq(StrUtil.isNotBlank(category), "category", category);
        queryWrapper.eq(ObjUtil.isNotEmpty(picWidth), "picWidth", picWidth);
        queryWrapper.eq(ObjUtil.isNotEmpty(picHeight), "picHeight", picHeight);
        queryWrapper.eq(ObjUtil.isNotEmpty(picSize), "picSize", picSize);
        queryWrapper.eq(ObjUtil.isNotEmpty(picScale), "picScale", picScale);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewStatus), "reviewStatus", reviewStatus);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewId), "reviewId", reviewId);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewTime), "reviewTime", reviewTime);
        // JSON 数组查询
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 排序
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    /**
     * 校验图片信息
     *
     * @param picture
     */
    @Override
    public void validPicture(Picture picture) {
        ThrowUtils.throwIf(picture == null, ErrorCodeEnum.NOT_PARAMS, "请先选择图片！");
        Long id = picture.getId();
        String originUrl = picture.getOriginUrl();
        String thumbUrl = picture.getThumbUrl();
        String introduction =picture.getIntroduction();
        ThrowUtils.throwIf(ObjUtil.isNull(id), ErrorCodeEnum.PARAMS_ERROR, "id为空！");
        if (StringUtils.isAnyBlank(originUrl, thumbUrl)) {
            ThrowUtils.throwIf(originUrl.length() > 1024, ErrorCodeEnum.PARAMS_ERROR, "url过长！");
            ThrowUtils.throwIf(thumbUrl.length() > 1024, ErrorCodeEnum.PARAMS_ERROR, "url过长！");
        }
        if (StrUtil.isNotBlank(introduction)) {
            ThrowUtils.throwIf(introduction.length() > 800, ErrorCodeEnum.PARAMS_ERROR, "简介过长！");
        }
    }

    /**
     * 获取单个图片封装
     *
     * @param picture
     * @param request
     * @return
     */
    @Override
    public PictureVO getPictureVO(Picture picture, HttpServletRequest request) {
        // 对象转封装类
        PictureVO pictureVO = PictureVO.objToVo(picture);
        // 关联查询用户信息
        Long userId = picture.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVO userVO = UserVO.objToVo(user);
            pictureVO.setUser(userVO);
        }
        return pictureVO;
    }

    /**
     * 分页获取图片封装
     *
     * @param picturePage
     * @param request
     * @return
     */
    @Override
    public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request) {
        List<Picture> pictureList = picturePage.getRecords();
        Page<PictureVO> pictureVOPage = new Page<> (
                picturePage.getCurrent(),
                picturePage.getSize(),
                picturePage.getTotal()
        );
        if (CollUtil.isEmpty(pictureList)) {
            return pictureVOPage;
        }
        // 对象列表 => 封装对象列表
        List<PictureVO> pictureVOList = pictureList.stream()
                .map(PictureVO::objToVo)
                .collect(Collectors.toList());
        // 1. 关联查询用户信息
        Set<Long> userIdSet = pictureList.stream()
                .map(Picture::getUserId)
                .collect(Collectors.toSet());
        // n -> userN
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet)
                .stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 填充信息
        pictureVOList.forEach(pictureVO -> {
            Long userId = pictureVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            pictureVO.setUser(UserVO.objToVo(user));
        });
        pictureVOPage.setRecords(pictureVOList);
        return pictureVOPage;
    }

    /**
     * 图片审核（仅管理员）
     *
     * @param pictureReviewRequest
     * @param loginUser
     */
    @Override
    public void reviewPicture(PictureReviewRequest pictureReviewRequest, User loginUser) {
        // 1. 校验参数
        ThrowUtils.throwIf(pictureReviewRequest == null, ErrorCodeEnum.NOT_PARAMS);
        Long id = pictureReviewRequest.getId();
        Integer reviewStatus = pictureReviewRequest.getReviewStatus();
        PictureReviewStatusEnum pictureReviewStatusEnum = PictureReviewStatusEnum.getEnumByValue(reviewStatus);
        if (id == null || pictureReviewStatusEnum == null
                || PictureReviewStatusEnum.REVIEWING.equals(pictureReviewStatusEnum)) {
            throw new MyException(ErrorCodeEnum.NOT_PARAMS);
        }
        // 2. 判断图片是否存在
        Picture oldPicture = this.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCodeEnum.NOT_PARAMS);
        // 3. 检查审核状态是否重复
        if (oldPicture.getReviewStatus().equals(reviewStatus)) {
            throw new MyException(ErrorCodeEnum.PARAMS_ERROR);
        }
        // 4. 操作数据库
        Picture uploadPicture = new Picture();
        BeanUtil.copyProperties(pictureReviewRequest, uploadPicture);
        uploadPicture.setReviewerId(loginUser.getId());
        uploadPicture.setReviewTime(new Date());
        boolean result = this.updateById(uploadPicture);
        ThrowUtils.throwIf(!result, ErrorCodeEnum.USER_LOSE_ACTION);
    }

    /**
     * 抓取图片批量上传（仅管理员）
     *
     * @param pictureUploadByBatchRequest 批量上传请求体
     * @param loginUser 当前用户
     * @return 实际上传数量
     */
    @Override
    public Integer uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser) {
        // 设置图源仓库（默认为Bing图源）
        String library = pictureUploadByBatchRequest.getLibrary();
        JsoupTemplate jsoupTemplate = jsoupTemplateFromBing;
        if (library.equals(UrlConstant.sourceSafebooru)) {
            jsoupTemplate = jsoupTemplateFromSafebooru;
            log.info("已切换到Safebooru源");
        } else if (library.equals(UrlConstant.sourceKonachan)) {
            jsoupTemplate = jsoupTemplateFromKonachan;
            log.info("已切换到Konachan源");
        } else {
            log.info("已切换到Bing源");
        }
        // 抓取图片
        return jsoupTemplate.executePictures(pictureUploadByBatchRequest, loginUser);
    }

}