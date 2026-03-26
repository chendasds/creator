package com.creation.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creation.platform.entity.Notification;
import com.creation.platform.entity.User;
import com.creation.platform.mapper.NotificationMapper;
import com.creation.platform.service.NotificationService;
import com.creation.platform.service.UserService;
import com.creation.platform.vo.NotificationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    @Autowired
    private UserService userService;

    @Override
    public List<NotificationVO> getNotificationList(Long userId) {
        return baseMapper.selectNotificationList(userId);
    }
    @Override
    public void readAll(Long userId) {
        LambdaUpdateWrapper<Notification> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Notification::getReceiverId, userId)
                     .eq(Notification::getIsRead, 0)
                     .set(Notification::getIsRead, 1);
        this.update(updateWrapper);
    }
    @Override
    public Integer getUnreadCount(Long userId) {
        return baseMapper.countUnread(userId);
    }

    // 异步执行发通知，不阻塞主业务逻辑
    @Async
    @Override
    public void sendNotification(Long receiverId, Long senderId, Integer type, Long targetId, String content) {
        if (receiverId.equals(senderId)) return; // 自己操作自己的不发通知
        Notification notification = new Notification();
        notification.setReceiverId(receiverId);
        notification.setSenderId(senderId);
        notification.setType(type);
        notification.setTargetId(targetId);
        notification.setContent(content);
        this.save(notification);
    }

    @Async
    @Transactional
    @Override
    public void broadcast(Long adminId, String content) {
        // 1. 获取所有用户 (排除掉发送者自己)
        List<User> allUsers = userService.list();

        List<Notification> batch = allUsers.stream()
                .filter(user -> !user.getId().equals(adminId))
                .map(user -> {
                    Notification n = new Notification();
                    n.setReceiverId(user.getId());
                    n.setSenderId(adminId);
                    n.setType(5); // 系统通知类型
                    n.setContent(content);
                    n.setIsRead(0);
                    return n;
                }).collect(Collectors.toList());

        // 2. 批量保存，效率极高
        this.saveBatch(batch);
    }
}
