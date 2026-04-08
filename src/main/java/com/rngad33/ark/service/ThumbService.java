package com.rngad33.ark.service;

import com.mybatisflex.core.service.IService;
import com.rngad33.ark.model.dto.thumb.ThumbRequest;
import com.rngad33.ark.model.entity.Thumb;

import java.util.List;

/**
 * 点赞服务接口
 */
public interface ThumbService extends IService<Thumb> {

    /**
     * 点赞
     */
    boolean doThumb(ThumbRequest thumbRequest);

    /**
     * 取消点赞
     */
    boolean unThumb(ThumbRequest thumbRequest);

    /**
     * 查看某用户赞过的图片 id
     */
    List<Long> listThumbIds(long userId);

    /**
     * 统计某图片的点赞量
     */
    long countThumb(long pictureId);

    /**
     * 判断是否已点赞
     *
     * @param pictureId
     * @param userId
     * @return
     */
    boolean hasThumb(long pictureId, long userId);



}