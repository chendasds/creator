package com.creation.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.creation.platform.entity.UserInteraction;

public interface UserInteractionService extends IService<UserInteraction> {

    /**
     * 点赞或收藏作品
     */
    boolean interact(Long userId, Long artworkId, Integer interactionType);

    /**
     * 取消点赞或收藏
     */
    boolean cancelInteraction(Long userId, Long artworkId, Integer interactionType);

    /**
     * 检查用户是否已点赞/收藏
     */
    boolean hasInteracted(Long userId, Long artworkId, Integer interactionType);
}
