package com.rngad33.ark.mapper;

import com.mybatisflex.core.BaseMapper;
import com.rngad33.ark.model.entity.Thumb;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface ThumbMapper extends BaseMapper<Thumb> {
    void batchUpdateThumbCount(@Param("countMap") Map<Long, Long> countMap);
}