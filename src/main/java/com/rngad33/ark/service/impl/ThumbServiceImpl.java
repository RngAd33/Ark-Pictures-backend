package com.rngad33.ark.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rngad33.ark.mapper.ThumbMapper;
import com.rngad33.ark.model.dto.thumb.ThumbRequest;
import com.rngad33.ark.model.entity.Picture;
import com.rngad33.ark.model.entity.Thumb;
import com.rngad33.ark.service.PictureService;
import com.rngad33.ark.service.ThumbService;
import com.rngad33.ark.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

/**
 * 点赞服务实现类
 */
@Service
public class ThumbServiceImpl extends ServiceImpl<ThumbMapper, Thumb> implements ThumbService {

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
        long userId = thumbRequest.getUserId();
        long pictureId = thumbRequest.getPictureId();

        // 判断是否已经点赞

        // 加分布式锁，执行操作

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

    /**
     * 查看某用户赞过的图片 id
     *
     * @param userId
     * @return
     */
    @Override
    public long[] listThumbIds(long userId) {
        QueryWrapper<Thumb> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id").eq("userId", userId);
        List<Thumb> list = this.list(queryWrapper);
        return list.stream().mapToLong(Thumb::getId).toArray();
    }

    /**
     * 统计某图片的点赞量
     *
     * @param pictureId
     * @return
     */
    @Override
    public long countThumb(long pictureId) {
        QueryWrapper<Thumb> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pictureId", pictureId);
        return this.count(queryWrapper);
    }

}