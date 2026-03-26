package com.creation.platform.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConversationVO {

    /**
     * 对方用户的ID
     */
    private Long targetUserId;

    /**
     * 对方用户昵称
     */
    private String targetUserName;

    /**
     * 对方用户头像
     */
    private String targetUserAvatar;

    /**
     * 最后一条消息内容
     */
    private String lastMessage;

    /**
     * 最后一条消息时间
     */
    private LocalDateTime lastMessageTime;

    /**
     * 未读消息数
     */
    private Integer unreadCount;
}
