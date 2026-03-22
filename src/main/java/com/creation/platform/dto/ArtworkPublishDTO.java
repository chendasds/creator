package com.creation.platform.dto;

import lombok.Data;

/**
 * 作品发布 DTO
 */
@Data
public class ArtworkPublishDTO {

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
     * 作品状态: 1-已发布 0-草稿
     */
    private Integer status;
}
