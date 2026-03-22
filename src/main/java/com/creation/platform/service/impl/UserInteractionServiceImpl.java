package com.creation.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creation.platform.dto.InteractionDTO;
import com.creation.platform.entity.UserInteraction;
import com.creation.platform.mapper.UserInteractionMapper;
import com.creation.platform.service.UserInteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserInteractionServiceImpl extends ServiceImpl<UserInteractionMapper, UserInteraction> implements UserInteractionService {

    @Autowired
    private UserInteractionMapper userInteractionMapper;

    @Override
    public boolean toggleInteraction(Long userId, InteractionDTO dto) {
        userInteractionMapper.toggleOrInsert(userId, dto.getArtworkId(), dto.getInteractionType());
        Integer isDeleted = userInteractionMapper.getInteractionStatus(userId, dto.getArtworkId(), dto.getInteractionType());
        return isDeleted != null && isDeleted == 0;
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
