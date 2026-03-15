package com.creation.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creation.platform.entity.Tag;
import com.creation.platform.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tag")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping("/{id}")
    public Tag getById(@PathVariable Long id) {
        return tagService.getById(id);
    }

    @GetMapping("/list")
    public List<Tag> list() {
        return tagService.list();
    }

    @GetMapping("/page")
    public Page<Tag> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Tag> page = new Page<>(current, size);
        return tagService.page(page);
    }

    @PostMapping
    public boolean save(@RequestBody Tag tag) {
        return tagService.save(tag);
    }

    @PutMapping
    public boolean updateById(@RequestBody Tag tag) {
        return tagService.updateById(tag);
    }

    @DeleteMapping("/{id}")
    public boolean removeById(@PathVariable Long id) {
        return tagService.removeById(id);
    }
}
