package com.rngad33.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rngad33.web.model.Picture;
import com.rngad33.web.service.PictureService;
import com.rngad33.web.mapper.PictureMapper;
import org.springframework.stereotype.Service;

/**
 * 图片业务实现
 *
 * @author Dr.YX
 * @description 针对表【picture(图片)】的数据库操作Service实现
 * @createDate 2025-04-29 15:27:12
 */
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture> implements PictureService {

}