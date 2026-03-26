package com.creation.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.creation.platform.entity.User;
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

    /**
     * 获取指定用户关注的人列表
     * @param userId 当前查询的用户ID
     * @return 被关注者的用户信息列表（按关注时间倒序）
     */
    java.util.List<User> getFollowings(Long userId);

    /**
     * 获取指定用户的粉丝列表
     * @param userId 当前查询的用户ID
     * @return 粉丝的用户信息列表（按关注时间倒序）
     */
    java.util.List<User> getFollowers(Long userId);
}
