package com.creation.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("comment")
public class Comment {

    @TableId(value = "id", type = IdType.AUTO)
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
     * 父评论ID（用于回复）
     */
    private Long parentId;

    /**
     * 评论内容
     */
    private String content;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
