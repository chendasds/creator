package com.creation.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.creation.platform.entity.Notification;
import com.creation.platform.vo.NotificationVO;

import java.util.List;

public interface NotificationService extends IService<Notification> {
    List<NotificationVO> getNotificationList(Long userId);
    void readAll(Long userId);
    Integer getUnreadCount(Long userId);
    // 发送通知的通用方法
    void sendNotification(Long receiverId, Long senderId, Integer type, Long targetId, String content);
    // 广播通知给所有用户（排除发送者自己）
    void broadcast(Long adminId, String content);
}
