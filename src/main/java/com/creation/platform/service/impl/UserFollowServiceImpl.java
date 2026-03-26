package com.creation.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creation.platform.entity.User;
import com.creation.platform.entity.UserFollow;
import com.creation.platform.mapper.UserFollowMapper;
import com.creation.platform.service.NotificationService;
import com.creation.platform.service.UserFollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户关注关系 Service 实现
 */
@Service
public class UserFollowServiceImpl extends ServiceImpl<UserFollowMapper, UserFollow> implements UserFollowService {

    @Autowired
    private NotificationService notificationService;

    @Override
    public boolean toggleFollow(Long followerId, Long followeeId) {
        // 先查当前关注状态，用于判断是否为新增关注
        Integer beforeStatus = baseMapper.checkFollowStatus(followerId, followeeId);
        boolean wasFollowing = (beforeStatus != null && beforeStatus == 0);

        boolean success = baseMapper.toggleFollow(followerId, followeeId) > 0;

        // --- 埋点：触发关注通知 (type = 4) ---
        // 从"未关注/已取消"变为"已关注"
        if (success && !wasFollowing) {
            notificationService.sendNotification(followeeId, followerId, 4, followerId, null);
        }

        return success;
    }

    @Override
    public List<User> getFollowings(Long userId) {
        return baseMapper.selectFollowings(userId);
    }

    @Override
    public List<User> getFollowers(Long userId) {
        return baseMapper.selectFollowers(userId);
    }
}
