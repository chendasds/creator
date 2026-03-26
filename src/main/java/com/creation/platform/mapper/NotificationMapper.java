package com.creation.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.creation.platform.entity.Notification;
import com.creation.platform.vo.NotificationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
    // 联表查询通知列表，带上发送者的头像昵称和目标文章的标题
    @Select("SELECT n.id, n.sender_id AS senderId, u.nickname AS senderName, u.avatar_url AS senderAvatar, " +
            "n.type, n.target_id AS targetId, a.title AS targetTitle, n.content, n.is_read AS isRead, n.create_time AS createTime " +
            "FROM notification n " +
            "LEFT JOIN user u ON n.sender_id = u.id " +
            "LEFT JOIN artwork a ON n.target_id = a.id AND n.type IN (1, 2, 3) " +
            "WHERE n.receiver_id = #{userId} AND n.is_deleted = 0 " +
            "ORDER BY n.is_read ASC, n.create_time DESC")
    List<NotificationVO> selectNotificationList(@Param("userId") Long userId);

    // 查询未读数量
    @Select("SELECT COUNT(*) FROM notification WHERE receiver_id = #{userId} AND is_read = 0 AND is_deleted = 0")
    Integer countUnread(@Param("userId") Long userId);
}
