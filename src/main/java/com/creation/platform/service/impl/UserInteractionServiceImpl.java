package com.creation.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creation.platform.entity.UserInteraction;
import com.creation.platform.mapper.UserInteractionMapper;
import com.creation.platform.service.UserInteractionService;
import org.springframework.stereotype.Service;

@Service
public class UserInteractionServiceImpl extends ServiceImpl<UserInteractionMapper, UserInteraction> implements UserInteractionService {

    @Override
    public boolean interact(Long userId, Long artworkId, Integer interactionType) {
        // 检查是否已存在
        if (hasInteracted(userId, artworkId, interactionType)) {
            return true; // 已存在，视为成功
        }
        UserInteraction interaction = new UserInteraction();
        interaction.setUserId(userId);
        interaction.setArtworkId(artworkId);
        interaction.setInteractionType(interactionType);
        return this.save(interaction);
    }

    @Override
    public boolean cancelInteraction(Long userId, Long artworkId, Integer interactionType) {
        LambdaQueryWrapper<UserInteraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInteraction::getUserId, userId)
                .eq(UserInteraction::getArtworkId, artworkId)
                .eq(UserInteraction::getInteractionType, interactionType);
        return this.remove(wrapper);
    }

    @Override
    public boolean hasInteracted(Long userId, Long artworkId, Integer interactionType) {
        LambdaQueryWrapper<UserInteraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInteraction::getUserId, userId)
                .eq(UserInteraction::getArtworkId, artworkId)
                .eq(UserInteraction::getInteractionType, interactionType);
        return this.count(wrapper) > 0;
    }
}
