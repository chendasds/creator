package com.creation.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.creation.platform.entity.UserFollow;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户关注关系 Mapper
 */
@Mapper
public interface UserFollowMapper extends BaseMapper<UserFollow> {

    /**
     * 切换关注状态（支持高并发防重复）
     * 使用 MySQL ON DUPLICATE KEY UPDATE 实现状态翻转：
     * - 记录不存在则插入（is_deleted=0 表示已关注）
     * - 记录存在则翻转 is_deleted（0变1取消关注，1变0恢复关注）
     */
    @Insert("INSERT INTO user_follow (follower_id, followee_id, create_time, is_deleted) " +
            "VALUES (#{followerId}, #{followeeId}, NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE is_deleted = 1 - is_deleted, create_time = NOW()")
    int toggleFollow(@Param("followerId") Long followerId, @Param("followeeId") Long followeeId);

    /**
     * 查询关注状态
     * @return is_deleted 字段值（0=已关注，1=未关注，null=从未关注）
     */
    @Select("SELECT is_deleted FROM user_follow WHERE follower_id = #{followerId} AND followee_id = #{followeeId}")
    Integer checkFollowStatus(@Param("followerId") Long followerId, @Param("followeeId") Long followeeId);
}
