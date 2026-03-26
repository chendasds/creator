package com.creation.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.creation.platform.entity.PrivateMessage;
import com.creation.platform.vo.ConversationVO;
import com.creation.platform.vo.PrivateMessageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface PrivateMessageMapper extends BaseMapper<PrivateMessage> {

    /**
     * 查询两个人之间的聊天记录（按时间正序）
     */
    @Select("SELECT m.id, m.sender_id AS senderId, m.receiver_id AS receiverId, " +
            "u.nickname AS senderName, u.avatar_url AS senderAvatar, " +
            "m.content, m.is_read AS isRead, m.create_time AS createTime " +
            "FROM private_message m " +
            "LEFT JOIN user u ON m.sender_id = u.id " +
            "WHERE ((m.sender_id = #{userId1} AND m.receiver_id = #{userId2}) " +
            "OR (m.sender_id = #{userId2} AND m.receiver_id = #{userId1})) " +
            "AND m.is_deleted = 0 " +
            "ORDER BY m.create_time ASC")
    List<PrivateMessageVO> selectChatHistory(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    /**
     * 将发送给某个用户的未读消息标记为已读
     */
    @Update("UPDATE private_message SET is_read = 1 WHERE sender_id = #{targetUserId} AND receiver_id = #{currentUserId} AND is_read = 0 AND is_deleted = 0")
    void markAsRead(@Param("currentUserId") Long currentUserId, @Param("targetUserId") Long targetUserId);

    /**
     * 统计用户未读消息总数
     */
    @Select("SELECT COUNT(*) FROM private_message WHERE receiver_id = #{userId} AND is_read = 0 AND is_deleted = 0")
    Integer countTotalUnread(@Param("userId") Long userId);
}
