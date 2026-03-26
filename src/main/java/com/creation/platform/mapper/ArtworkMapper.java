package com.creation.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creation.platform.entity.Artwork;
import com.creation.platform.vo.ArtworkVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ArtworkMapper extends BaseMapper<Artwork> {

        /**
         * 后台管理作品列表联表分页查询
         */
        @Select("<script>" +
                        "SELECT a.id, a.user_id AS userId, a.category_id AS categoryId, a.title, a.cover_url, a.description, a.view_count, a.word_count, a.status, a.create_time, "
                        +
                        "u.nickname AS authorName, " +
                        "c.name AS categoryName " +
                        "FROM artwork a " +
                        "LEFT JOIN user u ON a.user_id = u.id " +
                        "LEFT JOIN category c ON a.category_id = c.id " +
                        "WHERE a.is_deleted = 0 " +
                        "<if test='title != null and title != \"\"'> AND a.title LIKE CONCAT('%', #{title}, '%') </if>"
                        +
                        "<if test='categoryId != null'> AND a.category_id = #{categoryId} </if>" +
                        "<if test='status != null'> AND a.status = #{status} </if>" +
                        "ORDER BY a.create_time DESC" +
                        "</script>")
        Page<ArtworkVO> selectArtworkPage(Page<ArtworkVO> page,
                        @Param("title") String title,
                        @Param("categoryId") Long categoryId,
                        @Param("status") Integer status);

        /**
         * 查询分类占比数据
         */
        List<Map<String, Object>> selectCategoryRatio();

        /**
         * 查询七日趋势数据
         */
        List<Map<String, Object>> selectTrendData();

        /**
         * 公开作品流分页查询（status=1 且 is_deleted=0，按时间倒序，支持标签和分类过滤）
         * 使用 EXISTS 子查询过滤标签，避免连表导致的分页总数错误
         */
        @Select("<script>" +
                "SELECT a.id, a.user_id AS userId, a.category_id AS categoryId, a.title, a.cover_url, a.description, a.view_count, a.word_count, a.status, a.create_time, "
                + "u.nickname AS authorName, "
                + "c.name AS categoryName, "
                + "(SELECT COUNT(*) FROM user_interaction ui WHERE ui.artwork_id = a.id AND ui.interaction_type = 1 AND ui.is_deleted = 0) AS likeCount, "
                + "(SELECT COUNT(*) FROM user_interaction ui WHERE ui.artwork_id = a.id AND ui.interaction_type = 2 AND ui.is_deleted = 0) AS collectCount, "
                + "(SELECT COUNT(*) FROM comment co WHERE co.artwork_id = a.id AND co.is_deleted = 0) AS commentCount "
                + "FROM artwork a "
                + "LEFT JOIN user u ON a.user_id = u.id "
                + "LEFT JOIN category c ON a.category_id = c.id "
                + "WHERE a.status = 1 AND a.is_deleted = 0 "
                + "<if test='followerId != null'> AND a.user_id IN (SELECT followee_id FROM user_follow WHERE follower_id = #{followerId} AND is_deleted = 0) </if>"
                + "<if test='userId != null'> AND a.user_id = #{userId} </if>"
                + "<if test='categoryId != null'> AND a.category_id = #{categoryId} </if>"
                + "<if test='tagId != null'> "
                + "AND EXISTS (SELECT 1 FROM artwork_tag_relation atr WHERE atr.artwork_id = a.id AND atr.tag_id = #{tagId} AND atr.is_deleted = 0) "
                + "</if>"
                + "<if test='keyword != null and keyword != \"\"'>"
                + " AND (a.title LIKE CONCAT('%', #{keyword}, '%') OR a.description LIKE CONCAT('%', #{keyword}, '%'))"
                + "</if>"
                + "<choose>"
                + "<when test='sortType == \"recommend\"'>"
                + "ORDER BY (a.view_count * 1 + IFNULL(likeCount, 0) * 5 + IFNULL(commentCount, 0) * 10) DESC, a.create_time DESC"
                + "</when>"
                + "<otherwise>"
                + "ORDER BY a.create_time DESC"
                + "</otherwise>"
                + "</choose>"
                + "</script>")
        Page<ArtworkVO> selectFeedPage(Page<ArtworkVO> page, @Param("tagId") Long tagId, @Param("categoryId") Long categoryId, @Param("userId") Long userId, @Param("followerId") Long followerId, @Param("sortType") String sortType, @Param("keyword") String keyword);

        /**
         * 根据ID查询作品详情（联表查询作者名和分类名，包含正文内容）
         */
        @Select("SELECT a.id, a.user_id AS userId, a.category_id AS categoryId, a.title, a.cover_url, a.description, a.content, a.view_count, a.word_count, a.status, a.create_time, "
                + "u.nickname AS authorName, "
                + "c.name AS categoryName "
                + "FROM artwork a "
                + "LEFT JOIN user u ON a.user_id = u.id "
                + "LEFT JOIN category c ON a.category_id = c.id "
                + "WHERE a.id = #{id} AND a.is_deleted = 0")
        ArtworkVO selectDetailById(@Param("id") Long id);

        /**
         * 统计指定用户已发布作品的总浏览量
         */
        @Select("SELECT COALESCE(SUM(view_count), 0) FROM artwork WHERE user_id = #{userId} AND status = 1 AND is_deleted = 0")
        Integer selectTotalViewsByUserId(@Param("userId") Long userId);

        /**
         * 查询指定用户收藏的作品列表（支持标签筛选，含点赞数和评论数）
         */
        @Select("<script>" +
                "SELECT a.id, a.user_id AS userId, a.category_id AS categoryId, a.title, a.cover_url, a.description, a.view_count, a.word_count, a.status, a.create_time, " +
                "u.nickname AS authorName, c.name AS categoryName, " +
                "(SELECT COUNT(*) FROM user_interaction ui2 WHERE ui2.artwork_id = a.id AND ui2.interaction_type = 1 AND ui2.is_deleted = 0) AS likeCount, " +
                "(SELECT COUNT(*) FROM comment co WHERE co.artwork_id = a.id AND co.is_deleted = 0) AS commentCount " +
                "FROM artwork a " +
                "INNER JOIN user_interaction ui ON a.id = ui.artwork_id " +
                "LEFT JOIN user u ON a.user_id = u.id " +
                "LEFT JOIN category c ON a.category_id = c.id " +
                "WHERE ui.user_id = #{userId} AND ui.interaction_type = 2 AND a.is_deleted = 0 AND ui.is_deleted = 0 " +
                "<if test='tagId != null'> AND EXISTS (SELECT 1 FROM artwork_tag_relation atr WHERE atr.artwork_id = a.id AND atr.tag_id = #{tagId} AND atr.is_deleted = 0) </if> " +
                "ORDER BY ui.create_time DESC" +
                "</script>")
        List<com.creation.platform.vo.ArtworkVO> selectCollectedArtworks(@Param("userId") Long userId, @Param("tagId") Long tagId);

        /**
         * 查询指定用户点赞的作品列表（支持标签筛选，含点赞数和评论数）
         */
        @Select("<script>" +
                "SELECT a.id, a.user_id AS userId, a.category_id AS categoryId, a.title, a.cover_url, a.description, a.view_count, a.word_count, a.status, a.create_time, " +
                "u.nickname AS authorName, c.name AS categoryName, " +
                "(SELECT COUNT(*) FROM user_interaction ui2 WHERE ui2.artwork_id = a.id AND ui2.interaction_type = 1 AND ui2.is_deleted = 0) AS likeCount, " +
                "(SELECT COUNT(*) FROM comment co WHERE co.artwork_id = a.id AND co.is_deleted = 0) AS commentCount " +
                "FROM artwork a " +
                "INNER JOIN user_interaction ui ON a.id = ui.artwork_id " +
                "LEFT JOIN user u ON a.user_id = u.id " +
                "LEFT JOIN category c ON a.category_id = c.id " +
                "WHERE ui.user_id = #{userId} AND ui.interaction_type = 1 AND a.is_deleted = 0 AND ui.is_deleted = 0 " +
                "<if test='tagId != null'> AND EXISTS (SELECT 1 FROM artwork_tag_relation atr WHERE atr.artwork_id = a.id AND atr.tag_id = #{tagId} AND atr.is_deleted = 0) </if> " +
                "ORDER BY ui.create_time DESC" +
                "</script>")
        List<com.creation.platform.vo.ArtworkVO> selectLikedArtworks(@Param("userId") Long userId, @Param("tagId") Long tagId);

        /**
         * 获取创作者个人数据统计（作品数、总浏览量、总点赞数、粉丝数）
         */
        @Select("SELECT " +
                "  (SELECT COUNT(*) FROM artwork WHERE user_id = #{userId} AND is_deleted = 0) AS artworkCount, " +
                "  (SELECT IFNULL(SUM(view_count), 0) FROM artwork WHERE user_id = #{userId} AND is_deleted = 0) AS totalViews, " +
                "  (SELECT COUNT(*) FROM user_interaction ui INNER JOIN artwork a ON ui.artwork_id = a.id WHERE a.user_id = #{userId} AND ui.interaction_type = 1 AND ui.is_deleted = 0 AND a.is_deleted = 0) AS totalLikes, " +
                "  (SELECT COUNT(*) FROM user_follow WHERE followee_id = #{userId} AND is_deleted = 0) AS totalFollowers " +
                "FROM DUAL")
        com.creation.platform.vo.DashboardVO getCreatorDashboardStats(@Param("userId") Long userId);

        /**
         * 获取创作者近7天互动趋势（每日点赞数和收藏数）
         */
        @Select("SELECT DATE_FORMAT(ui.create_time, '%Y-%m-%d') AS date, " +
                "CAST(SUM(CASE WHEN ui.interaction_type = 1 THEN 1 ELSE 0 END) AS SIGNED) AS likeCount, " +
                "CAST(SUM(CASE WHEN ui.interaction_type = 2 THEN 1 ELSE 0 END) AS SIGNED) AS collectCount " +
                "FROM user_interaction ui " +
                "JOIN artwork a ON ui.artwork_id = a.id " +
                "WHERE a.user_id = #{userId} AND ui.create_time >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) AND ui.is_deleted = 0 " +
                "GROUP BY DATE_FORMAT(ui.create_time, '%Y-%m-%d') " +
                "ORDER BY date ASC")
        List<com.creation.platform.vo.InteractionTrendVO> getRecentInteractionTrend(@Param("userId") Long userId);

        /**
         * 获取创作者近7天粉丝趋势（每日新增粉丝数）
         */
        @Select("SELECT DATE_FORMAT(create_time, '%Y-%m-%d') AS date, COUNT(*) AS fanCount " +
                "FROM user_follow " +
                "WHERE followee_id = #{userId} AND create_time >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) AND is_deleted = 0 " +
                "GROUP BY DATE_FORMAT(create_time, '%Y-%m-%d')")
        List<com.creation.platform.vo.InteractionTrendVO> getRecentFanTrend(@Param("userId") Long userId);

        /**
         * 获取创作者近7天作品趋势（每日新增作品数）
         */
        @Select("SELECT DATE_FORMAT(create_time, '%Y-%m-%d') AS date, COUNT(*) AS artworkCount " +
                "FROM artwork " +
                "WHERE user_id = #{userId} AND status = 1 AND create_time >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) AND is_deleted = 0 " +
                "GROUP BY DATE_FORMAT(create_time, '%Y-%m-%d')")
        List<com.creation.platform.vo.InteractionTrendVO> getRecentArtworkTrend(@Param("userId") Long userId);
}
