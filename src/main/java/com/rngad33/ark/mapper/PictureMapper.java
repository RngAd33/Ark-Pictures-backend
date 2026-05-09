package com.rngad33.ark.mapper;

import com.mybatisflex.core.BaseMapper;
import com.rngad33.ark.model.entity.Picture;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 图片 Mapper
 */
public interface PictureMapper extends BaseMapper<Picture> {

    void batchUpdateThumbCount(@Param("countMap") Map<Long, Long> countMap);

}