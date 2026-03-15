package com.creation.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("artwork")
public class Artwork {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 作者ID
     */
    private Long userId;

    /**
     * 作品标题
     */
    private String title;

    /**
     * 作品正文内容
     */
    private String content;

    /**
     * 封面图URL
     */
    private String coverUrl;

    /**
     * 作品简介/创作说明
     */
    private String description;

    /**
     * 所属分类ID
     */
    private Long categoryId;

    /**
     * AI生成的作品分析与摘要
     */
    private String aiSummary;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 作品字数
     */
    private Integer wordCount;

    /**
     * 作品状态: 1-已发布 0-草稿
     */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
