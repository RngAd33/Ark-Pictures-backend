package com.rngad33.ark.model.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * 点赞实体类
 */
@Data
@Table("thumb")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Thumb {

    /**
     * id
     */
    @Id(keyType = KeyType.Generator)
    private Long id;

    /**
     * 用户 id
     */
    @Column("user_id")
    private Long userId;

    /**
     * 图片 id
     */
    @Column("picture_id")
    private Long pictureId;

    /**
     * 创建时间
     */
    @Column("create_time")
    private Long createTime;

}