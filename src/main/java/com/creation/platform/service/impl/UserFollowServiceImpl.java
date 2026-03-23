package com.creation.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creation.platform.entity.UserFollow;
import com.creation.platform.mapper.UserFollowMapper;
import com.creation.platform.service.UserFollowService;
import org.springframework.stereotype.Service;

/**
 * 用户关注关系 Service 实现
 */
@Service
public class UserFollowServiceImpl extends ServiceImpl<UserFollowMapper, UserFollow> implements UserFollowService {

    @Override
    public boolean toggleFollow(Long followerId, Long followeeId) {
        return baseMapper.toggleFollow(followerId, followeeId) > 0;
    }
}
