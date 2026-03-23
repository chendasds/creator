package com.creation.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_follow")
public class UserFollow {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关注者用户ID
     */
    private Long followerId;

    /**
     * 被关注者用户ID
     */
    private Long followeeId;

    /**
     * 关注时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 逻辑删除标记（0-未删除，1-已删除）
     */
    @TableLogic
    private Integer isDeleted;
}
