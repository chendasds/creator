package com.creation.platform.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationVO {
    private Long id;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private Integer type;
    private Long targetId;
    private String targetTitle;
    private String content;
    private Integer isRead;
    private LocalDateTime createTime;
}
