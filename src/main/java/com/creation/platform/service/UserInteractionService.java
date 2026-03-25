package com.creation.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.creation.platform.dto.InteractionDTO;
import com.creation.platform.entity.Result;
import com.creation.platform.entity.UserInteraction;
import com.creation.platform.vo.ArtworkVO;

import java.util.List;

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

    /**
     * 获取指定用户的收藏列表（带隐私保护检查，支持标签筛选）
     *
     * @param targetUserId   被查询的用户ID
     * @param currentUserId  当前登录用户ID
     * @param tagId          标签ID筛选（可选）
     * @return 收藏作品列表；非本人且对方隐藏收藏时返回错误 Result
     */
    Result<List<ArtworkVO>> getCollections(Long targetUserId, Long currentUserId, Long tagId);

    /**
     * 获取指定用户点赞的作品列表（默认公开，无需隐私校验，支持标签筛选）
     *
     * @param targetUserId 被查询的用户ID
     * @param tagId        标签ID筛选（可选）
     * @return 点赞作品列表；用户不存在时返回错误 Result
     */
    Result<List<ArtworkVO>> getLikes(Long targetUserId, Long tagId);
}
