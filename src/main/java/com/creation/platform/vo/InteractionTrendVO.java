package com.creation.platform.vo;

import lombok.Data;

@Data
public class InteractionTrendVO {

    private String date;

    private Long likeCount = 0L;

    private Long collectCount = 0L;

    private Long fanCount = 0L;

    private Long artworkCount = 0L;
}
