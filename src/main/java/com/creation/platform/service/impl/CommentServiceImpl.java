package com.creation.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creation.platform.dto.CommentDTO;
import com.creation.platform.entity.Comment;
import com.creation.platform.mapper.CommentMapper;
import com.creation.platform.service.CommentService;
import com.creation.platform.vo.CommentVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Override
    public Long addComment(Long userId, CommentDTO dto) {
        Comment comment = new Comment();
        comment.setArtworkId(dto.getArtworkId());
        comment.setUserId(userId);
        comment.setContent(dto.getContent());
        comment.setParentId(dto.getParentId());
        this.save(comment);
        return comment.getId();
    }

    @Override
    public List<CommentVO> getCommentTree(Long artworkId) {
        List<CommentVO> allComments = baseMapper.selectCommentListWithUser(artworkId);

        if (allComments == null || allComments.isEmpty()) {
            return new ArrayList<>();
        }

        List<CommentVO> rootComments = new ArrayList<>();
        Map<Long, CommentVO> commentMap = new HashMap<>();

        for (CommentVO comment : allComments) {
            if (comment.getChildren() == null) {
                comment.setChildren(new ArrayList<>());
            }
            commentMap.put(comment.getId(), comment);
            if (comment.getParentId() == null || comment.getParentId() == 0) {
                rootComments.add(comment);
            }
        }

        for (CommentVO comment : allComments) {
            if (comment.getParentId() != null && comment.getParentId() != 0) {
                Long currentParentId = comment.getParentId();
                CommentVO root = null;

                while (currentParentId != null) {
                    CommentVO parent = commentMap.get(currentParentId);
                    if (parent == null) break;
                    if (parent.getParentId() == null || parent.getParentId() == 0) {
                        root = parent;
                        break;
                    }
                    currentParentId = parent.getParentId();
                }

                if (root != null) {
                    root.getChildren().add(comment);
                }
            }
        }

        return rootComments;
    }

    /**
     * 递归级联删除所有子评论
     */
    private void cascadeDeleteChildren(Long parentId) {
        List<Long> childIds = baseMapper.selectIdsByParentId(parentId);
        if (childIds != null && !childIds.isEmpty()) {
            for (Long childId : childIds) {
                baseMapper.deleteByIdSystem(childId);
                cascadeDeleteChildren(childId);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteComment(Long commentId, Long userId) {
        int rows = baseMapper.deleteCommentSafely(commentId, userId);
        if (rows == 0) {
            throw new RuntimeException("删除失败：评论不存在或无权删除");
        }
        cascadeDeleteChildren(commentId);
        return true;
    }
}
