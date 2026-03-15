package com.creation.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.creation.platform.entity.Comment;

import java.util.List;

public interface CommentService extends IService<Comment> {

    /**
     * 获取作品的所有评论
     */
    List<Comment> getCommentsByArtworkId(Long artworkId);

    /**
     * 获取评论的回复
     */
    List<Comment> getRepliesByParentId(Long parentId);
}
