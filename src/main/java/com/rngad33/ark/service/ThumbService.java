package com.rngad33.ark.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rngad33.ark.model.dto.thumb.ThumbRequest;
import com.rngad33.ark.model.entity.Thumb;

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
     * 查看是否点赞
     */

    /**
     * 查看某用户赞过的图片 id
     */


}