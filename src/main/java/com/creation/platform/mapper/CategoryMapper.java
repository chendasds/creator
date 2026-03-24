package com.creation.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.creation.platform.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 穿透查询，无视逻辑删除
     */
    @Select("SELECT * FROM category WHERE name = #{name}")
    Category selectByNameIgnoreLogicDelete(@Param("name") String name);

    /**
     * 彻底绕过逻辑删除插件，强制更新 is_deleted 为 0
     */
    @Update("UPDATE category SET is_deleted = 0, description = #{description}, sort_order = #{sortOrder}, update_time = #{updateTime} WHERE id = #{id}")
    int resurrectCategory(Category category);

    /**
     * 获取热门分类（按关联的已发布文章数倒序，取前8个）
     */
    @Select("SELECT c.* FROM category c " +
            "LEFT JOIN artwork a ON c.id = a.category_id AND a.is_deleted = 0 AND a.status = 1 " +
            "WHERE c.is_deleted = 0 " +
            "GROUP BY c.id " +
            "ORDER BY COUNT(a.id) DESC " +
            "LIMIT 8")
    List<Category> selectHotCategories();
}
