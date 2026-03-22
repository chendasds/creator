package com.creation.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.creation.platform.entity.UserInteraction;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserInteractionMapper extends BaseMapper<UserInteraction> {

    /**
     * 切换点赞/收藏状态
     * 使用 MySQL ON DUPLICATE KEY UPDATE 实现状态翻转：
     * - 记录不存在则插入（is_deleted=0 表示已点赞/收藏）
     * - 记录存在则翻转 is_deleted（0变1取消，1变0恢复）
     */
    @Insert("INSERT INTO user_interaction (user_id, artwork_id, interaction_type, is_deleted, create_time) " +
            "VALUES (#{userId}, #{artworkId}, #{type}, 0, NOW()) " +
            "ON DUPLICATE KEY UPDATE is_deleted = 1 - is_deleted, create_time = NOW()")
    int toggleOrInsert(@Param("userId") Long userId, @Param("artworkId") Long artworkId, @Param("type") Integer type);

    /**
     * 查询当前互动状态
     *
     * @return is_deleted 的值：0-已点赞/已收藏，1-未点赞/未收藏
     */
    @Select("SELECT is_deleted FROM user_interaction WHERE user_id = #{userId} AND artwork_id = #{artworkId} AND interaction_type = #{type}")
    Integer getInteractionStatus(@Param("userId") Long userId, @Param("artworkId") Long artworkId, @Param("type") Integer type);
}
