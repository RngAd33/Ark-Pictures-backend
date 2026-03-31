package com.rngad33.ark.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 点赞实体类
 */
@Data
@TableName("thumb")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Thumb {

    /**
     * id
     */
    private Long id;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 图片 id
     */
    private Long pictureId;

    /**
     * 创建时间
     */
    private Long createTime;

}