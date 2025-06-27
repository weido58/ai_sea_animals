package com.marine.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * 海洋生物基础信息实体类
 */
@Data
@Accessors(chain = true)
@TableName("marine_species")
public class MarineSpecies {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 学名
     */
    @TableField("scientific_name")
    private String scientificName;

    /**
     * 俗名
     */
    @TableField("common_name")
    private String commonName;

    /**
     * 中文名
     */
    @TableField("chinese_name")
    private String chineseName;

    /**
     * 分类信息(JSON格式)
     */
    @TableField("classification")
    private String classification;

    /**
     * 栖息地描述
     */
    @TableField("habitat")
    private String habitat;

    /**
     * 分布区域
     */
    @TableField("distribution")
    private String distribution;

    /**
     * 特征描述
     */
    @TableField("characteristics")
    private String characteristics;

    /**
     * 体型范围
     */
    @TableField("size_range")
    private String sizeRange;

    /**
     * 食性
     */
    @TableField("diet")
    private String diet;

    /**
     * 保护状态
     */
    @TableField("conservation_status")
    private String conservationStatus;

    /**
     * 参考图片URLs(JSON数组)
     */
    @TableField("image_urls")
    private String imageUrls;

    /**
     * 详细描述
     */
    @TableField("description")
    private String description;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    /**
     * 是否删除 0-否 1-是
     */
    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted;
}