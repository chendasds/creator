package com.creation.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.creation.platform.entity.Comment;
import com.creation.platform.vo.CommentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 查询评论列表并关联用户信息（昵称、头像）
     */
    List<CommentVO> selectCommentListWithUser(@Param("artworkId") Long artworkId);

    /**
     * 安全逻辑删除评论（鉴权直接写入SQL）
     * 只有评论存在、未被删除、且属于当前用户时才会更新成功
     */
    @Update("UPDATE comment SET is_deleted = 1 WHERE id = #{id} AND user_id = #{userId} AND is_deleted = 0")
    int deleteCommentSafely(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 根据父评论ID查询所有子评论ID列表（用于级联删除）
     */
    @Select("SELECT id FROM comment WHERE parent_id = #{parentId} AND is_deleted = 0")
    List<Long> selectIdsByParentId(@Param("parentId") Long parentId);

    /**
     * 系统级强制逻辑删除（不需要校验userId，用于级联删除子评论）
     */
    @Update("UPDATE comment SET is_deleted = 1 WHERE id = #{id}")
    int deleteByIdSystem(@Param("id") Long id);
}
