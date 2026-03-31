package com.rngad33.ark.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rngad33.ark.mapper.ThumbMapper;
import com.rngad33.ark.model.dto.thumb.ThumbRequest;
import com.rngad33.ark.model.entity.Thumb;
import com.rngad33.ark.service.PictureService;
import com.rngad33.ark.service.ThumbService;
import com.rngad33.ark.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 点赞服务实现类
 */
@Service
public class ThumbServiceImpl extends ServiceImpl<ThumbMapper, Thumb> implements ThumbService {

    @Resource
    private PictureService pictureService;

    @Resource
    private UserService userService;

    @Resource
    private TransactionTemplate transactionTemplate;

    /**
     * 点赞
     *
     * @param thumbRequest
     * @return
     */
    @Override
    public boolean doThumb(ThumbRequest thumbRequest) {
        return false;
    }

    /**
     * 取消点赞
     *
     * @param thumbRequest
     * @return
     */
    @Override
    public boolean unThumb(ThumbRequest thumbRequest) {
        return false;
    }

}