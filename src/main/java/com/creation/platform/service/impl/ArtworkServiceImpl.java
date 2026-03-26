package com.creation.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creation.platform.dto.ArtworkPublishDTO;
import com.creation.platform.entity.Artwork;
import com.creation.platform.entity.Tag;
import com.creation.platform.entity.UserInteraction;
import com.creation.platform.mapper.ArtworkMapper;
import com.creation.platform.mapper.UserInteractionMapper;
import com.creation.platform.mapper.UserMapper;
import com.creation.platform.service.ArtworkService;
import com.creation.platform.service.ArtworkTagRelationService;
import com.creation.platform.service.TagService;
import com.creation.platform.vo.ArtworkVO;
import com.creation.platform.vo.DashboardVO;
import com.creation.platform.vo.InteractionTrendVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ArtworkServiceImpl extends ServiceImpl<ArtworkMapper, Artwork> implements ArtworkService {

    @Autowired
    private ArtworkMapper artworkMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserInteractionMapper userInteractionMapper;

    @Autowired
    private ArtworkTagRelationService artworkTagRelationService;

    @Autowired
    private TagService tagService;

    @Override
    public Page<ArtworkVO> getAdminArtworkPage(Integer current, Integer size, String title, Long categoryId,
            Integer status) {
        Page<ArtworkVO> page = new Page<>(current, size);
        return artworkMapper.selectArtworkPage(page, title, categoryId, status);
    }

    @Override
    public Page<ArtworkVO> getFeedPage(Integer current, Integer size, Long tagId, Long categoryId, Long userId,
            Long followerId, String sortType, String keyword) {
        Page<ArtworkVO> page = new Page<>(current, size);
        Page<ArtworkVO> pageResult = artworkMapper.selectFeedPage(page, tagId, categoryId, userId, followerId, sortType,
                keyword);

        // 为信息流中的每篇文章组装标签数据
        if (pageResult.getRecords() != null) {
            for (ArtworkVO vo : pageResult.getRecords()) {
                List<Long> tIds = artworkTagRelationService.getTagIdsByArtworkId(vo.getId());
                if (tIds != null && !tIds.isEmpty()) {
                    List<Tag> tags = tagService.listByIds(tIds);
                    vo.setTags(tags);
                }
            }
        }
        return pageResult;
    }

    @Override
    public ArtworkVO getArtworkDetail(Long id, Long userId) {
        ArtworkVO artworkVO = artworkMapper.selectDetailById(id);
        if (artworkVO == null) {
            return null;
        }

        artworkVO.setViewCount(artworkVO.getViewCount() + 1);
        Artwork artwork = new Artwork();
        artwork.setId(id);
        artwork.setViewCount(artworkVO.getViewCount());
        artworkMapper.updateById(artwork);

        if (userId != null) {
            LambdaQueryWrapper<UserInteraction> likedWrapper = new LambdaQueryWrapper<>();
            likedWrapper.eq(UserInteraction::getUserId, userId)
                    .eq(UserInteraction::getArtworkId, id)
                    .eq(UserInteraction::getInteractionType, 1);
            artworkVO.setIsLiked(userInteractionMapper.selectCount(likedWrapper) > 0);

            LambdaQueryWrapper<UserInteraction> collectedWrapper = new LambdaQueryWrapper<>();
            collectedWrapper.eq(UserInteraction::getUserId, userId)
                    .eq(UserInteraction::getArtworkId, id)
                    .eq(UserInteraction::getInteractionType, 2);
            artworkVO.setIsCollected(userInteractionMapper.selectCount(collectedWrapper) > 0);
        }

        LambdaQueryWrapper<UserInteraction> likeCountWrapper = new LambdaQueryWrapper<>();
        likeCountWrapper.eq(UserInteraction::getArtworkId, id)
                .eq(UserInteraction::getInteractionType, 1);
        artworkVO.setLikeCount(userInteractionMapper.selectCount(likeCountWrapper).intValue());

        LambdaQueryWrapper<UserInteraction> collectCountWrapper = new LambdaQueryWrapper<>();
        collectCountWrapper.eq(UserInteraction::getArtworkId, id)
                .eq(UserInteraction::getInteractionType, 2);
        artworkVO.setCollectCount(userInteractionMapper.selectCount(collectCountWrapper).intValue());

        // 查询并注入标签列表
        List<Long> tagIds = artworkTagRelationService.getTagIdsByArtworkId(id);
        if (tagIds != null && !tagIds.isEmpty()) {
            List<Tag> tags = tagService.listByIds(tagIds);
            artworkVO.setTags(tags);
        }

        return artworkVO;
    }

    @Override
    public DashboardVO getDashboardStats() {
        DashboardVO vo = new DashboardVO();

        // 1. 用户总数
        Long totalUsers = userMapper.selectCount(null);
        vo.setTotalUsers(totalUsers);

        // 2. 作品总数
        Long totalArtworks = artworkMapper.selectCount(null);
        vo.setTotalArtworks(totalArtworks);

        // 3. 今日新增作品
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        QueryWrapper<Artwork> todayWrapper = new QueryWrapper<>();
        todayWrapper.ge("create_time", todayStart);
        Long todayNewArtworks = artworkMapper.selectCount(todayWrapper);
        vo.setTodayNewArtworks(todayNewArtworks);

        // 4. 待审核作品数
        QueryWrapper<Artwork> pendingWrapper = new QueryWrapper<>();
        pendingWrapper.eq("status", 0);
        Long pendingAuditCount = artworkMapper.selectCount(pendingWrapper);
        vo.setPendingAuditCount(pendingAuditCount);

        // 5. 分类占比数据
        List<Map<String, Object>> categoryRatio = artworkMapper.selectCategoryRatio();
        vo.setCategoryRatio(categoryRatio);

        // 6. 七日趋势数据
        List<Map<String, Object>> trendList = artworkMapper.selectTrendData();
        List<String> dates = new ArrayList<>();
        List<Long> counts = new ArrayList<>();
        for (Map<String, Object> item : trendList) {
            // 处理日期
            Object dateObj = item.get("dates");
            if (dateObj != null) {
                dates.add(dateObj.toString());
            }
            // 处理数量
            Object countObj = item.get("counts");
            if (countObj != null) {
                if (countObj instanceof Number) {
                    counts.add(((Number) countObj).longValue());
                } else {
                    counts.add(Long.parseLong(countObj.toString()));
                }
            }
        }
        Map<String, Object> trendData = new LinkedHashMap<>();
        trendData.put("dates", dates);
        trendData.put("counts", counts);
        vo.setTrendData(trendData);

        return vo;
    }

    @Override
    public Long publishArtwork(Long userId, ArtworkPublishDTO dto) {
        Artwork artwork = new Artwork();
        BeanUtils.copyProperties(dto, artwork);
        artwork.setUserId(userId);
        artwork.setViewCount(0);
        if (dto.getContent() != null) {
            artwork.setWordCount(dto.getContent().length());
        }
        artworkMapper.insert(artwork);

        // 保存作品与标签的关联关系
        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            artworkTagRelationService.setTags(artwork.getId(), dto.getTagIds());
        }

        return artwork.getId();
    }

    @Override
    public DashboardVO getCreatorDashboardStats(Long userId) {
        return baseMapper.getCreatorDashboardStats(userId);
    }

    @Override
    public List<InteractionTrendVO> getRecentInteractionTrend(Long userId) {
        List<InteractionTrendVO> interactions = baseMapper.getRecentInteractionTrend(userId);
        List<InteractionTrendVO> fans = baseMapper.getRecentFanTrend(userId);
        List<InteractionTrendVO> artworks = baseMapper.getRecentArtworkTrend(userId);

        Map<String, InteractionTrendVO> map = new HashMap<>();

        interactions.forEach(item -> map.put(item.getDate(), item));

        fans.forEach(item -> {
            InteractionTrendVO vo = map.getOrDefault(item.getDate(), new InteractionTrendVO());
            vo.setDate(item.getDate());
            vo.setFanCount(item.getFanCount());
            map.put(item.getDate(), vo);
        });

        artworks.forEach(item -> {
            InteractionTrendVO vo = map.getOrDefault(item.getDate(), new InteractionTrendVO());
            vo.setDate(item.getDate());
            vo.setArtworkCount(item.getArtworkCount());
            map.put(item.getDate(), vo);
        });

        List<InteractionTrendVO> result = new ArrayList<>(map.values());
        result.sort(Comparator.comparing(InteractionTrendVO::getDate));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateArtwork(Long id, ArtworkPublishDTO dto) {
        // 1. 更新作品基本信息
        Artwork artwork = new Artwork();
        BeanUtils.copyProperties(dto, artwork);
        artwork.setId(id);
        if (dto.getContent() != null) {
            artwork.setWordCount(dto.getContent().length());
        }
        this.updateById(artwork);

        // 2. 更新标签关联（先全删，再全加）
        if (dto.getTagIds() != null) {
            artworkTagRelationService.setTags(id, dto.getTagIds());
        }
    }
}
