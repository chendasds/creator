package com.creation.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creation.platform.dto.InteractionDTO;
import com.creation.platform.entity.Result;
import com.creation.platform.entity.Tag;
import com.creation.platform.entity.User;
import com.creation.platform.entity.UserInteraction;
import com.creation.platform.mapper.ArtworkMapper;
import com.creation.platform.mapper.UserInteractionMapper;
import com.creation.platform.mapper.UserMapper;
import com.creation.platform.service.ArtworkTagRelationService;
import com.creation.platform.service.NotificationService;
import com.creation.platform.service.TagService;
import com.creation.platform.service.UserInteractionService;
import com.creation.platform.vo.ArtworkVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserInteractionServiceImpl extends ServiceImpl<UserInteractionMapper, UserInteraction> implements UserInteractionService {

    @Autowired
    private UserInteractionMapper userInteractionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ArtworkMapper artworkMapper;

    @Autowired
    private ArtworkTagRelationService artworkTagRelationService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TagService tagService;

    @Override
    public boolean toggleInteraction(Long userId, InteractionDTO dto) {
        userInteractionMapper.toggleOrInsert(userId, dto.getArtworkId(), dto.getInteractionType());
        Integer isDeleted = userInteractionMapper.getInteractionStatus(userId, dto.getArtworkId(), dto.getInteractionType());
        boolean isAdded = (isDeleted != null && isDeleted == 0);

        // --- 埋点：触发点赞/收藏通知 (type: 1=点赞 2=收藏) ---
        if (isAdded) {
            com.creation.platform.entity.Artwork artwork = artworkMapper.selectById(dto.getArtworkId());
            if (artwork != null) {
                notificationService.sendNotification(artwork.getUserId(), userId, dto.getInteractionType(), artwork.getId(), null);
            }
        }

        return isAdded;
    }

    @Override
    public boolean hasInteracted(Long userId, Long artworkId, Integer interactionType) {
        LambdaQueryWrapper<UserInteraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInteraction::getUserId, userId)
                .eq(UserInteraction::getArtworkId, artworkId)
                .eq(UserInteraction::getInteractionType, interactionType);
        return this.count(wrapper) > 0;
    }

    @Override
    public Result<List<ArtworkVO>> getCollections(Long targetUserId, Long currentUserId, Long tagId) {
        // 1. 获取目标用户信息，检查隐私开关
        User targetUser = userMapper.selectById(targetUserId);
        if (targetUser == null) return Result.error(404, "用户不存在");

        // 2. 隐私检查：如果不是本人，且用户开启了隐藏收藏 (1)
        if (!targetUserId.equals(currentUserId) && Integer.valueOf(1).equals(targetUser.getHideCollections())) {
            return Result.error(403, "该用户已隐藏收藏列表");
        }

        // 3. 查询收藏的作品（支持标签筛选）
        List<ArtworkVO> list = artworkMapper.selectCollectedArtworks(targetUserId, tagId);

        // 4. 组装标签信息
        attachTagsToArtworks(list);

        return Result.success(list);
    }

    @Override
    public Result<List<ArtworkVO>> getLikes(Long targetUserId, Long tagId) {
        // 检查用户是否存在
        User targetUser = userMapper.selectById(targetUserId);
        if (targetUser == null) return Result.error(404, "用户不存在");

        // 查询点赞的作品（支持标签筛选）
        List<ArtworkVO> list = artworkMapper.selectLikedArtworks(targetUserId, tagId);

        // 组装标签信息
        attachTagsToArtworks(list);

        return Result.success(list);
    }

    /**
     * 为作品列表组装标签信息
     */
    private void attachTagsToArtworks(List<ArtworkVO> artworks) {
        if (artworks != null && !artworks.isEmpty()) {
            for (ArtworkVO vo : artworks) {
                List<Long> tIds = artworkTagRelationService.getTagIdsByArtworkId(vo.getId());
                if (tIds != null && !tIds.isEmpty()) {
                    List<Tag> tags = tagService.listByIds(tIds);
                    vo.setTags(tags);
                }
            }
        }
    }
}
