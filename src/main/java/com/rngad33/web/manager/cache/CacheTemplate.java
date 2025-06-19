package com.rngad33.web.manager.cache;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rngad33.web.model.dto.picture.PictureQueryRequest;
import com.rngad33.web.model.entity.Picture;
import com.rngad33.web.model.vo.PictureVO;
import com.rngad33.web.service.PictureService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.DigestUtils;

/**
 * 分页获取图片列表（有缓存）模板抽象类
 */
public abstract class CacheTemplate {

    @Resource
    private PictureService pictureService;

    /**
     * 分页获取图片列表
     *
     * @param pictureQueryRequest
     * @param request
     * @return
     */
    public Page<PictureVO> listPictureVOByPageWithCache(PictureQueryRequest pictureQueryRequest, HttpServletRequest request) {
        // 优先查询缓存，没查到就查询数据库
        // - 获取查询参数
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        // - 构建key
        String queryCondition = JSONUtil.toJsonStr(pictureQueryRequest);   // 序列化
        String hashKey = DigestUtils.md5DigestAsHex(queryCondition.getBytes());
        String cachedValue = getCachedValue(hashKey);
        if (cachedValue != null) {
            // - 缓存命中，缓存查询结果
            return JSONUtil.toBean(cachedValue, Page.class);
        }
        // - 缓存未命中，查询数据库
        Page<Picture> picturePage = pictureService.page(new Page<>(current, size),
                pictureService.getQueryWrapper(pictureQueryRequest));
        Page<PictureVO> pictureVOPage = pictureService.getPictureVOPage(picturePage, request);
        String cacheValue = JSONUtil.toJsonStr(pictureVOPage);   // 序列化
        // - 设置缓存有效期
        int cacheExpireTime = 300 + RandomUtil.randomInt(0, 300);   // 预留区间，防止缓存雪崩
        // - 写入缓存
        setCache(hashKey, cacheValue, cacheExpireTime);
        return pictureVOPage;
    }

    /**
     * 获取缓存数据
     *
     * @param hashKey
     * @return cachedValue
     */
    protected abstract String getCachedValue(String hashKey);

    /**
     * 将数据写入缓存
     *
     * @param hashKey
     * @param cacheValue
     * @param cacheExpireTime
     */
    protected abstract void setCache(String hashKey, String cacheValue, int cacheExpireTime);

}