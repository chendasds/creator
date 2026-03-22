package com.creation.platform.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 评论 VO（支持楼中楼嵌套结构）
 */
@Data
public class CommentVO {

    private Long id;

    /**
     * 作品ID
     */
    private Long artworkId;

    /**
     * 评论用户ID
     */
    private Long userId;

    /**
     * 父评论ID
     */
    private Long parentId;

    /**
     * 评论内容
     */
    private String content;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 评论人昵称
     */
    private String authorName;

    /**
     * 评论人头像
     */
    private String authorAvatar;

    /**
     * 回复目标用户昵称（父评论作者）
     */
    private String replyToName;

    /**
     * 子评论列表（楼中楼）
     */
    private List<CommentVO> children = new ArrayList<>();
}
