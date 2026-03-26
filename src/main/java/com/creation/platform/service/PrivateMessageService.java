package com.creation.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.creation.platform.entity.PrivateMessage;
import com.creation.platform.vo.ConversationVO;
import com.creation.platform.vo.PrivateMessageVO;

import java.util.List;

public interface PrivateMessageService extends IService<PrivateMessage> {

    /**
     * 发送消息
     *
     * @param senderId   发送者ID
     * @param receiverId 接收者ID
     * @param content    消息内容
     * @return 消息视图对象
     */
    PrivateMessageVO sendMessage(Long senderId, Long receiverId, String content);

    /**
     * 获取聊天记录
     *
     * @param currentUserId 当前用户ID
     * @param targetUserId  对方用户ID
     * @return 聊天记录列表
     */
    List<PrivateMessageVO> getChatHistory(Long currentUserId, Long targetUserId);

    /**
     * 获取会话列表
     *
     * @param userId 当前用户ID
     * @return 会话列表
     */
    List<ConversationVO> getConversationList(Long userId);

    /**
     * 获取未读消息总数
     *
     * @param userId 用户ID
     * @return 未读消息数量
     */
    Integer getTotalUnread(Long userId);
}
