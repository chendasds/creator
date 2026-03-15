package com.creation.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.creation.platform.entity.ArtworkTagRelation;

import java.util.List;

public interface ArtworkTagRelationService extends IService<ArtworkTagRelation> {

    /**
     * 为作品设置标签
     */
    boolean setTags(Long artworkId, List<Long> tagIds);

    /**
     * 获取作品的所有标签ID
     */
    List<Long> getTagIdsByArtworkId(Long artworkId);
}
