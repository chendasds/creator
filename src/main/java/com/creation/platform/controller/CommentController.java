package com.creation.platform.controller;

import com.creation.platform.entity.Comment;
import com.creation.platform.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/{id}")
    public Comment getById(@PathVariable Long id) {
        return commentService.getById(id);
    }

    @GetMapping("/artwork/{artworkId}")
    public Map<String, Object> getCommentsByArtworkId(@PathVariable Long artworkId) {
        List<Comment> comments = commentService.getCommentsByArtworkId(artworkId);
        Map<String, Object> result = new HashMap<>();
        result.put("comments", comments);
        result.put("total", comments.size());
        return result;
    }

    @GetMapping("/replies/{parentId}")
    public List<Comment> getReplies(@PathVariable Long parentId) {
        return commentService.getRepliesByParentId(parentId);
    }

    @PostMapping
    public boolean save(@RequestBody Comment comment) {
        return commentService.save(comment);
    }

    @PutMapping
    public boolean updateById(@RequestBody Comment comment) {
        return commentService.updateById(comment);
    }

    @DeleteMapping("/{id}")
    public boolean removeById(@PathVariable Long id) {
        return commentService.removeById(id);
    }
}
