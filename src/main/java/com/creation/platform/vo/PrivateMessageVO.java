package com.creation.platform.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PrivateMessageVO {

    private Long id;

    private Long senderId;

    private Long receiverId;

    /**
     * 发送者昵称
     */
    private String senderName;

    /**
     * 发送者头像
     */
    private String senderAvatar;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 是否已读: 0-未读 1-已读
     */
    private Integer isRead;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
