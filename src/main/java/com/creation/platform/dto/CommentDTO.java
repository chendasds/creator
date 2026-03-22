package com.creation.platform.dto;

import lombok.Data;

/**
 * 评论请求 DTO
 */
@Data
public class CommentDTO {

    /**
     * 作品ID
     */
    private Long artworkId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 父评论ID（用于回复，可为 null 表示顶级评论）
     */
    private Long parentId;
}
