package com.rngad33.web.model;

import lombok.Data;
import java.util.Date;
import java.util.List;

/**
 * 图片模型
 */
@Data
public class Picture {

    /**
     * id
     */
    private Long id;

    /**
     * 图片地址
     */
    private String url;

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
     * 标签列表
     */
    private List<String> tags;

    /**
     * 尺寸信息
     */
    private Long picSize;

    /**
     * 宽
     */
    private Integer picWidth;

    /**
     * 高
     */
    private Integer picHeight;

    /**
     * 宽高比
     */
    private Double picScale;

    /**
     * 图片格式
     */
    private String picFormat;

    /**
     * 创建人id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 用户编辑时间
     */
    private Date editTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

}