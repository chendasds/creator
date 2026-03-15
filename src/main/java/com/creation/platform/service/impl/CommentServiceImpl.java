package com.creation.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creation.platform.entity.Comment;
import com.creation.platform.mapper.CommentMapper;
import com.creation.platform.service.CommentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Override
    public List<Comment> getCommentsByArtworkId(Long artworkId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getArtworkId, artworkId)
                .isNull(Comment::getParentId)
                .orderByDesc(Comment::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<Comment> getRepliesByParentId(Long parentId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getParentId, parentId)
                .orderByAsc(Comment::getCreateTime);
        return this.list(wrapper);
    }
}
