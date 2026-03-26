package com.creation.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creation.platform.entity.PrivateMessage;
import com.creation.platform.entity.User;
import com.creation.platform.mapper.PrivateMessageMapper;
import com.creation.platform.mapper.UserMapper;
import com.creation.platform.service.PrivateMessageService;
import com.creation.platform.vo.ConversationVO;
import com.creation.platform.vo.PrivateMessageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PrivateMessageServiceImpl extends ServiceImpl<PrivateMessageMapper, PrivateMessage> implements PrivateMessageService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public PrivateMessageVO sendMessage(Long senderId, Long receiverId, String content) {
        PrivateMessage msg = new PrivateMessage();
        msg.setSenderId(senderId);
        msg.setReceiverId(receiverId);
        msg.setContent(content);
        this.save(msg);

        PrivateMessageVO vo = new PrivateMessageVO();
        vo.setId(msg.getId());
        vo.setSenderId(senderId);
        vo.setReceiverId(receiverId);
        vo.setContent(content);
        vo.setIsRead(0);
        vo.setCreateTime(msg.getCreateTime());

        User sender = userMapper.selectById(senderId);
        if (sender != null) {
            vo.setSenderName(sender.getNickname());
            vo.setSenderAvatar(sender.getAvatarUrl());
        }
        return vo;
    }

    @Override
    public List<PrivateMessageVO> getChatHistory(Long currentUserId, Long targetUserId) {
        baseMapper.markAsRead(currentUserId, targetUserId);
        return baseMapper.selectChatHistory(currentUserId, targetUserId);
    }

    @Override
    public List<ConversationVO> getConversationList(Long userId) {
        LambdaQueryWrapper<PrivateMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PrivateMessage::getSenderId, userId)
                .or()
                .eq(PrivateMessage::getReceiverId, userId)
                .orderByDesc(PrivateMessage::getCreateTime);
        List<PrivateMessage> allMessages = this.list(queryWrapper);

        if (allMessages == null || allMessages.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, List<PrivateMessage>> conversationMap = new LinkedHashMap<>();
        for (PrivateMessage msg : allMessages) {
            Long targetId = msg.getSenderId().equals(userId) ? msg.getReceiverId() : msg.getSenderId();
            conversationMap.computeIfAbsent(targetId, k -> new ArrayList<>()).add(msg);
        }

        List<ConversationVO> voList = new ArrayList<>();
        for (Map.Entry<Long, List<PrivateMessage>> entry : conversationMap.entrySet()) {
            Long targetId = entry.getKey();
            List<PrivateMessage> msgs = entry.getValue();

            PrivateMessage lastMsg = msgs.get(0);

            long unreadCount = msgs.stream()
                    .filter(m -> m.getReceiverId().equals(userId) && m.getIsRead() == 0)
                    .count();

            ConversationVO vo = new ConversationVO();
            vo.setTargetUserId(targetId);
            vo.setLastMessage(lastMsg.getContent());
            vo.setLastMessageTime(lastMsg.getCreateTime());
            vo.setUnreadCount((int) unreadCount);

            User targetUser = userMapper.selectById(targetId);
            if (targetUser != null) {
                vo.setTargetUserName(targetUser.getNickname() != null ? targetUser.getNickname() : targetUser.getUsername());
                vo.setTargetUserAvatar(targetUser.getAvatarUrl());
            }
            voList.add(vo);
        }

        voList.sort((v1, v2) -> v2.getLastMessageTime().compareTo(v1.getLastMessageTime()));

        return voList;
    }

    @Override
    public Integer getTotalUnread(Long userId) {
        return baseMapper.countTotalUnread(userId);
    }
}
