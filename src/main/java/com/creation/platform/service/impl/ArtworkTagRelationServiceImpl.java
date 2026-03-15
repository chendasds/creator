package com.creation.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creation.platform.entity.ArtworkTagRelation;
import com.creation.platform.mapper.ArtworkTagRelationMapper;
import com.creation.platform.service.ArtworkTagRelationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ArtworkTagRelationServiceImpl extends ServiceImpl<ArtworkTagRelationMapper, ArtworkTagRelation> implements ArtworkTagRelationService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setTags(Long artworkId, List<Long> tagIds) {
        // 删除原有关联
        LambdaQueryWrapper<ArtworkTagRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArtworkTagRelation::getArtworkId, artworkId);
        this.remove(wrapper);

        // 添加新关联
        if (tagIds != null && !tagIds.isEmpty()) {
            for (Long tagId : tagIds) {
                ArtworkTagRelation relation = new ArtworkTagRelation();
                relation.setArtworkId(artworkId);
                relation.setTagId(tagId);
                this.save(relation);
            }
        }
        return true;
    }

    @Override
    public List<Long> getTagIdsByArtworkId(Long artworkId) {
        LambdaQueryWrapper<ArtworkTagRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArtworkTagRelation::getArtworkId, artworkId);
        List<ArtworkTagRelation> relations = this.list(wrapper);
        return relations.stream().map(ArtworkTagRelation::getTagId).toList();
    }
}
