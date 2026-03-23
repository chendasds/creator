package com.creation.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.creation.platform.entity.UserFollow;

/**
 * 用户关注关系 Service 接口
 */
public interface UserFollowService extends IService<UserFollow> {

    /**
     * 切换关注状态（关注/取消关注）
     * @param followerId  关注者用户ID
     * @param followeeId  被关注者用户ID
     * @return 操作是否成功
     */
    boolean toggleFollow(Long followerId, Long followeeId);
}
