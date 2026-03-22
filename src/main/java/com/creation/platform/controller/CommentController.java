package com.creation.platform.controller;

import com.creation.platform.dto.CommentDTO;
import com.creation.platform.entity.Comment;
import com.creation.platform.entity.Result;
import com.creation.platform.service.CommentService;
import com.creation.platform.vo.CommentVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 发表评论或回复
     */
    @PostMapping("/add")
    public Result<Long> addComment(HttpServletRequest request, @RequestBody CommentDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        Long commentId = commentService.addComment(userId, dto);
        return Result.success(commentId);
    }

    /**
     * 获取作品评论树（楼中楼结构）
     */
    @GetMapping("/list")
    public Result<List<CommentVO>> getCommentTree(@RequestParam Long artworkId) {
        List<CommentVO> tree = commentService.getCommentTree(artworkId);
        return Result.success(tree);
    }

    @GetMapping("/detail/{id}")
    public Comment getById(@PathVariable Long id) {
        return commentService.getById(id);
    }

    @PutMapping
    public boolean updateById(@RequestBody Comment comment) {
        return commentService.updateById(comment);
    }

    /**
     * 删除评论（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(HttpServletRequest request, @PathVariable Long id) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            commentService.deleteComment(id, userId);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }
}
