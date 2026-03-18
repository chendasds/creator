package com.creation.platform.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArtworkVO {

    private Long id;

    /**
     * 作品标题
     */
    private String title;

    /**
     * 封面图URL
     */
    private String coverUrl;

    /**
     * 作品简介/创作说明
     */
    private String description;

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

    private LocalDateTime createTime;

    /**
     * 作者昵称（联表查询结果）
     */
    private String authorName;

    /**
     * 分类名称（联表查询结果）
     */
    private String categoryName;
}
