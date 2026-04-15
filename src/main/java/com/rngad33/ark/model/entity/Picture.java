package com.rngad33.ark.model.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.util.Date;

/**
 * 图片模型
 *
 * @TableName picture
 */
@Data
@Table("picture")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Picture {

    /**
     * id
     * 当前策略为随机长id
     */
    @Id(keyType = KeyType.Generator)
    private Long id;

    /**
     * 原图 url
     */
    @Column("origin_url")
    private String originUrl;

    /**
     * 缩略图 url
     */
    @Column("thumb_url")
    private String thumbUrl;

    /**
     * 图片名称
     */
    private String name;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签（JSON数组）
     */
    private String tags;

    /**
     * 图片体积
     */
    @Column("pic_size")
    private Long picSize;

    /**
     * 图片宽度
     */
    @Column("pic_width")
    private Integer picWidth;

    /**
     * 图片高度
     */
    @Column("pic_height")
    private Integer picHeight;

    /**
     * 图片宽高比例
     */
    @Column("pic_scale")
    private Double picScale;

    /**
     * 图片格式
     */
    @Column("pic_format")
    private String picFormat;

    /**
     * 创建用户 id
     */
    @Column("user_id")
    private Long userId;

    /**
     * 点赞量
     */
    @Column("thumb_count")
    private Long thumbCount;

    /**
     * 创建时间
     */
    @Column("create_time")
    private Date createTime;

    /**
     * 编辑时间
     */
    @Column("edit_time")
    private Date editTime;

    /**
     * 更新时间
     */
    @Column("update_time")
    private Date updateTime;

    /**
     * 审核状态：0-待审核; 1-通过; 2-拒绝
     */
    @Column("review_status")
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    @Column("review_message")
    private String reviewMessage;

    /**
     * 审核人id
     */
    @Column("reviewer_id")
    private Long reviewerId;

    /**
     * 审核时间
     */
    @Column("review_time")
    private Date reviewTime;

    /**
     * 是否删除
     */
    @Column(value = "is_delete", isLogicDelete = true)
    private Integer isDelete;

}