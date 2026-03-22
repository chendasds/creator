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
                        "SELECT a.id, a.title, a.cover_url, a.description, a.view_count, a.word_count, a.status, a.create_time, "
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
         * 公开作品流分页查询（status=1 且 is_deleted=0，按时间倒序）
         */
        @Select("SELECT a.id, a.title, a.cover_url, a.description, a.view_count, a.word_count, a.status, a.create_time, "
                + "u.nickname AS authorName, "
                + "c.name AS categoryName, "
                + "(SELECT COUNT(*) FROM user_interaction ui WHERE ui.artwork_id = a.id AND ui.interaction_type = 1 AND ui.is_deleted = 0) AS likeCount, "
                + "(SELECT COUNT(*) FROM user_interaction ui WHERE ui.artwork_id = a.id AND ui.interaction_type = 2 AND ui.is_deleted = 0) AS collectCount, "
                + "(SELECT COUNT(*) FROM comment co WHERE co.artwork_id = a.id AND co.is_deleted = 0) AS commentCount "
                + "FROM artwork a "
                + "LEFT JOIN user u ON a.user_id = u.id "
                + "LEFT JOIN category c ON a.category_id = c.id "
                + "WHERE a.status = 1 AND a.is_deleted = 0 "
                + "ORDER BY a.create_time DESC")
        Page<ArtworkVO> selectFeedPage(Page<ArtworkVO> page);

        /**
         * 根据ID查询作品详情（联表查询作者名和分类名，包含正文内容）
         */
        @Select("SELECT a.id, a.title, a.cover_url, a.description, a.content, a.view_count, a.word_count, a.status, a.create_time, "
                + "u.nickname AS authorName, "
                + "c.name AS categoryName "
                + "FROM artwork a "
                + "LEFT JOIN user u ON a.user_id = u.id "
                + "LEFT JOIN category c ON a.category_id = c.id "
                + "WHERE a.id = #{id} AND a.is_deleted = 0")
        ArtworkVO selectDetailById(@Param("id") Long id);
}
