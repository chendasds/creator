package com.creation.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.creation.platform.entity.ArtworkTagRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Insert;

@Mapper
public interface ArtworkTagRelationMapper extends BaseMapper<ArtworkTagRelation> {

    /**
     * 插入或恢复标签关联
     * 当唯一索引冲突时（之前被逻辑删除），将 is_deleted 置为 0 恢复记录
     */
    @Insert("INSERT INTO artwork_tag_relation (artwork_id, tag_id, create_time, is_deleted) " +
            "VALUES (#{artworkId}, #{tagId}, NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE is_deleted = 0")
    int insertOrRecover(@Param("artworkId") Long artworkId, @Param("tagId") Long tagId);
}
