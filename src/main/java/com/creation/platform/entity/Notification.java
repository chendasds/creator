package com.creation.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("notification")
public class Notification {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long receiverId;
    private Long senderId;
    private Integer type; // 1-点赞 2-收藏 3-评论 4-关注 5-系统
    private Long targetId;
    private String content;
    private Integer isRead;
    private LocalDateTime createTime;
    @TableLogic
    private Integer isDeleted;
}
