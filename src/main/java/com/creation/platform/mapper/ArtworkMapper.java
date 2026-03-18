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
            "SELECT a.id, a.title, a.cover_url, a.description, a.view_count, a.word_count, a.status, a.create_time, " +
            "u.nickname AS authorName, " +
            "c.name AS categoryName " +
            "FROM artwork a " +
            "LEFT JOIN user u ON a.user_id = u.id " +
            "LEFT JOIN category c ON a.category_id = c.id " +
            "WHERE a.is_deleted = 0 " +
            "<if test='title != null and title != \"\"'> AND a.title LIKE CONCAT('%', #{title}, '%') </if>" +
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
}
