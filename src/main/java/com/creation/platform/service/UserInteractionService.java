package com.creation.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.creation.platform.dto.InteractionDTO;
import com.creation.platform.entity.UserInteraction;

public interface UserInteractionService extends IService<UserInteraction> {

    /**
     * 切换互动状态（状态翻转）
     * 如果已存在则删除（取消），如果不存在则新增（点赞/收藏）
     *
     * @param userId 当前用户ID
     * @param dto    互动信息 DTO
     * @return true-已点赞/已收藏，false-未点赞/未收藏
     */
    boolean toggleInteraction(Long userId, InteractionDTO dto);

    /**
     * 检查用户是否已点赞/收藏
     */
    boolean hasInteracted(Long userId, Long artworkId, Integer interactionType);
}
