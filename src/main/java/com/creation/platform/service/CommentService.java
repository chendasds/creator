package com.creation.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.creation.platform.dto.CommentDTO;
import com.creation.platform.entity.Comment;
import com.creation.platform.vo.CommentVO;

import java.util.List;

public interface CommentService extends IService<Comment> {

    /**
     * 发表评论/回复
     *
     * @param userId 当前登录用户ID
     * @param dto    评论信息
     * @return 新评论ID
     */
    Long addComment(Long userId, CommentDTO dto);

    /**
     * 获取作品评论树（楼中楼结构）
     *
     * @param artworkId 作品ID
     * @return 树形结构的评论列表
     */
    List<CommentVO> getCommentTree(Long artworkId);

    /**
     * 删除评论（逻辑删除）
     *
     * @param userId 当前登录用户ID
     * @param commentId 评论ID
     * @return 是否删除成功
     */
    boolean deleteComment(Long userId, Long commentId);
}
