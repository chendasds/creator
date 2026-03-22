package com.creation.platform.dto;

import lombok.Data;

/**
 * 用户互动 DTO
 */
@Data
public class InteractionDTO {

    /**
     * 作品ID
     */
    private Long artworkId;

    /**
     * 互动类型: 1-点赞 2-收藏
     */
    private Integer interactionType;
}
